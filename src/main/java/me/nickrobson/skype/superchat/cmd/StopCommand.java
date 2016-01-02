package me.nickrobson.skype.superchat.cmd;

import in.kyle.ezskypeezlife.api.SkypeUserRole;
import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.SuperChatController;

public class StopCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "stop", "restart" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[] { "", "stops the bot (restarting through a script)" };
    }

    @Override
    public SkypeUserRole role() {
        return SkypeUserRole.ADMIN;
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
        group.sendMessage(encode("Goodbye!"));
        SuperChatController.saveProgress();
        System.exit(0);
    }

}
