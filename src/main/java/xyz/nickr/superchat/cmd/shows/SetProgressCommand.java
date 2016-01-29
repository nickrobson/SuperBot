package xyz.nickr.superchat.cmd.shows;

import java.util.Map;

import xyz.nickr.superchat.SuperChatController;
import xyz.nickr.superchat.SuperChatShows;
import xyz.nickr.superchat.SuperChatShows.Show;
import xyz.nickr.superchat.cmd.Command;
import xyz.nickr.superchat.sys.Group;
import xyz.nickr.superchat.sys.Message;
import xyz.nickr.superchat.sys.MessageBuilder;
import xyz.nickr.superchat.sys.Sys;
import xyz.nickr.superchat.sys.User;

public class SetProgressCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "me", "setprg" };
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] { "[show] [episode]", "set your progress on [show] to [episode]" };
    }

    @Override
    public void exec(Sys sys, User user, Group conv, String used, String[] args, Message message) {
        if (args.length < 2) {
            sendUsage(null, user, conv);
        } else {
            Show show = SuperChatShows.getShow(args[0]);
            String ep = args[1].toUpperCase();

            MessageBuilder<?> mb = sys.message();
            if (show == null) {
                conv.sendMessage(mb.text("Invalid show name: " + args[0]));
            } else if (ep.equalsIgnoreCase("none") || ep.equalsIgnoreCase("remove")) {
                Map<String, String> prg = SuperChatController.getProgress(show);
                prg.remove(user.getUsername());
                SuperChatController.PROGRESS.put(show.getMainName(), prg);
                conv.sendMessage(mb.text("Removed " + user.getDisplayName().orElse(user.getUsername()) + "'s progress on ").bold(true).text(show.getDisplay()));
                SuperChatController.saveProgress();
            } else if (!SuperChatShows.EPISODE_PATTERN.matcher(ep).matches()) {
                conv.sendMessage(mb.text("Invalid episode: " + ep + " (doesn't match SxEyy format)"));
            } else {
                Map<String, String> prg = SuperChatController.getProgress(show);
                prg.put(user.getUsername(), ep);
                SuperChatController.PROGRESS.put(show.getMainName(), prg);
                conv.sendMessage(mb.text("Set " + user.getDisplayName().orElse(user.getUsername()) + "'s progress on ").bold(true).text(show.getDisplay()).bold(false).text(" to " + ep));
                SuperChatController.saveProgress();
            }
        }
    }

}
