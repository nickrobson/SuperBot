package xyz.nickr.superbot.sys.telegram;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupConfiguration;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

/**
 * Created by bo0tzz
 */
public class TelegramSys implements Sys {

    private final TelegramBot bot;

    private final Map<String, GroupConfiguration> configs = new HashMap<>();
    private final Properties usernameCache = new Properties();

    public TelegramSys(String key) {
        bot = TelegramBot.login(key);
        bot.getEventsManager().register(new TelegramListener(bot, this));
        bot.startUpdates(false);
        try {
            usernameCache.load(new FileInputStream(new File("tgusers.cache")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "Telegram";
    }

    @Override
    public String prefix() {
        return "/";
    }

    @Override
    public boolean isUIDCaseSensitive() {
        return false;
    }

    @Override
    public boolean columns() {
        return false;
    }

    @Override
    public MessageBuilder<?> message() {
        return new TelegramMessageBuilder();
    }

    @Override
    public String getUserFriendlyName(String uniqueId) {
        return usernameCache.getProperty(uniqueId, uniqueId);
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
        String un = user.getUsername();
        if (un != null)
            usernameCache.setProperty(String.valueOf(user.getId()), un);
        try {
            Path tmp = Files.createTempFile("superbot-tguserscache-" + System.nanoTime(), ".tmp");
            usernameCache.store(Files.newBufferedWriter(tmp), "Telegram Username Cache file");
            Files.copy(tmp, new File("tgusers.cache").toPath(), StandardCopyOption.REPLACE_EXISTING);
            if (!tmp.toFile().delete())
                tmp.toFile().deleteOnExit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new TelegramUser(user, this);
    }

    Message wrap(String msg, pro.zackpollard.telegrambot.api.chat.message.Message message) {
        return new TelegramMessage(this, msg, message);
    }

    public pro.zackpollard.telegrambot.api.chat.message.Message sendMessage(Chat chat, String message) {
        System.out.println("Sending: " + message);
        SendableTextMessage msg = SendableTextMessage.builder().message(message).parseMode(ParseMode.MARKDOWN).build();
        return bot.sendMessage(chat, msg);
    }

}
