package xyz.nickr.superbot.cmd.config;

import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.cmd.Permission;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupConfiguration;
import xyz.nickr.superbot.sys.GroupType;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class EditConfigCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"editcfg"};
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[] {"[option] [value]", "sets [option] to [value] in this group's config"};
    }

    @Override
    public Permission perm() {
        return this.string("cfg.edit");
    }

    @Override
    public boolean alwaysEnabled() {
        return true;
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (group.getType() == GroupType.USER) {
            group.sendMessage(sys.message().escaped("User chats don't have configurations."));
        } else if (args.length < 2) {
            this.sendUsage(sys, user, group);
        } else {
            GroupConfiguration cfg = GroupConfiguration.getGroupConfiguration(group);
            String prev = cfg.set(args[0], args[1]);
            cfg.save();
            MessageBuilder mb = sys.message().bold(true).escaped(args[0]).bold(false).escaped(" is now ").bold(true).escaped(args[1]).bold(false);
            if (prev != null) {
                mb.escaped(" (was ").bold(true).escaped(prev).bold(false).escaped(")");
            }
            group.sendMessage(mb.escaped("."));
        }
    }

}
