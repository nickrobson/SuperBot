package xyz.nickr.superbot.cmd.perm;

import xyz.nickr.superbot.SuperBotPermissions;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.cmd.Permission;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Profile;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class DelPermCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{ "delperm" };
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[]{ "[profile] [perm]", "removes [perm] from [profile]" };
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
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length < 2) {
            sendUsage(sys, user, group);
        } else {
            if (!Profile.getProfile(args[0]).isPresent()) {
                group.sendMessage(sys.message().text("No profile with name = " + args[0].toLowerCase()));
                return;
            }
            MessageBuilder<?> mb = sys.message().bold(true).text(args[0]).bold(false);
            if (SuperBotPermissions.set(args[0], args[1], false)) {
                mb.text(" no longer has: ");
            } else {
                mb.text(" doesn't have: ");
            }
            group.sendMessage(mb.bold(true).text(args[1]).bold(false));
        }
    }

}
