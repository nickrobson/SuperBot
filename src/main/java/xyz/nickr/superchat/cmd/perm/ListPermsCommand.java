package xyz.nickr.superchat.cmd.perm;

import java.util.Set;

import xyz.nickr.superchat.Joiner;
import xyz.nickr.superchat.SuperChatPermissions;
import xyz.nickr.superchat.cmd.Command;
import xyz.nickr.superchat.sys.Group;
import xyz.nickr.superchat.sys.Message;
import xyz.nickr.superchat.sys.MessageBuilder;
import xyz.nickr.superchat.sys.Sys;
import xyz.nickr.superchat.sys.User;

public class ListPermsCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{ "listperms", "showperms" };
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return userchat ? new String[]{ "", "shows your permissions" } : new String[]{ "[username]", "shows [username]'s permissions" };
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public void exec(Sys sys, User user, Group conv, String used, String[] args, Message message) {
        String username = args.length == 0 ? user.getUsername() : args[0];
        Set<String> perms = SuperChatPermissions.get(username);
        MessageBuilder<?> mb = sys.message();
        if (perms.isEmpty()) {
            mb.bold(true).text(username + " has no permissions.").bold(false);
        } else {
            mb.bold(true).text(username + " has the following permissions:").bold(false).newLine();
            mb.text(Joiner.join(", ", perms));
        }
        conv.sendMessage(mb.toString());
    }

}
