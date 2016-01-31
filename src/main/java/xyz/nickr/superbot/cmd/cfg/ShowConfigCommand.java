package xyz.nickr.superbot.cmd.cfg;

import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupConfiguration;
import xyz.nickr.superbot.sys.GroupType;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class ShowConfigCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{ "showcfg" };
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[]{ "", "shows this group's config values" };
    }

    @Override
    public boolean alwaysEnabled() {
        return true;
    }

    @Override
    public void exec(Sys sys, User user, Group conv, String used, String[] args, Message message) {
        MessageBuilder<?> mb = sys.message();
        GroupConfiguration cfg = SuperBotController.getGroupConfiguration(conv, false);
        if (conv.getType() == GroupType.USER)
            conv.sendMessage(mb.text("User chats don't have configurations."));
        else if (cfg == null)
            conv.sendMessage(mb.text("There is no config for this group!"));
        else {
            mb.bold(true).text("Configuration settings:").bold(false);
            cfg.get().forEach((opt, val) -> {
                mb.newLine();
                mb.italic(true).text(opt.toString()).italic(false);
                mb.text(" = " + val);
            });
            conv.sendMessage(mb.toString());
        }
    }

}
