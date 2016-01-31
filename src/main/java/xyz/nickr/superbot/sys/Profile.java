package xyz.nickr.superbot.sys;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import xyz.nickr.superbot.SuperBotProfiles;

public class Profile {

    // Profile Name <=> Profile
    public static final Map<String, Profile> ALL = new HashMap<>();

    public static Optional<Profile> getProfile(String name) {
        return Optional.ofNullable(ALL.get(name.toLowerCase()));
    }

    public static Optional<Profile> get(Sys sys, User user) {
        return get(sys.getName(), sys.isUIDCaseSensitive() ? user.getUniqueId() : user.getUniqueId().toLowerCase());
    }

    public static Optional<Profile> get(Sys sys, String uniqueId) {
        return get(sys.getName(), sys.isUIDCaseSensitive() ? uniqueId : uniqueId.toLowerCase());
    }

    public static Optional<Profile> get(String provider, User user) {
        return get(provider, user.getProvider().isUIDCaseSensitive() ? user.getUniqueId() : user.getUniqueId().toLowerCase());
    }

    public static Optional<Profile> get(String provider, String uniqueId) {
        for (Profile prof : ALL.values()) {
            Optional<String> acc = prof.getAccount(provider);
            if (acc.isPresent() && acc.get().equals(uniqueId))
                return Optional.of(prof);
        }
        return Optional.empty();
    }

    // Provider <=> Unique ID

    private final String name;
    private final File file;
    private final Properties props = new Properties();

    public Profile(String name, File file) {
        this.name = name.toLowerCase();
        this.file = file;

        load();
        props.setProperty("name", name);
    }

    public Profile(File file) {
        this.file = file;
        load();
        this.name = props.getProperty("name").toLowerCase();
    }

    public Profile(String name) {
        this(name, SuperBotProfiles.fileOf(name));
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getAccounts() {
        Map<String, String> accs = new HashMap<>();
        for (String key : props.stringPropertyNames()) {
            if (key.startsWith("acc."))
                accs.put(key.substring(4), props.getProperty(key));
        }
        return accs;
    }

    public Optional<String> getAccount(String provider) {
        return Optional.ofNullable(props.getProperty("acc." + provider));
    }

    public void setAccount(Sys sys, User user, boolean save) {
        setAccount(sys.getName(), user.getUniqueId(), save);
    }

    public void setAccount(String provider, String uniqueId, boolean save) {
        props.setProperty(provider, uniqueId);
        if (save)
            save();
    }

    public Profile register() {
        return ALL.computeIfAbsent(name.toLowerCase(), s -> this);
    }

    public void load() {
        try {
            props.load(Files.newBufferedReader(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            Path tmp = Files.createTempFile("superbot-profile-" + name + "-" + System.nanoTime(), ".tmp");
            props.store(Files.newBufferedWriter(tmp), "");
            Files.copy(tmp, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            if (!tmp.toFile().delete())
                tmp.toFile().deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
