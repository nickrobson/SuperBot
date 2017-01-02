package xyz.nickr.superbot.sys.gitter;

import java.util.Optional;

import xyz.nickr.jitter.api.User;
import xyz.nickr.superbot.event.EventManager;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;

public class GitterUser implements xyz.nickr.superbot.sys.User {

    private final GitterSys sys;
    private final User user;

    public GitterUser(GitterSys sys, User user) {
        this.sys = sys;
        this.user = user;
    }

    @Override
    public Sys getProvider() {
        return this.sys;
    }

    @Override
    public String getUniqueId() {
        return this.user.getUsername();
    }

    @Override
    public Message sendMessage(MessageBuilder message, boolean event) {
        Message m = this.sys.wrap(this.user.sendMessage(GitterMessageBuilder.build(message)));
        if (event) {
            EventManager.onSend(this, message);
        }
        return m;
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    @Override
    public Optional<String> getDisplayName() {
        return Optional.of(this.user.getDisplayName());
    }

}
