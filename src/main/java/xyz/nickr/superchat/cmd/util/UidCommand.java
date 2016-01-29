package xyz.nickr.superchat.cmd.util;

import xyz.nickr.superchat.cmd.Command;
import xyz.nickr.superchat.sys.Group;
import xyz.nickr.superchat.sys.Message;
import xyz.nickr.superchat.sys.Sys;
import xyz.nickr.superchat.sys.User;

public class UidCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "uid", "gid" };
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] { "", "gets the group's unique id" };
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public void exec(Sys sys, User user, Group conv, String used, String[] args, Message message) {
        conv.sendMessage(sys.message().text("This group's longId is: ").bold(true).text(conv.getUniqueId()));
    }

    @Override
    public boolean alwaysEnabled() {
        return true;
    }

}
