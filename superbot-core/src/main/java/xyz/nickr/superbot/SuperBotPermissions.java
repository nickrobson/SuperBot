package xyz.nickr.superbot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public final class SuperBotPermissions {

    static final Map<String, Set<String>> permissions = new HashMap<>();

    public static Set<String> get(String profile) {
        if (profile == null) {
            return new HashSet<>();
        }
        return permissions.computeIfAbsent(profile.toLowerCase(), k -> new TreeSet<>());
    }

    public static boolean has(String profile, String permission) {
        return get(profile).contains(permission);
    }

    static boolean set(String profile, String permission, boolean on, boolean save) {
        Set<String> s = get(profile);
        boolean has = s.contains(permission);
        if (on) {
            s.add(permission);
        } else {
            s.remove(permission);
        }
        permissions.put(profile.toLowerCase(), s);
        if (save) {
            SuperBotResource.savePermissions();
        }
        return s.contains(permission) != has;
    }

    public static boolean set(String profile, String permission, boolean on) {
        return set(profile, permission, on, true);
    }

    public static void clear() {
        permissions.clear();
    }

}
