package xyz.nickr.superchat.cmd.shows;

import java.util.Arrays;
import java.util.Optional;

import xyz.nickr.superchat.SuperChatShows;
import xyz.nickr.superchat.SuperChatShows.Show;
import xyz.nickr.superchat.cmd.Command;
import xyz.nickr.superchat.cmd.Permission;
import xyz.nickr.superchat.sys.Group;
import xyz.nickr.superchat.sys.Message;
import xyz.nickr.superchat.sys.MessageBuilder;
import xyz.nickr.superchat.sys.Sys;
import xyz.nickr.superchat.sys.User;

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
    public void exec(Sys sys, User user, Group conv, String used, String[] args, Message message) {
        if (args.length < 3) {
            sendUsage(null, user, conv);
        } else {
            String showName = args[0].replace("___", " "), day = args[1];
            String[] aliases = new String[args.length-2];
            for (int i = 2; i < args.length; i++)
                aliases[i-2] = args[i].toLowerCase();
            Optional<Show> opt = Arrays.stream(aliases)
                                    .map(s -> SuperChatShows.getShow(s))
                                    .filter(s -> s != null)
                                    .findAny();
            SuperChatShows.Show show = opt.orElse(null);
            MessageBuilder<?> mb = sys.message();
            if (show == null) {
                show = new SuperChatShows.Show(showName, day, aliases);
                if (SuperChatShows.addShow(show)) {
                    mb.italic(true).text("Let's review the info:").italic(false);
                    mb.bold(true).text("\nDisplay Name: ").bold(false).text(show.display);
                    mb.bold(true).text("\nDay of the Week: ").bold(false).text(show.day);
                    mb.bold(true).text("\nAliases: ").bold(false).text(Arrays.asList(show.names).toString());
                    conv.sendMessage(mb);
                } else {
                    conv.sendMessage(mb.text("Something went wrong."));
                }
            } else {
                conv.sendMessage(mb.text("A show already exists with the name \"" + showName + "\""));
            }
        }
    }

}
