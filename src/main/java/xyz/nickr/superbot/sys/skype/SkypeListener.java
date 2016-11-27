package xyz.nickr.superbot.sys.skype;

import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.chat.messages.ChatMessage;
import com.samczsun.skype4j.events.EventHandler;
import com.samczsun.skype4j.events.Listener;

import com.samczsun.skype4j.events.chat.message.MessageReceivedEvent;
import com.samczsun.skype4j.events.contact.ContactRequestEvent;
import com.samczsun.skype4j.exceptions.ConnectionException;
import xyz.nickr.superbot.SuperBotCommands;
import xyz.nickr.superbot.cmd.link.LinkCommand;
import xyz.nickr.superbot.sys.Group;
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
        this.cmd(event.getMessage());
    }

    public synchronized void cmd(ChatMessage message) {
        com.samczsun.skype4j.user.User user = message.getSender();
        Chat group = message.getChat();

        Group g = this.sys.wrap(group);
        User u = this.sys.wrap(user);

        LinkCommand.propagate(this.sys, g, u, this.sys.wrap(message));
        SuperBotCommands.exec(this.sys, g, u, this.sys.wrap(message));
    }

}
