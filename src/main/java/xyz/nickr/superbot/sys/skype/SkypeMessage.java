package xyz.nickr.superbot.sys.skype;

import com.samczsun.skype4j.chat.messages.ChatMessage;
import com.samczsun.skype4j.chat.messages.SentMessage;
import com.samczsun.skype4j.exceptions.ConnectionException;
import xyz.nickr.superbot.sys.Conversable;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class SkypeMessage implements Message {

    private final SkypeSys sys;
    private final ChatMessage msg;
    private final Conversable conv;
    private final User user;

    public SkypeMessage(SkypeSys sys, ChatMessage msg) {
        this.sys = sys;
        this.msg = msg;
        this.conv = sys.wrap(msg.getChat());
        this.user = sys.wrap(msg.getSender());
    }

    @Override
    public Sys getProvider() {
        return this.sys;
    }

    @Override
    public String getUniqueId() {
        return this.msg.getId();
    }

    @Override
    public Conversable getConversation() {
        return this.conv;
    }

    @Override
    public User getSender() {
        return this.user;
    }

    @Override
    public String getMessage() {
        return this.msg.getContent().asPlaintext();
    }

    @Override
    public void edit(MessageBuilder message) {
        if (this.msg instanceof SentMessage) {
            try {
                ((SentMessage) this.msg).edit(com.samczsun.skype4j.formatting.Message.fromHtml(message.build()));
            } catch (ConnectionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isEdited() {
        return false;
    }

}
