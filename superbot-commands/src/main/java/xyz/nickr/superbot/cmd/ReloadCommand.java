package xyz.nickr.superbot.cmd;

import xyz.nickr.superbot.SuperBotResource;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class ReloadCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"reload"};
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] {"", "reloads the bot"};
    }

    @Override
    public Permission perm() {
        return this.string("admin.reload");
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
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        Message msg = group.sendMessage(sys.message().escaped(" "));
        SuperBotResource.saveProgress();
        SuperBotResource.savePermissions();
        new Thread(() -> {
            SuperBotResource.load(s -> {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                msg.edit(sys.message().escaped("Reloading... " + s));
            });
        }, "Reload Thread").start();
    }

}
