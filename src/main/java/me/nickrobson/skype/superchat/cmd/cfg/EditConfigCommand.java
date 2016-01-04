package me.nickrobson.skype.superchat.cmd.cfg;

import in.kyle.ezskypeezlife.api.SkypeConversationType;
import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.GroupConfiguration;
import me.nickrobson.skype.superchat.SuperChatController;
import me.nickrobson.skype.superchat.cmd.Command;
import me.nickrobson.skype.superchat.perm.Permission;
import me.nickrobson.skype.superchat.perm.StringPermission;

public class EditConfigCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{ "editcfg" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userchat) {
        return new String[]{ "[option] [value]", "sets [option] to [value] in this group's config" };
    }

    @Override
    public Permission perm() {
        return new StringPermission("cfg.edit");
    }

    @Override
    public boolean alwaysEnabled() {
        return true;
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        if (group.getConversationType() == SkypeConversationType.USER) {
            group.sendMessage(encode("User chats don't have configurations."));
        } else if (args.length < 2) {
            sendUsage(user, group);
        } else {
            GroupConfiguration cfg = SuperChatController.getGroupConfiguration(group);
            String prev = cfg.set(args[0], args[1]);
            cfg.save();
            group.sendMessage(bold(encode(args[0])) + " is now " + bold(encode(args[1])) + (prev != null ? " (was " + bold(encode(prev)) + ")" : "") + ".");
        }
    }

}
