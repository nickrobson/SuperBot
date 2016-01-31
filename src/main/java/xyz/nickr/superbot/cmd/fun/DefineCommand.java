package xyz.nickr.superbot.cmd.fun;

import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class DefineCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{ "define" };
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[]{ "[phrase]", "defines [phrase]" };
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        group.sendMessage(sys.message().text("Do I look like a fucking dictionary to you?"));
    }

}
