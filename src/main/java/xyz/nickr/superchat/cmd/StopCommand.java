package xyz.nickr.superchat.cmd;

import xyz.nickr.superchat.SuperChatController;
import xyz.nickr.superchat.sys.Group;
import xyz.nickr.superchat.sys.Message;
import xyz.nickr.superchat.sys.Sys;
import xyz.nickr.superchat.sys.User;

public class StopCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "stop", "restart", "kys" };
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] { "", "stops the bot (restarting through a script)" };
    }

    @Override
    public Permission perm() {
        return string("admin.stop");
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public boolean alwaysEnabled() {
        return true;
    }

    @Override
    public void exec(Sys sys, User user, Group conv, String used, String[] args, Message message) {
        if (used.equalsIgnoreCase("kys"))
            conv.sendMessage(sys.message().text("ded"));
        else
            conv.sendMessage(sys.message().text("Goodbye!"));
        SuperChatController.saveProgress();
        System.exit(0);
    }

}
