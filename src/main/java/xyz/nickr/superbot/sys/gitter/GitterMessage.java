package xyz.nickr.superbot.sys.gitter;

import xyz.nickr.jitter.api.Message;
import xyz.nickr.superbot.sys.Conversable;
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
        return sys;
    }

    @Override
    public String getUniqueId() {
        return message.getID();
    }

    @Override
    public Conversable getConversation() {
        return sys.wrap(message.getRoom());
    }

    @Override
    public User getSender() {
        return sys.wrap(message.getSender());
    }

    @Override
    public String getMessage() {
        return message.getText();
    }

    @Override
    public void edit(String message) {
        this.message.edit(message);
    }

    @Override
    public boolean isEdited() {
        return this.message.getEditTimestamp().isPresent();
    }

}
