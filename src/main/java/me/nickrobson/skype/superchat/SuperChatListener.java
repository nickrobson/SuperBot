package me.nickrobson.skype.superchat;

import java.util.HashSet;
import java.util.Set;

import me.nickrobson.skype.superchat.cmd.Command;
import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.event.EventListener;
import xyz.gghost.jskype.events.APILoadedEvent;
import xyz.gghost.jskype.events.UserChatEvent;
import xyz.gghost.jskype.events.UserJoinEvent;
import xyz.gghost.jskype.events.UserLeaveEvent;
import xyz.gghost.jskype.events.UserPendingContactRequestEvent;
import xyz.gghost.jskype.internal.impl.GroupImpl;
import xyz.gghost.jskype.message.FormatUtils;
import xyz.gghost.jskype.message.Message;
import xyz.gghost.jskype.user.GroupUser;
import xyz.gghost.jskype.user.OnlineStatus;
import xyz.gghost.jskype.user.User;

public class SuperChatListener implements EventListener {

    public void loaded(APILoadedEvent event) {
        SuperChatController.skype.updateStatus(OnlineStatus.ONLINE);

        try {
            for (User user : SuperChatController.skype.getContactRequests())
                user.sendContactRequest(SuperChatController.skype, "FYI, you can send commands here too!");
        } catch (Exception ex) {
        }
    }

    public void join(UserJoinEvent event) {
        GroupConfiguration cfg = SuperChatController.GCONFIGS.get(event.getGroup().getLongId());
        if (cfg != null && cfg.isShowJoinMessage())
            event.getGroup().sendMessage(FormatUtils.bold(MessageBuilder.html_escape(String.format(SuperChatController.WELCOME_MESSAGE_JOIN, event.getUser().getDisplayName(), event.getGroup().getTopic()))) + "\n" +
                MessageBuilder.html_escape("You can access my help menu by typing `" + SuperChatController.COMMAND_PREFIX + "help`"));
    }

    public void leave(UserLeaveEvent event) {
        SuperChatController.wipe(event.getUser().getUsername());
    }

    public void contactRequest(UserPendingContactRequestEvent event) {
        event.accept(SuperChatController.skype);
    }

    private final Set<String> seenMessages = new HashSet<>();

    public synchronized void chat(UserChatEvent event) {
        Message message = event.getMsg();
        User user = message.getSender();
        Group group = event.getChat();
        String msg = message.getMessage().trim();
        String[] words = msg.split("\\s+");

        if (seenMessages.contains(message.getId()))
            return;

        seenMessages.add(message.getId());

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                seenMessages.remove(message.getId());
            } catch (Exception e) {
            }
        }).start();

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
        else if (group.isUserChat())
            if (!cmd.userchat() && !cmd.alwaysEnabled())
                return;
        else
            return;
        
        String[] args = new String[words.length - 1];
        for (int i = 1; i < words.length; i++)
            args[i - 1] = words[i];

        GroupUser guser = group.getUserByUsername(user.getUsername());
        if (guser != null || group.isUserChat()) {
            if (group.isUserChat())
                guser = new GroupUser(user, GroupUser.Role.MASTER, (GroupImpl) group);
            if (cmd.role() == GroupUser.Role.USER || guser.role == GroupUser.Role.MASTER)
                cmd.exec(guser, group, cmdName, args, message);
        }
    }

}
