package me.nickrobson.skype.superchat.cmd;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;

public class GidCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "gid" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[] { "", "gets the group's longId" };
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        group.sendMessage(encode("This group's longId is: ") + bold(encode(group.getLongId())));
    }

    @Override
    public boolean alwaysEnabled() {
        return true;
    }

}
