package xyz.nickr.superbot.sys.skype;

import xyz.nickr.superbot.sys.Conversable;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
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
        return this.msg.getMessage();
    }

    @Override
    public void edit(MessageBuilder message) {
        this.msg.edit(message.build());
    }

    @Override
    public boolean isEdited() {
        return this.msg.isEdited();
    }

}
