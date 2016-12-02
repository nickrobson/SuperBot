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
    private final String content;

    public SkypeMessage(SkypeSys sys, ChatMessage msg, String content) {
        this.sys = sys;
        this.msg = msg;
        this.conv = sys.wrap(msg.getChat());
        this.user = sys.wrap(msg.getSender());
        this.content = content != null ? content : this.msg.getContent().asPlaintext();
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
        return content;
    }

    @Override
    public void edit(MessageBuilder message) {
        if (this.msg instanceof SentMessage) {
            try {
                String html = HtmlMessageBuilder.build(message);
                com.samczsun.skype4j.formatting.Message m = com.samczsun.skype4j.formatting.Message.fromHtml(html);
                ((SentMessage) this.msg).edit(m);
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
