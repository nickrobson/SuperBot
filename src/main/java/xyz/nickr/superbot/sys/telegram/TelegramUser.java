package xyz.nickr.superbot.sys.telegram;

import java.util.Optional;

import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

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
        return this.user.getUsername();
    }

    @Override
    public Optional<String> getDisplayName() {
        return Optional.of(this.user.getFullName());
    }

    @Override
    public Sys getProvider() {
        return this.sys;
    }

    @Override
    public String getUniqueId() {
        return String.valueOf(this.user.getId());
    }

    @Override
    public Message sendMessage(MessageBuilder message) {
        return this.sys.wrap(message, this.sys.sendMessage(this.sys.getBot().getChat(this.user.getId()), message));
    }
}
