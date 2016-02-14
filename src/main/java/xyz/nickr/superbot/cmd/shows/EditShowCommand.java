package xyz.nickr.superbot.cmd.shows;

import java.util.Arrays;

import xyz.nickr.superbot.Joiner;
import xyz.nickr.superbot.SuperBotShows;
import xyz.nickr.superbot.SuperBotShows.Show;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.cmd.Permission;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class EditShowCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{ "editshow" };
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[]{ "display/day/aliases [show name] [params]", "updates the display name, day, or aliases of a show" };
    }

    @Override
    public Permission perm() {
        return string("shows.edit");
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        Show show = args.length < 3 ? null : SuperBotShows.getShow(args[1]);
        if (show != null)
            show = show.clone();
        MessageBuilder<?> mb = sys.message();
        if (args.length < 3) {
            sendUsage(sys, user, group);
            return;
        } else if (show == null) {
            group.sendMessage(mb.escaped("I couldn't find a show with the name \"" + args[1] + "\""));
        } else if (args[0].equalsIgnoreCase("display")) {
            show.display = Joiner.join(" ", Arrays.copyOfRange(args, 2, args.length));
        } else if (args[0].equalsIgnoreCase("day")) {
            show.day = Joiner.join(" ", Arrays.copyOfRange(args, 2, args.length));
        } else if (args[0].equalsIgnoreCase("aliases")) {
            show.names = Arrays.copyOfRange(args, 2, args.length);
        } else {
            sendUsage(sys, user, group);
            return;
        }
        if (SuperBotShows.editShow(args[1], show)) {
            mb.italic(true).escaped("Successfully edited show:").italic(false);
            mb.bold(true).escaped("\nDisplay Name: ").bold(false).escaped(show.display);
            mb.bold(true).escaped("\nDay of the Week: ").bold(false).escaped(show.day);
            mb.bold(true).escaped("\nAliases: ").bold(false).escaped(Arrays.asList(show.names).toString());
            group.sendMessage(mb);
        } else {
            group.sendMessage(mb.escaped("Something went wrong."));
        }
    }

}
