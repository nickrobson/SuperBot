package xyz.nickr.superbot.sys.telegram;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.ChatMember;
import pro.zackpollard.telegrambot.api.chat.GroupChat;
import pro.zackpollard.telegrambot.api.chat.IndividualChat;
import pro.zackpollard.telegrambot.api.chat.SuperGroupChat;
import pro.zackpollard.telegrambot.api.chat.message.send.InputFile;
import pro.zackpollard.telegrambot.api.chat.message.send.SendablePhotoMessage;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupType;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

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
        if (this.chat instanceof GroupChat) {
            return ((GroupChat) this.chat).getName();
        } else if (this.chat instanceof SuperGroupChat) {
            return ((SuperGroupChat) this.chat).getName();
        } else if (this.chat instanceof IndividualChat) {
            return ((IndividualChat) this.chat).getPartner().getUsername();
        } else {
            return "";
        }
    }

    @Override
    public GroupType getType() {
        if (this.chat instanceof IndividualChat) {
            return GroupType.USER;
        }
        return GroupType.GROUP;
    }

    @Override
    public boolean isAdmin(User u) {
        for (ChatMember cm : this.chat.getChatAdministrators()) {
            if (cm.getUser().getUsername().equals(this.sys.getBot().getBotUsername())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<User> getUsers() {
        return this.chat.getChatAdministrators().stream().map(m -> this.sys.wrap(m.getUser())).collect(Collectors.toSet());
    }

    @Override
    public Sys getProvider() {
        return this.sys;
    }

    @Override
    public String getUniqueId() {
        return this.chat.getId();
    }

    @Override
    public Message sendMessage(MessageBuilder message) {
        Message m = this.sys.wrap(message, this.sys.sendMessage(this.chat, message));
        share(message);
        return m;
    }

    @Override
    public Message sendMessageNoShare(MessageBuilder message) {
        return this.sys.wrap(message, this.sys.sendMessage(this.chat, message));
    }

    @Override
    public void sendPhoto(File file) {
        this.chat.sendMessage(SendablePhotoMessage.builder().photo(new InputFile(file)).build());
    }

}
