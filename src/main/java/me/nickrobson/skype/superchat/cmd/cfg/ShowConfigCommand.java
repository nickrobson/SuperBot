package me.nickrobson.skype.superchat.cmd.cfg;

import in.kyle.ezskypeezlife.api.SkypeConversationType;
import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.GroupConfiguration;
import me.nickrobson.skype.superchat.MessageBuilder;
import me.nickrobson.skype.superchat.SuperChatController;
import me.nickrobson.skype.superchat.cmd.Command;

public class ShowConfigCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{ "showcfg" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userchat) {
        return new String[]{ "", "shows this group's config values" };
    }

    @Override
    public boolean alwaysEnabled() {
        return true;
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        GroupConfiguration cfg = SuperChatController.getGroupConfiguration(group, false);
        if (group.getConversationType() == SkypeConversationType.USER)
            group.sendMessage(encode("User chats don't have configurations."));
        else if (cfg == null)
            group.sendMessage(encode("There is no config for this group!"));
        else {
            MessageBuilder mb = new MessageBuilder();
            mb.italic(true).text("Configuration settings:").italic(false).newLine();
            cfg.get().forEach((opt, val) -> {
                mb.bold(true).text(opt).bold(false);
                mb.text(" = ");
                mb.bold(true).text(val).bold(false);
                mb.newLine();
            });
            group.sendMessage(mb.toString());
        }
    }

}
