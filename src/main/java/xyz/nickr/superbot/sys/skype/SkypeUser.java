package xyz.nickr.superbot.sys.skype;

import java.util.Optional;

import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class SkypeUser implements User {

    private final SkypeSys sys;
    final in.kyle.ezskypeezlife.api.user.SkypeUser user;

    public SkypeUser(SkypeSys sys, in.kyle.ezskypeezlife.api.user.SkypeUser user) {
        this.sys = sys;
        this.user = user;
    }

    @Override
    public Sys getProvider() {
        return this.sys;
    }

    @Override
    public String getUniqueId() {
        return this.getUsername();
    }

    @Override
    public Message sendMessage(MessageBuilder message) {
        return this.sys.wrap(this.user.sendMessage(message.build()));
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    @Override
    public Optional<String> getDisplayName() {
        return this.user.getDisplayName();
    }

}
