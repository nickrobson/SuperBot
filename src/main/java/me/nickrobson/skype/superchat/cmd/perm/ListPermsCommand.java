package me.nickrobson.skype.superchat.cmd.perm;

import java.util.Set;

import in.kyle.ezskypeezlife.api.SkypeConversationType;
import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.Joiner;
import me.nickrobson.skype.superchat.MessageBuilder;
import me.nickrobson.skype.superchat.SuperChatPermissions;
import me.nickrobson.skype.superchat.cmd.Command;

public class ListPermsCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{ "listperms", "showperms" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userchat) {
        return userchat ? new String[]{ "", "shows your permissions" } : new String[]{ "[username]", "shows [username]'s permissions" };
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        String username;
        if (args.length == 0 || group.getConversationType() == SkypeConversationType.USER) {
            username = user.getUsername();
        } else {
            username = args[0];
        }
        Set<String> perms = SuperChatPermissions.get(username);
        MessageBuilder mb = new MessageBuilder();
        if (perms.isEmpty()) {
            mb.bold(true).text(username + " has no permissions.").bold(false);
        } else {
            mb.bold(true).text(username + " has the following permissions:").bold(false).newLine();
            mb.text(Joiner.join(", ", perms));
        }
        group.sendMessage(mb.toString());
    }

}
