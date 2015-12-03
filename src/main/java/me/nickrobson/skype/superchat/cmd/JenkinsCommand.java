package me.nickrobson.skype.superchat.cmd;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.MessageBuilder;

public class JenkinsCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{ "jenkins" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[]{ "", "gets a link to the jenkins" };
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        group.sendMessage(new MessageBuilder().link("http://ci.nickr.xyz/view/SuperChat/").text("Click here for the Jenkins").link(null).build());
    }

}
