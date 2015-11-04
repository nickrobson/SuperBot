package me.nickrobson.skype.superchat.cmd;

import java.util.Map;

import me.nickrobson.skype.superchat.SuperChatController;
import me.nickrobson.skype.superchat.SuperChatShows;
import me.nickrobson.skype.superchat.SuperChatShows.Show;
import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.message.Message;
import xyz.gghost.jskype.user.GroupUser;

public class SetProgressCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "me", "setprg" };
    }

    @Override
    public String[] help(GroupUser user, boolean userChat) {
        return new String[] { "[show] [episode]", "set your progress on [show] to [episode]" };
    }

    @Override
    public void exec(GroupUser user, Group group, String used, String[] args, Message message) {
        if (args.length < 2) {
            sendMessage(group, encode("Incorrect usage: `~me [show] [episode]`"));
        } else {
            Show show = SuperChatShows.getShow(args[0]);
            String ep = args[1].toUpperCase();

            if (show == null) {
                sendMessage(group, encode("Invalid show name: " + args[0]));
            } else if (ep.equalsIgnoreCase("none") || ep.equalsIgnoreCase("remove")) {
                Map<String, String> prg = SuperChatController.getProgress(show);
                prg.remove(user.getUser().getUsername());
                SuperChatController.PROGRESS.put(show.getMainName(), prg);
                sendMessage(group, encode("Removed " + user.getUser().getDisplayName() + "'s progress on ") + bold(encode(show.getDisplay())));
                SuperChatController.saveProgress();
            } else if (!SuperChatShows.EPISODE_PATTERN.matcher(ep).matches()) {
                sendMessage(group, encode("Invalid episode: " + ep + " (doesn't match SxEyy format)"));
            } else {
                Map<String, String> prg = SuperChatController.getProgress(show);
                prg.put(user.getUser().getUsername(), ep);
                SuperChatController.PROGRESS.put(show.getMainName(), prg);
                sendMessage(group, encode("Set " + user.getUser().getDisplayName() + "'s progress on ") + bold(encode(show.getDisplay())) + encode(" to " + ep));
                SuperChatController.saveProgress();
            }
        }
    }

}
