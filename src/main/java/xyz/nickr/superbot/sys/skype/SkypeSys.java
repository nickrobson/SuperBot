package xyz.nickr.superbot.sys.skype;

import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.SkypeBuilder;
import com.samczsun.skype4j.Visibility;
import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.exceptions.ChatNotFoundException;
import com.samczsun.skype4j.exceptions.ConnectionException;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class SkypeSys extends Sys {

    Skype skype;

    public SkypeSys(String username, String password) {
        new Thread(() -> {
            long now = System.currentTimeMillis();
            System.out.println("Loading SuperBot: Skype");
            try {
                this.skype = new SkypeBuilder(username, password).withAllResources().build();
                this.skype.login();
                this.skype.getEventDispatcher().registerListener(new SkypeListener(this));
                this.skype.subscribe();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Done SuperBot: Skype (" + (System.currentTimeMillis() - now) + "ms)");
        }).start();
    }

    @Override
    public String getName() {
        return "Skype";
    }

    @Override
    public String prefix() {
        return "+";
    }

    @Override
    public boolean isUIDCaseSensitive() {
        return false;
    }

    @Override
    public String getUserFriendlyName(String uniqueId) {
        return uniqueId;
    }

    @Override
    public Group getGroup(String uniqueId) {
        try {
            return wrap(skype.getOrLoadChat(uniqueId));
        } catch (ConnectionException | ChatNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    Group wrap(Chat group) {
        return new SkypeGroup(this, group);
    }

    User wrap(com.samczsun.skype4j.user.User user) {
        return new SkypeUser(this, user);
    }

    Message wrap(com.samczsun.skype4j.chat.messages.ChatMessage message, String msg) {
        return new SkypeMessage(this, message, msg);
    }

}
