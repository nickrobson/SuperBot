package xyz.nickr.superbot.sys.skype;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupType;
import xyz.nickr.superbot.sys.Message;
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
        return sys;
    }

    @Override
    public String getUniqueId() {
        return conv.getLongId();
    }

    @Override
    public String getDisplayName() {
        return conv.getTopic();
    }

    @Override
    public GroupType getType() {
        switch (conv.getConversationType()) {
            case USER:
                return GroupType.USER;
            case GROUP:
                return GroupType.GROUP;
            default:
                return null;
        }
    }

    @Override
    public Message sendMessage(String message) {
        if (conv != null)
            return sys.wrap(conv.sendMessage(message));
        return null;
    }

    @Override
    public boolean isAdmin(User u) {
        return conv.isAdmin(((SkypeUser) u).user);
    }

}
