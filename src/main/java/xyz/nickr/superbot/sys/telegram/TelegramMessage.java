package xyz.nickr.superbot.sys.telegram;

import xyz.nickr.superbot.sys.Conversable;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

/**
 * Created by bo0tzz
 */
public class TelegramMessage implements Message {

    private final TelegramSys sys;
    private final String strmsg;
    private final pro.zackpollard.telegrambot.api.chat.message.Message message;

    public TelegramMessage(TelegramSys sys, String strmsg, pro.zackpollard.telegrambot.api.chat.message.Message message) {
        this.sys = sys;
        this.strmsg = strmsg;
        this.message = message;
    }

    @Override
    public Sys getProvider() {
        return sys;
    }

    @Override
    public String getUniqueId() {
        return String.valueOf(message.getMessageId());
    }

    @Override
    public Conversable getConversation() {
        return sys.wrap(message.getChat());
    }

    @Override
    public User getSender() {
        return sys.wrap(message.getSender());
    }

    @Override
    public String getMessage() {
        return strmsg;
    }

    @Override
    public void edit(String message) {
        return;
    }

    @Override
    public boolean isEdited() {
        return false;
    }
}
