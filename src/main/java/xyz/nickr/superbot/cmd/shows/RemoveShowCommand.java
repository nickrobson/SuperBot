package xyz.nickr.superbot.cmd.shows;

import xyz.nickr.superbot.SuperBotShows;
import xyz.nickr.superbot.SuperBotShows.Show;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.cmd.Permission;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

/**
 * Created by Horrgs on 1/1/2016.
 *
 * @author Horrgs
 * @author Nick Robson
 */
public class RemoveShowCommand implements Command {
    @Override
    public String[] names() {
        return new String[] { "removeshow" };
    }

    @Override
    public Permission perm() {
        return string("shows.remove");
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] { "[show]", "Remove a show from the list" };
    }

    @Override
    public void exec(Sys sys, User user, Group conv, String used, String[] args, Message message) {
        if (args.length == 0) {
            sendUsage(null, user, conv);
        } else {
            MessageBuilder<?> mb = sys.message();
            Show show = SuperBotShows.getShow(args[0]);
            if (show != null) {
                if (SuperBotShows.removeShow(args[0])) {
                    conv.sendMessage(mb.bold(true).text("Removed show: ").bold(false).text(show.display));
                } else {
                    conv.sendMessage(mb.text("Something went wrong."));
                }
            } else {
                conv.sendMessage(mb.text("I couldn't find a show with the name \"" + args[0] + "\""));
            }
        }
    }
}
