package me.nickrobson.skype.superchat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public final class SuperChatPermissions {

    static final Map<String, Set<String>> permissions = new HashMap<>();

    public static Set<String> get(String username) {
        return permissions.computeIfAbsent(username.toLowerCase(), k -> new TreeSet<>());
    }

    public static boolean has(String username, String permission) {
        return get(username).contains(permission);
    }

    static boolean set(String username, String permission, boolean on, boolean save) {
        Set<String> s = get(username);
        boolean has = s.contains(permission);
        if (on)
            s.add(permission);
        else
            s.remove(permission);
        permissions.put(username.toLowerCase(), s);
        if (save) {
            SuperChatController.savePermissions();
        }
        return s.contains(permission) != has;
    }

    public static boolean set(String username, String permission, boolean on) {
        return set(username, permission, on, true);
    }

    public static void clear() {
        permissions.clear();
    }

}
