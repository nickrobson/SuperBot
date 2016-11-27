package xyz.nickr.superbot.sys;

import java.util.Optional;

public interface User extends Conversable {

    String getUsername();

    Optional<String> getDisplayName();

    default String name() {
        return getDisplayName().orElse(getUsername());
    }

    default Optional<Profile> getProfile() {
        return Profile.get(getProvider(), this);
    }

    default Message sendMessageNoShare(MessageBuilder m) {
        return sendMessage(m);
    }

}
