package xyz.nickr.superbot.cmd.util;

import xyz.nickr.superbot.SuperBotResource;
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
        builder.bold(true).escaped("Version:").bold(false).escaped(" " + SuperBotResource.VERSION).newLine();
        builder.bold(true).escaped("Build Number:").bold(false).escaped(" ");
        int build = SuperBotResource.BUILD_NUMBER;
        if (build > 0) {
            builder.escaped(Integer.toString(build));
        } else {
            builder.escaped("Unknown");
        }
        if (SuperBotResource.GIT_COMMIT_IDS.length > 0) {
            builder.newLine().bold(true).escaped("Commit Summary:").bold(false);
        }
        for (int i = 0; i < SuperBotResource.GIT_COMMIT_IDS.length; i++) {
            String id = SuperBotResource.GIT_COMMIT_IDS[i];
            String msg = SuperBotResource.GIT_COMMIT_MESSAGES[i];
            String author = SuperBotResource.GIT_COMMIT_AUTHORS[i];
            builder.newLine().italic(true).escaped(author).italic(false).escaped(" " + msg + " ").link("https://github.com/nickrobson/SuperBot/commit/" + id, id.substring(0, 8)).setPreview(false);
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
