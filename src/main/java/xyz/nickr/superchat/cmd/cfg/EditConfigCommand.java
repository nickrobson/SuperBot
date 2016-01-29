package xyz.nickr.superchat.cmd.cfg;

import xyz.nickr.superchat.SuperChatController;
import xyz.nickr.superchat.cmd.Command;
import xyz.nickr.superchat.cmd.Permission;
import xyz.nickr.superchat.sys.Group;
import xyz.nickr.superchat.sys.GroupConfiguration;
import xyz.nickr.superchat.sys.GroupType;
import xyz.nickr.superchat.sys.Message;
import xyz.nickr.superchat.sys.MessageBuilder;
import xyz.nickr.superchat.sys.Sys;
import xyz.nickr.superchat.sys.User;

public class EditConfigCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{ "editcfg" };
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[]{ "[option] [value]", "sets [option] to [value] in this group's config" };
    }

    @Override
    public Permission perm() {
        return string("cfg.edit");
    }

    @Override
    public boolean alwaysEnabled() {
        return true;
    }

    @Override
    public void exec(Sys sys, User user, Group conv, String used, String[] args, Message message) {
        if (conv.getType() == GroupType.USER) {
            conv.sendMessage(sys.message().text("User chats don't have configurations."));
        } else if (args.length < 2) {
            sendUsage(null, user, conv);
        } else {
            GroupConfiguration cfg = SuperChatController.getGroupConfiguration(conv);
            String prev = cfg.set(args[0], args[1]);
            cfg.save();
            MessageBuilder<?> mb = sys.message().bold(true).text(args[0]).bold(false).text(" is now ").bold(true).text(args[1]).bold(false);
            if (prev != null)
                mb.text(" (was ").bold(true).text(prev).bold(false).text(")");
            conv.sendMessage(mb.text("."));
        }
    }

}
