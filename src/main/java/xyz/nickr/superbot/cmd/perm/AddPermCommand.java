package xyz.nickr.superbot.cmd.perm;

import xyz.nickr.superbot.SuperBotPermissions;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.cmd.Permission;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class AddPermCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{ "addperm" };
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[]{ "[username] [perm]", "gives [perm] to [username]" };
    }

    @Override
    public Permission perm() {
        return string("permissions.modify");
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public void exec(Sys sys, User user, Group conv, String used, String[] args, Message message) {
        if (args.length < 2) {
            sendUsage(null, user, conv);
        } else {
            MessageBuilder<?> mb = sys.message().bold(true).text(args[0]).bold(false);
            if (SuperBotPermissions.set(args[0], args[1], true)) {
                mb.text(" now has: ");
            } else {
                mb.text(" already has: ");
            }
            conv.sendMessage(mb.bold(true).text(args[1]).bold(false));
        }
    }

}
