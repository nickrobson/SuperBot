package xyz.nickr.superbot.sys.gitter;

import xyz.nickr.jitter.api.Message;
import xyz.nickr.superbot.sys.Conversable;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class GitterMessage implements xyz.nickr.superbot.sys.Message {

    private final GitterSys sys;
    private final Message message;

    public GitterMessage(GitterSys sys, Message message) {
        this.sys = sys;
        this.message = message;
    }

    @Override
    public Sys getProvider() {
        return this.sys;
    }

    @Override
    public String getUniqueId() {
        return this.message.getID();
    }

    @Override
    public Conversable getConversation() {
        return this.sys.wrap(this.message.getRoom());
    }

    @Override
    public User getSender() {
        return this.sys.wrap(this.message.getSender());
    }

    @Override
    public String getMessage() {
        return this.message.getText();
    }

    @Override
    public void edit(MessageBuilder message) {
        this.message.edit(GitterMessageBuilder.build(message));
    }

    @Override
    public boolean isEdited() {
        return this.message.getEditTimestamp().isPresent();
    }

}
