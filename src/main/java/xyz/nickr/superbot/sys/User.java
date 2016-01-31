package xyz.nickr.superbot.sys;

import java.util.Optional;

public interface User extends Conversable {

    String getUsername();

    Optional<String> getDisplayName();

    default String name() {
        return getDisplayName().orElse(getUsername());
    }

}
