package me.nickrobson.skype.superchat;

import in.kyle.ezskypeezlife.Chat;
import in.kyle.ezskypeezlife.api.SkypeConversationType;
import in.kyle.ezskypeezlife.api.SkypeUserRole;
import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import in.kyle.ezskypeezlife.events.conversation.SkypeConversationUserJoinEvent;
import in.kyle.ezskypeezlife.events.conversation.SkypeConversationUserLeaveEvent;
import in.kyle.ezskypeezlife.events.conversation.SkypeMessageEditedEvent;
import in.kyle.ezskypeezlife.events.conversation.SkypeMessageReceivedEvent;
import in.kyle.ezskypeezlife.events.user.SkypeContactRequestEvent;
import me.nickrobson.skype.superchat.cmd.Command;

public class SuperChatListener {

    public void join(SkypeConversationUserJoinEvent event) {
        GroupConfiguration cfg = SuperChatController.GCONFIGS.get(event.getConversation().getLongId());
        if (cfg != null && cfg.isShowJoinMessage())
            event.getConversation().sendMessage(
                    Chat.bold(MessageBuilder.html_escape(String.format(SuperChatController.WELCOME_MESSAGE_JOIN,
                        event.getUser().getDisplayName(), event.getConversation().getTopic())))
                        + "\n" + MessageBuilder.html_escape("You can access my help menu by typing `"
                        + SuperChatController.COMMAND_PREFIX + "help`"));
    }

    public void leave(SkypeConversationUserLeaveEvent event) {
        SuperChatController.wipe(event.getUser().getUsername());
    }

    public void contactRequest(SkypeContactRequestEvent event) {
        event.getSkypeUser().setContact(true);
    }

    public synchronized void chat(SkypeMessageReceivedEvent event) {
        cmd(event.getMessage());
    }

    public synchronized void chat(SkypeMessageEditedEvent event) {
        cmd(event.getSkypeMessage());
    }
    
    public synchronized void cmd(SkypeMessage message) {
        SkypeUser user = message.getSender();
        SkypeConversation group = message.getConversation();
        String msg = message.getMessage().trim();
        String[] words = msg.split("\\s+");

        if (words.length == 0 || !words[0].startsWith(SuperChatController.COMMAND_PREFIX)) {
            return;
        }

        String cmdName = words[0].substring(SuperChatController.COMMAND_PREFIX.length()).toLowerCase();
        Command cmd = SuperChatController.COMMANDS.get(cmdName);

        if (cmd == null)
            return;

        GroupConfiguration cfg = SuperChatController.GCONFIGS.get(group.getLongId());
        if (cfg != null)
            if (!cfg.isCommandEnabled(cmd) && !cmd.alwaysEnabled())
                return;
            else if (group.getConversationType() == SkypeConversationType.USER)
                if (!cmd.userchat() && !cmd.alwaysEnabled())
                    return;
                else
                    return;

        String[] args = new String[words.length - 1];
        for (int i = 1; i < words.length; i++)
            args[i - 1] = words[i];

        if (cmd.role() == SkypeUserRole.USER || group.isAdmin(user))
            cmd.exec(user, group, cmdName, args, message);
    }

}
