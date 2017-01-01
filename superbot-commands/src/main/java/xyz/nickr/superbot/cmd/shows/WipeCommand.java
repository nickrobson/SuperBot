package xyz.nickr.superbot.cmd.shows;

import java.util.concurrent.atomic.AtomicBoolean;

import xyz.nickr.superbot.SuperBotResource;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.cmd.Permission;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class WipeCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "wipe", "clear" };
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] { "[user]", "wipe [user]'s progress" };
    }

    @Override
    public Permission perm() {
        return string("admin.wipe");
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length == 0) {
            sendUsage(sys, user, group);
            return;
        }
        String toRemove = args[0];
        AtomicBoolean wiped = SuperBotResource.wipe(toRemove);
        if (wiped.get()) {
            group.sendMessage(sys.message().escaped("Wiped " + toRemove));
            SuperBotResource.saveProgress();
        } else {
            group.sendMessage(sys.message().escaped("No data to wipe on " + toRemove));
        }
    }

}
