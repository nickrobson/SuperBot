package xyz.nickr.superbot.sys.telegram;

import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import xyz.nickr.superbot.sys.*;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bo0tzz
 */
public class TelegramSys implements Sys {
    private final TelegramBot bot;

    private final Map<String, GroupConfiguration> configs = new HashMap<>();

    public TelegramSys(String key) {
        bot = TelegramBot.login(key);
        bot.getEventsManager().register(new TelegramListener(bot, this));
        bot.startUpdates(false);
    }

    @Override
    public String getName() {
        return "Telegram";
    }

    @Override
    public boolean isUIDCaseSensitive() {
        return false;
    }

    @Override
    public MessageBuilder<?> message() {
        return new TelegramMessageBuilder();
    }

    @Override
    public GroupConfiguration getGroupConfiguration(String uniqueId) {
        return configs.get(uniqueId);
    }

    @Override
    public void addGroupConfiguration(GroupConfiguration cfg) {
        configs.put(cfg.getUniqueId(), cfg);
    }

    Group wrap(Chat chat) {
        return new TelegramGroup(chat, this);
    }

    User wrap(pro.zackpollard.telegrambot.api.user.User user) {
        return new TelegramUser(user, this);
    }

    Message wrap(String msg, pro.zackpollard.telegrambot.api.chat.message.Message message) {
        return new TelegramMessage(this, msg, message);
    }

    public pro.zackpollard.telegrambot.api.chat.message.Message sendMessage(Chat chat, String message) {
        SendableTextMessage msg = SendableTextMessage.builder().message(message).parseMode(ParseMode.HTML).build();
        return bot.sendMessage(chat, msg);
    }
}
