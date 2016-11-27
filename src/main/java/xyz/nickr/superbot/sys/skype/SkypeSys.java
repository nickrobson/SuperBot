package xyz.nickr.superbot.sys.skype;

import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.SkypeBuilder;
import com.samczsun.skype4j.chat.Chat;
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
                SkypeListener listener = new SkypeListener(this);
                this.skype = new SkypeBuilder(username, password).withAllResources().build();
                this.skype.login();
                this.skype.getEventDispatcher().registerListener(listener);
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
    public MessageBuilder message() {
        return new HtmlMessageBuilder();
    }

    @Override
    public String getUserFriendlyName(String uniqueId) {
        return uniqueId;
    }

    @Override
    public Group getGroup(String uniqueId) {
        return wrap(skype.getChat(uniqueId));
    }

    Group wrap(Chat group) {
        return new SkypeGroup(this, group);
    }

    User wrap(com.samczsun.skype4j.user.User user) {
        return new SkypeUser(this, user);
    }

    Message wrap(com.samczsun.skype4j.chat.messages.ChatMessage message) {
        return new SkypeMessage(this, message);
    }

}
