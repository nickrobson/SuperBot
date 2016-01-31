package xyz.nickr.superbot.sys.telegram;

import pro.zackpollard.telegrambot.api.chat.*;
import xyz.nickr.superbot.sys.*;
import xyz.nickr.superbot.sys.User;

import java.util.Collections;
import java.util.Set;

/**
 * Created by bo0tzz
 */
public class TelegramGroup implements Group {
    private final Chat chat;
    private final TelegramSys sys;

    public TelegramGroup(Chat chat, TelegramSys sys) {
        this.chat = chat;
        this.sys = sys;
    }

    @Override
    public String getDisplayName() {
        if(chat instanceof GroupChat) {
            return ((GroupChat) chat).getName();
        } else if (chat instanceof SuperGroupChat) {
            return ((SuperGroupChat) chat).getName();
        } else if (chat instanceof IndividualChat) {
            return ((IndividualChat) chat).getPartner().getUsername();
        } else {
            return "";
        }
    }

    @Override
    public GroupType getType() {
        if (chat instanceof IndividualChat) {
            return GroupType.USER;
        }
        return GroupType.GROUP;
    }

    @Override
    public boolean isAdmin(User u) {
        return false;
    }

    @Override
    public Set<User> getUsers() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Sys getProvider() {
        return sys;
    }

    @Override
    public String getUniqueId() {
        return chat.getId();
    }

    @Override
    public Message sendMessage(String message) {
        return sys.wrap(sys.sendMessage(chat, message));
    }
}
