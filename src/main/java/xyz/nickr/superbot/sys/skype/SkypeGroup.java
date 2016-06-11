package xyz.nickr.superbot.sys.skype;

import java.util.Set;
import java.util.stream.Collectors;

import in.kyle.ezskypeezlife.api.conversation.SkypeConversation;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupType;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class SkypeGroup implements Group {

    private final SkypeSys sys;
    private final SkypeConversation conv;

    public SkypeGroup(SkypeSys sys, SkypeConversation conv) {
        this.sys = sys;
        this.conv = conv;
    }

    @Override
    public Sys getProvider() {
        return this.sys;
    }

    @Override
    public String getUniqueId() {
        return this.conv.getLongId();
    }

    @Override
    public String getDisplayName() {
        return this.conv.getTopic();
    }

    @Override
    public Message sendMessage(MessageBuilder message) {
        return this.sys.wrap(this.conv.sendMessage(message.build()));
    }

    @Override
    public GroupType getType() {
        switch (this.conv.getConversationType()) {
            case USER:
                return GroupType.USER;
            case GROUP:
                return GroupType.GROUP;
            default:
                return null;
        }
    }

    @Override
    public Set<User> getUsers() {
        return this.conv.getUsers().stream().map(u -> this.sys.wrap(u)).collect(Collectors.toSet());
    }

    @Override
    public boolean isAdmin(User u) {
        return this.conv.isAdmin(((SkypeUser) u).user);
    }

}
