package xyz.nickr.superbot.cmd.util;

import xyz.nickr.mathos.Mathos;
import xyz.nickr.superbot.Joiner;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class MathsCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{ "maths", "math" };
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[]{ "[maths]", "interprets maths for you" };
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length == 0) {
            sendUsage(sys, user, group);
            return;
        }
        MessageBuilder<?> mb = sys.message();
        try {
            String input = Joiner.join(" ", args);
            mb.escaped("[Maths] Query: " + input).newLine();
            mb.escaped("[Maths] Result: " + Mathos.value(input));
            group.sendMessage(mb);
        } catch (Exception ex) {
            group.sendMessage(mb.escaped(ex.getClass().getSimpleName() + ": " + ex.getMessage()));
        }
    }

    @Override
    public boolean alwaysEnabled() {
        return true;
    }

    @Override
    public boolean userchat() {
        return true;
    }

}
