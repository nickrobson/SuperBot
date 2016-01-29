package xyz.nickr.superchat.cmd.shows;

import java.util.concurrent.atomic.AtomicBoolean;

import xyz.nickr.superchat.SuperChatController;
import xyz.nickr.superchat.cmd.Command;
import xyz.nickr.superchat.cmd.Permission;
import xyz.nickr.superchat.sys.Group;
import xyz.nickr.superchat.sys.Message;
import xyz.nickr.superchat.sys.Sys;
import xyz.nickr.superchat.sys.User;

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
    public void exec(Sys sys, User user, Group conv, String used, String[] args, Message message) {
        if (args.length == 0) {
            sendUsage(null, user, conv);
            return;
        }
        String toRemove = args[0];
        AtomicBoolean wiped = SuperChatController.wipe(toRemove);
        if (wiped.get()) {
            conv.sendMessage(sys.message().text("Wiped " + toRemove));
            SuperChatController.saveProgress();
        } else {
            conv.sendMessage(sys.message().text("No data to wipe on " + toRemove));
        }
    }

}
