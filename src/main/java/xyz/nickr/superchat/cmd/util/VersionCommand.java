package xyz.nickr.superchat.cmd.util;

import xyz.nickr.superchat.SuperChatController;
import xyz.nickr.superchat.cmd.Command;
import xyz.nickr.superchat.sys.Group;
import xyz.nickr.superchat.sys.Message;
import xyz.nickr.superchat.sys.MessageBuilder;
import xyz.nickr.superchat.sys.Sys;
import xyz.nickr.superchat.sys.User;

public class VersionCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "version" };
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] { "", "shows version info" };
    }

    @Override
    public void exec(Sys sys, User user, Group conv, String used, String[] args, Message message) {
        MessageBuilder<?> builder = sys.message();
        builder.bold(true).text("Version: ").bold(false).text(SuperChatController.VERSION).newLine();
        builder.bold(true).text("Build Number: ").bold(false);
        int build = SuperChatController.BUILD_NUMBER;
        if (build > 0)
            builder.text(Integer.toString(build));
        else
            builder.text("Unknown");
        if (SuperChatController.GIT_COMMIT_IDS.length > 0)
            builder.newLine().bold(true).text("Commit Summary:").bold(false);
        for (int i = 0; i < SuperChatController.GIT_COMMIT_IDS.length; i++) {
            String id = SuperChatController.GIT_COMMIT_IDS[i].substring(0, 8);
            String msg = SuperChatController.GIT_COMMIT_MESSAGES[i];
            String author = SuperChatController.GIT_COMMIT_AUTHORS[i];
            builder.newLine().italic(true).text(author + " ").italic(false).text(msg).italic(true).text(" (" + id + ")").italic(false).build();
        }
        conv.sendMessage(builder.toString());
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
