package xyz.nickr.superbot.sys.skype;

import com.samczsun.skype4j.exceptions.ConnectionException;
import java.util.Optional;

import xyz.nickr.superbot.event.EventManager;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class SkypeUser implements User {

    private final SkypeSys sys;
    final com.samczsun.skype4j.user.User user;

    public SkypeUser(SkypeSys sys, com.samczsun.skype4j.user.User user) {
        this.sys = sys;
        this.user = user;
    }

    @Override
    public Sys getProvider() {
        return this.sys;
    }

    @Override
    public String getUniqueId() {
        return this.getUsername();
    }

    @Override
    public Message sendMessage(MessageBuilder message, boolean event) {
        try {
            String html = HtmlMessageBuilder.build(message);
            com.samczsun.skype4j.formatting.Message m = com.samczsun.skype4j.formatting.Message.fromHtml(html);
            Message msg = this.sys.wrap(this.user.getChat().sendMessage(m), null);
            if (event && msg != null)
                EventManager.onSend(this, message);
            return msg;
        } catch (ConnectionException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    @Override
    public Optional<String> getDisplayName() {
        try {
            return Optional.of(this.user.getDisplayName());
        } catch (ConnectionException e) {
            return Optional.of(this.user.getUsername());
        }
    }

}
