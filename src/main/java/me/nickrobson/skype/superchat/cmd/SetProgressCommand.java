package me.nickrobson.skype.superchat.cmd;

import java.util.Map;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.SuperChatController;
import me.nickrobson.skype.superchat.SuperChatShows;
import me.nickrobson.skype.superchat.SuperChatShows.Show;

public class SetProgressCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "me", "setprg" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[] { "[show] [episode]", "set your progress on [show] to [episode]" };
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        if (args.length < 2) {
            group.sendMessage(encode("Incorrect usage: `~me [show] [episode]`"));
        } else {
            Show show = SuperChatShows.getShow(args[0]);
            String ep = args[1].toUpperCase();

            if (show == null) {
                group.sendMessage(encode("Invalid show name: " + args[0]));
            } else if (ep.equalsIgnoreCase("none") || ep.equalsIgnoreCase("remove")) {
                Map<String, String> prg = SuperChatController.getProgress(show);
                prg.remove(user.getUsername());
                SuperChatController.PROGRESS.put(show.getMainName(), prg);
                group.sendMessage(encode("Removed " + user.getDisplayName().orElse(user.getUsername()) + "'s progress on ")
                        + bold(encode(show.getDisplay())));
                SuperChatController.saveProgress();
            } else if (!SuperChatShows.EPISODE_PATTERN.matcher(ep).matches()) {
                group.sendMessage(encode("Invalid episode: " + ep + " (doesn't match SxEyy format)"));
            } else {
                Map<String, String> prg = SuperChatController.getProgress(show);
                prg.put(user.getUsername(), ep);
                SuperChatController.PROGRESS.put(show.getMainName(), prg);
                group.sendMessage(encode("Set " + user.getDisplayName().orElse(user.getUsername()) + "'s progress on ")
                        + bold(encode(show.getDisplay())) + encode(" to " + ep));
                SuperChatController.saveProgress();
            }
        }
    }

}
