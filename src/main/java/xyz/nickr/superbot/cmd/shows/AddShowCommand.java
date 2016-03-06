package xyz.nickr.superbot.cmd.shows;

import java.util.Arrays;
import java.util.Optional;

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
public class AddShowCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "addshow" };
    }

    @Override
    public Permission perm() {
        return string("shows.add");
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] { "[display] [day] [aliases...]", "add a new show to the list" };
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length < 3) {
            sendUsage(sys, user, group);
        } else {
            String showName = args[0].replace("___", " "), day = args[1];
            String[] aliases = new String[args.length-2];
            for (int i = 2; i < args.length; i++)
                aliases[i-2] = args[i].toLowerCase();
            Optional<Show> opt = Arrays.stream(aliases)
                                    .map(s -> SuperBotShows.getShow(s))
                                    .filter(s -> s != null)
                                    .findAny();
            SuperBotShows.Show show = opt.orElse(null);
            MessageBuilder<?> mb = sys.message();
            if (show == null) {
                show = new SuperBotShows.Show(showName, day, aliases);
                if (SuperBotShows.addShow(show)) {
                    mb.italic(true).escaped("Let's review the info:").italic(false);
                    mb.newLine().bold(true).escaped("Display Name: ").bold(false).escaped(show.display);
                    mb.newLine().bold(true).escaped("Day of the Week: ").bold(false).escaped(show.day);
                    mb.newLine().bold(true).escaped("Aliases: ").bold(false).escaped(Arrays.asList(show.names).toString());
                    group.sendMessage(mb);
                } else {
                    group.sendMessage(mb.escaped("Something went wrong."));
                }
            } else {
                group.sendMessage(mb.escaped("A show already exists with the name \"" + showName + "\""));
            }
        }
    }

}
