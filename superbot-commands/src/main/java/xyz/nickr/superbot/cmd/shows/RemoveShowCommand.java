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
        return new String[] {"delshow"};
    }

    @Override
    public Permission perm() {
        return this.string("shows.remove");
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] {"[cmd]", "Remove a cmd from its associated show"};
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length == 0) {
            this.sendUsage(sys, user, group);
        } else {
            MessageBuilder mb = sys.message();
            Show show = SuperBotShows.getShow(args[0]);
            if (show != null) {
                if (SuperBotShows.removeLink(args[0])) {
                    group.sendMessage(mb.bold(true).escaped("Removed cmd (" + args[0] + ") from: ").bold(false).escaped(show.getDisplay()));
                } else {
                    group.sendMessage(mb.escaped("Something went wrong."));
                }
            } else {
                group.sendMessage(mb.escaped("I couldn't find a show with the name \"" + args[0] + "\""));
            }
        }
    }
}
