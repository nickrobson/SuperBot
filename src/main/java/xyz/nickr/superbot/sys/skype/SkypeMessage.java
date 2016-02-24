package xyz.nickr.superbot.sys.skype;

import xyz.nickr.superbot.sys.Conversable;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class SkypeMessage implements Message {

    private final SkypeSys sys;
    private final in.kyle.ezskypeezlife.api.conversation.message.SkypeMessage msg;
    private final Conversable conv;
    private final User user;

    public SkypeMessage(SkypeSys sys, in.kyle.ezskypeezlife.api.conversation.message.SkypeMessage msg) {
        this.sys = sys;
        this.msg = msg;
        this.conv = sys.wrap(msg.getConversation());
        this.user = sys.wrap(msg.getSender());
    }

    @Override
    public Sys getProvider() {
        return sys;
    }

    @Override
    public String getUniqueId() {
        return msg.getId();
    }

    @Override
    public Conversable getConversation() {
        return conv;
    }

    @Override
    public User getSender() {
        return user;
    }

    @Override
    public String getMessage() {
        return msg.getMessage();
    }

    @Override
    public void edit(String message) {
        msg.edit(message);
    }

    @Override
    public boolean isEdited() {
        return msg.isEdited();
    }

}
