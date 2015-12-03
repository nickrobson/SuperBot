package me.nickrobson.skype.superchat.cmd;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.MessageBuilder;
import me.nickrobson.skype.superchat.SuperChatController;

public class VersionCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{ "version" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[]{ "", "" };
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        MessageBuilder builder = new MessageBuilder().bold(true).text("Build Number:").bold(false);
        int build = SuperChatController.BUILD_NUMBER;
        if (build > 0)
            builder.text(Integer.toString(build));
        else
            builder.text("Unknown");
        group.sendMessage(builder.toString());
    }

}
