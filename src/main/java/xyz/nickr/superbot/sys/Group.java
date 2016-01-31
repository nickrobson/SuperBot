package xyz.nickr.superbot.sys;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface Group extends Conversable {

    String getDisplayName();

    GroupType getType();

    boolean isAdmin(User u);

    Set<User> getUsers();

    default Map<String, User> getUserMap() {
        return getUsers().stream().collect(Collectors.toMap(u -> u.getUsername(), u -> u));
    }

}
