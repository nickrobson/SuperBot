package xyz.nickr.superbot.sys.telegram;

import pro.zackpollard.telegrambot.api.TelegramBot;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

import java.util.Optional;

/**
 * Created by bo0tzz
 */
public class TelegramUser implements User {
    private final pro.zackpollard.telegrambot.api.user.User user;
    private final TelegramSys sys;

    public TelegramUser(pro.zackpollard.telegrambot.api.user.User user, TelegramSys sys) {
        this.user = user;
        this.sys = sys;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public Optional<String> getDisplayName() {
        return Optional.of(user.getFullName());
    }

    @Override
    public Sys getProvider() {
        return sys;
    }

    @Override
    public String getUniqueId() {
        return String.valueOf(user.getId());
    }

    @Override
    public Message sendMessage(String message) {
        return sys.wrap(message, sys.sendMessage(TelegramBot.getChat(user.getId()), message));
    }
}
