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
import xyz.nickr.superbot.SuperBotResource;

public class Profile {

    // Profile Name <=> Profile
    public static final Map<String, Profile> ALL = new HashMap<>();

    public static Optional<Profile> getProfile(String name) {
        return Optional.ofNullable(ALL.get(name.toLowerCase()));
    }

    public static Optional<Profile> get(Sys sys, User user) {
        return get(sys, user.getUniqueId());
    }

    public static Optional<Profile> get(Sys sys, String uniqueId) {
        return get(sys.getName(), sys.isUIDCaseSensitive() ? uniqueId : uniqueId.toLowerCase());
    }

    public static Optional<Profile> get(String provider, User user) {
        return get(provider, user.getProvider().isUIDCaseSensitive() ? user.getUniqueId() : user.getUniqueId().toLowerCase());
    }

    public static Optional<Profile> get(String provider, String uniqueId) {
        Sys sys = SuperBotResource.PROVIDERS.get(provider);
        for (Profile prof : ALL.values()) {
            Optional<String> acc = prof.getAccount(provider);
            if (acc.isPresent() && (acc.get().equals(uniqueId) || sys.getUserFriendlyName(acc.get()).equals(sys.getUserFriendlyName(uniqueId)))) {
                return Optional.of(prof);
            }
        }
        return Optional.empty();
    }

    // Provider <=> Unique ID

    private final String name;
    private final File file;
    private final Properties props = new Properties();

    public Profile(String name, File file) {
        this.name = name;
        this.file = file;

        this.load();
        this.props.setProperty("name", name);
    }

    public Profile(File file) {
        this.file = file;
        this.load();
        this.name = this.props.getProperty("name");
    }

    public Profile(String name) {
        this(name, SuperBotProfiles.fileOf(name));
    }

    public String getName() {
        return this.name;
    }

    public boolean has(String key) {
        return this.props.containsKey(key);
    }

    public String get(String key) {
        return this.props.getProperty(key);
    }

    public void remove(String key) {
        this.props.remove(key);
    }

    public void set(String key, String val) {
        this.props.setProperty(key, val);
    }

    public Map<String, String> getAccounts() {
        Map<String, String> accs = new HashMap<>();
        for (String key : this.props.stringPropertyNames()) {
            if (key.startsWith("acc.")) {
                accs.put(key.substring(4), this.props.getProperty(key));
            }
        }
        return accs;
    }

    public Optional<String> getAccount(String provider) {
        return Optional.ofNullable(this.props.getProperty("acc." + provider));
    }

    public void setAccount(Sys sys, User user, boolean save) {
        this.setAccount(sys.getName(), user.getUniqueId(), save);
    }

    public void setAccount(String provider, String uniqueId, boolean save) {
        this.props.setProperty("acc." + provider, uniqueId);
        if (save) {
            this.save();
        }
    }

    public Profile register() {
        return ALL.computeIfAbsent(this.name.toLowerCase(), s -> this);
    }

    public void load() {
        try {
            this.props.load(Files.newBufferedReader(this.file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            Path tmp = Files.createTempFile("superbot-profile-" + this.name + "-" + System.nanoTime(), ".tmp");
            this.props.store(Files.newBufferedWriter(tmp), "User Profile: " + this.name);
            Files.copy(tmp, this.file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            if (!tmp.toFile().delete()) {
                tmp.toFile().deleteOnExit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
