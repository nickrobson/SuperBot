package xyz.nickr.superbot.sys.telegram;

import java.io.File;

import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.IndividualChat;
import pro.zackpollard.telegrambot.api.chat.message.send.InputFile;
import pro.zackpollard.telegrambot.api.chat.message.send.SendablePhotoMessage;
import xyz.nickr.superbot.event.EventManager;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupType;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;

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
        if (this.chat instanceof IndividualChat) {
            return ((IndividualChat) this.chat).getPartner().getUsername();
        }
        return this.chat.getName();
    }

    @Override
    public GroupType getType() {
        if (this.chat instanceof IndividualChat) {
            return GroupType.USER;
        }
        return GroupType.GROUP;
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
    public Message sendMessage(MessageBuilder message, boolean event) {
        Message m = this.sys.sendMessage(this.chat, message);
        if (event)
            EventManager.onSend(this, message);
        return m;
    }

    @Override
    public void sendPhoto(File file) {
        this.chat.sendMessage(SendablePhotoMessage.builder().photo(new InputFile(file)).build());
    }

}
