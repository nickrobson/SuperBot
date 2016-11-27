package xyz.nickr.superbot.sys.skype;

import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.chat.GroupChat;
import com.samczsun.skype4j.chat.IndividualChat;
import com.samczsun.skype4j.chat.messages.ChatMessage;
import com.samczsun.skype4j.chat.messages.SentMessage;
import com.samczsun.skype4j.exceptions.ConnectionException;
import java.util.Set;
import java.util.stream.Collectors;

import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupType;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class SkypeGroup implements Group {

    private final SkypeSys sys;
    private final Chat conv;

    public SkypeGroup(SkypeSys sys, Chat conv) {
        this.sys = sys;
        this.conv = conv;
    }

    @Override
    public Sys getProvider() {
        return this.sys;
    }

    @Override
    public String getUniqueId() {
        return this.conv.getIdentity();
    }

    @Override
    public String getDisplayName() {
        if (conv instanceof GroupChat) {
            return ((GroupChat) conv).getTopic();
        } else if (conv instanceof IndividualChat) {
            return ((IndividualChat) conv).getPartner().getUsername();
        }
        return this.conv.getIdentity();
    }

    @Override
    public Message sendMessage(MessageBuilder message) {
        Message m = sendMessageNoShare(message);
        if (m != null)
            share(message);
        return m;
    }

    @Override
    public Message sendMessageNoShare(MessageBuilder message) {
        try {
            com.samczsun.skype4j.formatting.Message m = com.samczsun.skype4j.formatting.Message.fromHtml(message.build());
            ChatMessage sent = this.conv.sendMessage(m);
            return this.sys.wrap(sent);
        } catch (ConnectionException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public GroupType getType() {
        if (conv instanceof GroupChat) {
            return GroupType.GROUP;
        } else if (conv instanceof IndividualChat) {
            return GroupType.USER;
        }
        return null;
    }

    @Override
    public Set<User> getUsers() {
        return this.conv.getAllUsers().stream().map(this.sys::wrap).collect(Collectors.toSet());
    }

    @Override
    public boolean isAdmin(User u) {
        return this.conv.getUser(u.getUsername()).getRole() == com.samczsun.skype4j.user.User.Role.ADMIN;
    }

}
