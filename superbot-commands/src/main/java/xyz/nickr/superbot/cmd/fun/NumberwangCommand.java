package xyz.nickr.superbot.cmd.fun;

import java.util.Random;

import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class NumberwangCommand implements Command {

    private final Random random = new Random();

    @Override
    public String[] names() {
        return new String[]{ "numberwang" };
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[]{ "[number]", "guess a number" };
    }

    @Override
    public boolean alwaysEnabled() {
        return true;
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length == 0)
            sendUsage(sys, user, group);
        else if (random.nextInt(8) == random.nextInt(8))
            group.sendMessage(sys.message().escaped("That's numberwang!"));
        else
            group.sendMessage(sys.message().escaped("Sorry, that's not numberwang!"));
    }

}
