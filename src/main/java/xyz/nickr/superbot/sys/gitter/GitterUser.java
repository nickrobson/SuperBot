package xyz.nickr.superbot.sys.gitter;

import java.util.Optional;

import xyz.nickr.jitter.api.User;
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
        return sys;
    }

    @Override
    public String getUniqueId() {
        return user.getUsername();
    }

    @Override
    public xyz.nickr.superbot.sys.Message sendMessage(String message) {
        return sys.wrap(user.sendMessage(message));
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public Optional<String> getDisplayName() {
        return Optional.of(user.getDisplayName());
    }

}
