package xyz.nickr.superbot.sys.telegram;

import pro.zackpollard.telegrambot.api.chat.message.content.TextContent;
import xyz.nickr.superbot.SuperBotCommands;
import xyz.nickr.superbot.sys.Conversable;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

/**
 * Created by bo0tzz
 */
public class TelegramMessage implements Message {
    private final TelegramSys sys;
    private final pro.zackpollard.telegrambot.api.chat.message.Message message;

    public TelegramMessage(pro.zackpollard.telegrambot.api.chat.message.Message message, TelegramSys sys) {
        this.message = message;
        this.sys = sys;
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
        String content = ((TextContent)message.getContent()).getContent();
        if (content.startsWith("/")) {
            content.replaceFirst("/", SuperBotCommands.COMMAND_PREFIX);
        }
        return content;
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
