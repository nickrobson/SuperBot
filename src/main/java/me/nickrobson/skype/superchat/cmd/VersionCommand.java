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
        return new String[]{ "", "shows version info" };
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        MessageBuilder builder = new MessageBuilder();
        builder.bold(true).text("Version: ").bold(false).text(SuperChatController.VERSION).newLine();
        builder.bold(true).text("Build Number: ").bold(false);
        int build = SuperChatController.BUILD_NUMBER;
        if (build > 0)
            builder.text(Integer.toString(build));
        else
            builder.text("Unknown");
        if (SuperChatController.GIT_COMMIT_ID.length > 0)
            builder.newLine().bold(true).text("Commit Summary:").bold(false);
        for (int i = 0; i < SuperChatController.GIT_COMMIT_ID.length; i++) {
            String id = SuperChatController.GIT_COMMIT_ID[i].substring(0, 8);
            String msg = SuperChatController.GIT_COMMIT_MESSAGE[i];
            String author = SuperChatController.GIT_COMMIT_AUTHORS[i];
            String txt = new MessageBuilder()
                    .bold(true).text(author).bold(false)
                    .text(msg)
                    .italic(true).text(" (" + id + ")").italic(false)
                    .build();
            builder.newLine().html(txt);
        }
        group.sendMessage(builder.toString());
    }

    @Override
    public boolean alwaysEnabled() {
        return true;
    }

    @Override
    public boolean userchat() {
        return true;
    }

}
