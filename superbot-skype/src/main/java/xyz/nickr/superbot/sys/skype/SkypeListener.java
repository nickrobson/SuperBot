package xyz.nickr.superbot.sys.skype;

import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.chat.messages.ChatMessage;
import com.samczsun.skype4j.events.EventHandler;
import com.samczsun.skype4j.events.Listener;

import com.samczsun.skype4j.events.chat.message.MessageEditedEvent;
import com.samczsun.skype4j.events.chat.message.MessageReceivedEvent;
import com.samczsun.skype4j.events.contact.ContactRequestEvent;
import com.samczsun.skype4j.exceptions.ConnectionException;
import xyz.nickr.superbot.SuperBotCommands;
import xyz.nickr.superbot.event.EventManager;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.User;

public class SkypeListener implements Listener {

    private SkypeSys sys;

    public SkypeListener(SkypeSys sys) {
        this.sys = sys;
    }

    @EventHandler
    public void onContactRequest(ContactRequestEvent event) {
        try {
            event.getRequest().accept();
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onMessageReceived(MessageReceivedEvent event) {
        this.cmd(event.getMessage(), null);
    }

    @EventHandler
    public void onMessageEdited(MessageEditedEvent event) {
        this.cmd(event.getMessage(), event.getNewContent());
    }

    public synchronized void cmd(ChatMessage message, String msg) {
        com.samczsun.skype4j.user.User user = message.getSender();
        Chat group = message.getChat();

        Group g = this.sys.wrap(group);
        User u = this.sys.wrap(user);

        Message m = this.sys.wrap(message, msg);

        if (!u.getUsername().equals(sys.skype.getUsername())) {
            EventManager.onMessage(this.sys, g, u, m);
            SuperBotCommands.exec(this.sys, g, u, m);
        }
    }

}
