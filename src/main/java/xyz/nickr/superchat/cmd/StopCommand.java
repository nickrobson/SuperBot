package xyz.nickr.superchat.cmd;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import xyz.nickr.superchat.SuperChatController;

public class StopCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "stop", "restart", "kys" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
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
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        if (used.equalsIgnoreCase("kys"))
            group.sendMessage(encode("ded"));
        else
            group.sendMessage(encode("Goodbye!"));
        SuperChatController.saveProgress();
        System.exit(0);
    }

}
