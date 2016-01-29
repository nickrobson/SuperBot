package xyz.nickr.superchat.cmd;

import xyz.nickr.superchat.SuperChatController;
import xyz.nickr.superchat.sys.Group;
import xyz.nickr.superchat.sys.Message;
import xyz.nickr.superchat.sys.Sys;
import xyz.nickr.superchat.sys.User;

public class ReloadCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "reload" };
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] { "", "reloads the bot" };
    }

    @Override
    public Permission perm() {
        return string("admin.reload");
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
        Message msg = conv.sendMessage(sys.message().text(" "));
        SuperChatController.saveProgress();
        SuperChatController.savePermissions();
        new Thread(() -> {
            SuperChatController.load(s -> {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                msg.edit("Reloading... " + s);
            });
        }, "Reload Thread").start();
    }

}
