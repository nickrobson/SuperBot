package xyz.nickr.superbot.sys.skype;

import java.util.Optional;

import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class SkypeUser implements User {

    private final SkypeSys sys;
    final in.kyle.ezskypeezlife.api.obj.SkypeUser user;

    public SkypeUser(SkypeSys sys, in.kyle.ezskypeezlife.api.obj.SkypeUser user) {
        this.sys = sys;
        this.user = user;
    }

    @Override
    public Sys getProvider() {
        return sys;
    }

    @Override
    public Message sendMessage(String message) {
        return sys.wrap(user.sendMessage(message));
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public Optional<String> getDisplayName() {
        return user.getDisplayName();
    }

}
