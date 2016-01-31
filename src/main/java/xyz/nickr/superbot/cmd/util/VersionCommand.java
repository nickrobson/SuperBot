package xyz.nickr.superbot.cmd.util;

import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

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
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        MessageBuilder<?> builder = sys.message();
        builder.bold(true).text("Version: ").bold(false).text(SuperBotController.VERSION).newLine();
        builder.bold(true).text("Build Number: ").bold(false);
        int build = SuperBotController.BUILD_NUMBER;
        if (build > 0)
            builder.text(Integer.toString(build));
        else
            builder.text("Unknown");
        if (SuperBotController.GIT_COMMIT_IDS.length > 0)
            builder.newLine().bold(true).text("Commit Summary:").bold(false);
        for (int i = 0; i < SuperBotController.GIT_COMMIT_IDS.length; i++) {
            String id = SuperBotController.GIT_COMMIT_IDS[i].substring(0, 8);
            String msg = SuperBotController.GIT_COMMIT_MESSAGES[i];
            String author = SuperBotController.GIT_COMMIT_AUTHORS[i];
            builder.newLine().italic(true).text(author + " ").italic(false).text(msg).italic(true).text(" (" + id + ")").italic(false).build();
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
