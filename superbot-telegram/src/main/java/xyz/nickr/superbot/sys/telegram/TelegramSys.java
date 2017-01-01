package xyz.nickr.superbot.sys.telegram;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage.SendableTextMessageBuilder;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

/**
 * Created by bo0tzz
 */
public class TelegramSys extends Sys {

    private TelegramBot bot;

    private final Properties usernameCache = new Properties();

    public TelegramSys(String key) {
        new Thread(() -> {
            long now = System.currentTimeMillis();
            System.out.println("Loading SuperBot: Telegram");
            this.bot = TelegramBot.login(key);
            this.bot.getEventsManager().register(new TelegramListener(this.bot, this));
            this.bot.startUpdates(true);

            try {
                this.usernameCache.load(new FileInputStream(new File("tgusers.cache")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Done SuperBot: Telegram (" + (System.currentTimeMillis() - now) + "ms)");
        }).start();
    }

    public TelegramBot getBot() {
        return this.bot;
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
    public String getUserFriendlyName(String uniqueId) {
        Chat chat = bot.getChat(uniqueId);
        if (chat != null) {
            return chat.getName();
        }
        return this.usernameCache.getProperty(uniqueId, uniqueId);
    }

    @Override
    public Group getGroup(String uniqueId) {
        Chat c = bot.getChat(uniqueId);
        return c != null ? wrap(c) : null;
    }

    Group wrap(Chat chat) {
        return new TelegramGroup(chat, this);
    }

    User wrap(pro.zackpollard.telegrambot.api.user.User user) {
        String un = user.getUsername();
        if (un != null) {
            if (!un.equals(this.usernameCache.setProperty(String.valueOf(user.getId()), un))) {
                try {
                    Path tmp = Files.createTempFile("superbot-tguserscache-" + System.nanoTime(), ".tmp");
                    this.usernameCache.store(Files.newBufferedWriter(tmp), "Telegram Username Cache file");
                    Files.copy(tmp, new File("tgusers.cache").toPath(), StandardCopyOption.REPLACE_EXISTING);
                    if (!tmp.toFile().delete()) {
                        tmp.toFile().deleteOnExit();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return new TelegramUser(user, this);
    }

    Message wrap(MessageBuilder msg, pro.zackpollard.telegrambot.api.chat.message.Message message) {
        return new TelegramMessage(this, TelegramMessageBuilder.build(msg), message);
    }

    public pro.zackpollard.telegrambot.api.chat.message.Message sendMessage(Chat chat, MessageBuilder message) {
        SendableTextMessageBuilder msg = SendableTextMessage.builder();
        msg.disableWebPagePreview(!message.isPreview());
        msg.message(TelegramMessageBuilder.build(message));
        msg.parseMode(ParseMode.MARKDOWN);
        return this.bot.sendMessage(chat, msg.build());
    }

}
