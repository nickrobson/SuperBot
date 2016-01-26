package me.nickrobson.skype.superchat;

import java.util.regex.Pattern;

import in.kyle.ezskypeezlife.Chat;
import in.kyle.ezskypeezlife.api.SkypeConversationType;
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

    public static final Pattern START_OF_LINE = Pattern.compile("^", Pattern.MULTILINE);

    public void join(SkypeConversationUserJoinEvent event) {
        SkypeUser user = event.getUser();
        SkypeConversation convo = event.getConversation();
        GroupConfiguration cfg = SuperChatController.getGroupConfiguration(convo);
        if (cfg != null && cfg.isShowJoinMessage()) {
            String welcome = String.format(SuperChatController.WELCOME_MESSAGE_JOIN, user.getUsername(), convo.getTopic());
            String help = "You can access my help menu by typing `" + SuperChatController.COMMAND_PREFIX + "help`";
            String message = Chat.bold(MessageBuilder.html_escape(welcome)) + "\n" + MessageBuilder.html_escape(help);
            convo.sendMessage(message);
        }
    }

    public void leave(SkypeConversationUserLeaveEvent event) {
        SuperChatController.wipe(event.getUser().getUsername());
    }

    public void contactRequest(SkypeContactRequestEvent event) {
        event.getUser().setContact(true);
    }

    public synchronized void chat(SkypeMessageReceivedEvent event) {
        cmd(event.getMessage());
    }

    public synchronized void chat(SkypeMessageEditedEvent event) {
        SkypeConversation convo = event.getMessage().getConversation();
        GroupConfiguration conf = SuperChatController.getGroupConfiguration(convo, false);
        boolean isGroup = convo.getConversationType() == SkypeConversationType.GROUP;
        if (isGroup && conf != null && conf.isShowEditedMessages()) {
            MessageBuilder mb = new MessageBuilder();
            mb.bold(true).text(event.getUser().getUsername()).bold(false);
            mb.text(" edited their message:").newLine();
            mb.html(START_OF_LINE.matcher(event.getContentOld()).replaceAll("&gt; ")).newLine().newLine();
            mb.html(START_OF_LINE.matcher(event.getContentNew()).replaceAll("&gt; "));
            convo.sendMessage(mb.build());
        }
        cmd(event.getMessage());
    }

    public synchronized void cmd(SkypeMessage message) {
        SkypeUser user = message.getSender();
        SkypeConversation group = message.getConversation();
        String msg = message.getMessage().trim();
        String[] words = msg.split("\\s+");

        if (msg.isEmpty() || words.length == 0 || !words[0].startsWith(SuperChatController.COMMAND_PREFIX)) {
            return;
        }

        String cmdName = words[0].substring(SuperChatController.COMMAND_PREFIX.length()).toLowerCase();
        Command cmd = SuperChatController.COMMANDS.get(cmdName);
        if (cmd == null)
            return;

        GroupConfiguration cfg = SuperChatController.getGroupConfiguration(group);
        if (group.getConversationType() == SkypeConversationType.GROUP) {
            if (cfg.isDisabled() || !cfg.isCommandEnabled(cmd))
                return;
        } else if (!cmd.userchat())
            return;

        String[] args = new String[words.length - 1];
        for (int i = 1; i < words.length; i++)
            args[i - 1] = words[i];

        boolean userchat = group.getConversationType() == SkypeConversationType.USER && cmd.userchat();

        if (group.getConversationType() == SkypeConversationType.GROUP || userchat) {
            if (!cmd.perm().has(group, user)) {
                MessageBuilder mb = new MessageBuilder();
                mb.bold(true).text("Error: ").bold(false);
                mb.text("You don't have permission to use " + SuperChatController.COMMAND_PREFIX + cmdName + "!");
                group.sendMessage(mb.toString());
            } else {
                cmd.exec(user, group, cmdName, args, message);
            }
        }
    }

}
