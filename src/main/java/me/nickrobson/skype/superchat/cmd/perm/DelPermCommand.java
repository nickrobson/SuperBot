package me.nickrobson.skype.superchat.cmd.perm;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.SuperChatPermissions;
import me.nickrobson.skype.superchat.cmd.Command;
import me.nickrobson.skype.superchat.perm.Permission;
import me.nickrobson.skype.superchat.perm.StringPermission;

public class DelPermCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{ "delperm" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[]{ "[username] [perm]", "removes [perm] from [username]" };
    }

    @Override
    public Permission perm() {
        return new StringPermission("permissions.modify");
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        if (args.length < 2) {
            group.sendMessage(bold(encode("Usage: ")) + encode(PREFIX + "delperm [username] [perm]"));
        } else {
            if (SuperChatPermissions.set(args[0], args[1], false)) {
                group.sendMessage(encode(args[0] + " no longer has: " + args[1]));
            } else {
                group.sendMessage(encode(args[0] + " never had: " + args[1]));
            }
        }
    }

}
