package me.nickrobson.skype.superchat.cmd.fun;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.cmd.Command;

public class DefineCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{ "define" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userchat) {
        return new String[]{ "[phrase]", "defines [phrase]" };
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        group.sendMessage(encode("Do I look like a fucking dictionary to you?"));
    }

}
