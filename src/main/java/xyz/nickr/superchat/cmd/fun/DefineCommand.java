package xyz.nickr.superchat.cmd.fun;

import xyz.nickr.superchat.cmd.Command;
import xyz.nickr.superchat.sys.Group;
import xyz.nickr.superchat.sys.Message;
import xyz.nickr.superchat.sys.Sys;
import xyz.nickr.superchat.sys.User;

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
    public void exec(Sys sys, User user, Group conv, String used, String[] args, Message message) {
        conv.sendMessage(sys.message().text("Do I look like a fucking dictionary to you?"));
    }

}
