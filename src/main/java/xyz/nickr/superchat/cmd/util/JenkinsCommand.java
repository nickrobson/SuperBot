package xyz.nickr.superchat.cmd.util;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import xyz.nickr.superchat.MessageBuilder;
import xyz.nickr.superchat.cmd.Command;

public class JenkinsCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "jenkins" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[] { "", "tells you the bot's jenkins" };
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
        group.sendMessage(new MessageBuilder().link("http://ci.nickr.xyz/view/SuperChat/").text("http://ci.nickr.xyz/view/SuperChat/").build());
    }

}
