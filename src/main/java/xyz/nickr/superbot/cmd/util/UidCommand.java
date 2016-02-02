package xyz.nickr.superbot.cmd.util;

import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

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
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        group.sendMessage(sys.message().escaped("This group's longId is: ").bold(true).escaped(group.getUniqueId()));
    }

    @Override
    public boolean alwaysEnabled() {
        return true;
    }

}
