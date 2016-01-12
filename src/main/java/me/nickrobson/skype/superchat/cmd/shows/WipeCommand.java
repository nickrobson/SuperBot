package me.nickrobson.skype.superchat.cmd.shows;

import java.util.concurrent.atomic.AtomicBoolean;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.SuperChatController;
import me.nickrobson.skype.superchat.cmd.Command;
import me.nickrobson.skype.superchat.cmd.Permission;

public class WipeCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "wipe", "clear" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[] { "[user]", "wipe [user]'s progress" };
    }

    @Override
    public Permission perm() {
        return string("admin.wipe");
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        if (args.length == 0) {
            sendUsage(user, group);
            return;
        }
        String toRemove = args[0];
        AtomicBoolean wiped = SuperChatController.wipe(toRemove);
        if (wiped.get()) {
            group.sendMessage(encode("Wiped " + toRemove));
            SuperChatController.saveProgress();
        } else {
            group.sendMessage(encode("No data to wipe on " + toRemove));
        }
    }

}
