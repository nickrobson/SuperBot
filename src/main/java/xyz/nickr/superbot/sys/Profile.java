package xyz.nickr.superbot.sys;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class Profile {

    // Profile Name <=> Profile
    public static final Map<String, Profile> ALL = new HashMap<>();

    public static Optional<Profile> getProfile(String name) {
        return Optional.ofNullable(ALL.get(name.toLowerCase()));
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
        this.name = name;
        this.file = file;

        load();
        props.setProperty("name", name);
    }

    public Profile(File file) {
        this.file = file;
        load();
        this.name = props.getProperty("name");
    }

    public String getName() {
        return name;
    }

    public Optional<String> getAccount(String provider) {
        return Optional.ofNullable(props.getProperty("acc." + provider));
    }

    public void setAccount(String provider, String uniqueId, boolean save) {
        props.setProperty(provider, uniqueId);
        if (save)
            save();
    }

    public void setAccount(String provider, String uniqueId) {
        setAccount(provider, uniqueId, true);
    }

    public Profile register() {
        return ALL.putIfAbsent(name.toLowerCase(), this);
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
            file.delete();
            props.store(Files.newBufferedWriter(file.toPath()), "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
