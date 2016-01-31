package xyz.nickr.superbot.cmd.shows;

import java.util.Map;

import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.SuperBotShows;
import xyz.nickr.superbot.SuperBotShows.Show;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

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
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length < 2) {
            sendUsage(sys, user, group);
        } else {
            Show show = SuperBotShows.getShow(args[0]);
            String ep = args[1].toUpperCase();

            MessageBuilder<?> mb = sys.message();
            if (show == null) {
                group.sendMessage(mb.text("Invalid show name: " + args[0]));
            } else if (ep.equalsIgnoreCase("none") || ep.equalsIgnoreCase("remove")) {
                Map<String, String> prg = SuperBotController.getProgress(show);
                prg.remove(user.getUsername());
                SuperBotController.PROGRESS.put(show.getMainName(), prg);
                group.sendMessage(mb.text("Removed " + user.getDisplayName().orElse(user.getUsername()) + "'s progress on ").bold(true).text(show.getDisplay()));
                SuperBotController.saveProgress();
            } else if (!SuperBotShows.EPISODE_PATTERN.matcher(ep).matches()) {
                group.sendMessage(mb.text("Invalid episode: " + ep + " (doesn't match SxEyy format)"));
            } else {
                Map<String, String> prg = SuperBotController.getProgress(show);
                prg.put(user.getUsername(), ep);
                SuperBotController.PROGRESS.put(show.getMainName(), prg);
                group.sendMessage(mb.text("Set " + user.getDisplayName().orElse(user.getUsername()) + "'s progress on ").bold(true).text(show.getDisplay()).bold(false).text(" to " + ep));
                SuperBotController.saveProgress();
            }
        }
    }

}
