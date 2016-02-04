package xyz.nickr.superbot.cmd.shows;

import java.util.Map;
import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.SuperBotShows;
import xyz.nickr.superbot.SuperBotShows.Show;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Profile;
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
            String prefix = sys.prefix();
            Profile profile = user.getProfile().orElse(null);
            if (profile == null) {
                group.sendMessage("You need a profile to use this. Use " + prefix + "createprofile.");
                return;
            }
            String profileName = profile.getName();
            Show show = SuperBotShows.getShow(args[0]);
            String ep = args[1].toUpperCase();
            MessageBuilder<?> mb = sys.message();
            if (show == null) {
                group.sendMessage(mb.escaped("Invalid show name: ").bold(true).escaped(args[0]).bold(false));
            } else if (ep.equalsIgnoreCase("none") || ep.equalsIgnoreCase("remove")) {
                Map<String, String> prg = SuperBotController.getProgress(show);
                prg.remove(profileName);
                SuperBotController.PROGRESS.put(show.getMainName(), prg);
                group.sendMessage(mb.escaped("Removed ").bold(true).escaped(profileName).bold(false).escaped("'s progress on ").bold(true).escaped(show.getDisplay()));
                SuperBotController.saveProgress();
            } else if (!SuperBotShows.EPISODE_PATTERN.matcher(ep).matches()) {
                group.sendMessage(mb.escaped("Invalid episode: ").bold(true).escaped(ep).bold(false).escaped(" (doesn't match SxEyy format)"));
            } else {
                Map<String, String> prg = SuperBotController.getProgress(show);
                prg.put(profileName, ep);
                SuperBotController.PROGRESS.put(show.getMainName(), prg);
                group.sendMessage(mb.escaped("Set ").bold(true).escaped(profileName).bold(false).escaped("'s progress on ").bold(true).escaped(show.getDisplay()).bold(false).escaped(" to " + ep));
                SuperBotController.saveProgress();
            }
        }
    }

}
