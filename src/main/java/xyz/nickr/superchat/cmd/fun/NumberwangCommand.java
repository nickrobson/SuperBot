package xyz.nickr.superchat.cmd.fun;

import java.util.Random;

import xyz.nickr.superchat.cmd.Command;
import xyz.nickr.superchat.sys.Group;
import xyz.nickr.superchat.sys.Message;
import xyz.nickr.superchat.sys.Sys;
import xyz.nickr.superchat.sys.User;

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
    public void exec(Sys sys, User user, Group conv, String used, String[] args, Message message) {
        if (args.length == 0)
            sendUsage(null, user, conv);
        else if (random.nextInt(8) == random.nextInt(8))
            conv.sendMessage(sys.message().text("That's numberwang!"));
        else
            conv.sendMessage(sys.message().text("Sorry, that's not numberwang!"));
    }

}
