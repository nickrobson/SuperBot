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
        return new String[] {"version"};
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] {"", "shows version info"};
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        MessageBuilder builder = sys.message();
        builder.bold(true).escaped("Version: ").bold(false).escaped(SuperBotController.VERSION).newLine();
        builder.bold(true).escaped("Build Number: ").bold(false);
        int build = SuperBotController.BUILD_NUMBER;
        if (build > 0) {
            builder.escaped(Integer.toString(build));
        } else {
            builder.escaped("Unknown");
        }
        if (SuperBotController.GIT_COMMIT_IDS.length > 0) {
            builder.newLine().bold(true).escaped("Commit Summary:").bold(false);
        }
        for (int i = 0; i < SuperBotController.GIT_COMMIT_IDS.length; i++) {
            String id = SuperBotController.GIT_COMMIT_IDS[i];
            String msg = SuperBotController.GIT_COMMIT_MESSAGES[i];
            String author = SuperBotController.GIT_COMMIT_AUTHORS[i];
            builder.newLine().italic(true).escaped(author + " ").italic(false).escaped(msg + " ").link("https://github.com/nickrobson/SuperBot/commit/" + id, id.substring(0, 8)).build();
        }
        group.sendMessage(builder);
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
