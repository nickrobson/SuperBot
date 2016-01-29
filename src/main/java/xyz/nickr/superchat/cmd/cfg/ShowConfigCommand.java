package xyz.nickr.superchat.cmd.cfg;

import xyz.nickr.superchat.SuperChatController;
import xyz.nickr.superchat.cmd.Command;
import xyz.nickr.superchat.sys.Group;
import xyz.nickr.superchat.sys.GroupConfiguration;
import xyz.nickr.superchat.sys.GroupType;
import xyz.nickr.superchat.sys.Message;
import xyz.nickr.superchat.sys.MessageBuilder;
import xyz.nickr.superchat.sys.Sys;
import xyz.nickr.superchat.sys.User;

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
        GroupConfiguration cfg = SuperChatController.getGroupConfiguration(conv, false);
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
