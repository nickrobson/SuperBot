package me.nickrobson.skype.superchat.cmd;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.MessageBuilder;

public class GitCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "git" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[] { "", "tells you the bot's git repo" };
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
        group.sendMessage(new MessageBuilder().link("http://github.com/nickrobson/SuperChat").html("http://github.com/nickrobson/SuperChat").build());
    }

}
