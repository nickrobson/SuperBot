package xyz.nickr.superbot.sys;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import xyz.nickr.superbot.cmd.Command;

public class GroupConfiguration {

    public static final String KEY_PROVIDER           = "provider";
    public static final String KEY_UNIQUE_ID          = "uniqueId";
    public static final String KEY_GROUP_NAME         = "groupName";
    public static final String KEY_IS_DISABLED        = "disabled";
    public static final String KEY_SHOW_JOINS         = "showJoins";
    public static final String KEY_SHOW_EDITS         = "showEdits";
    public static final String KEY_EVERYTHING_ON      = "everythingOn";
    public static final String KEY_USE_ALWAYS_ENABLED = "useAlwaysEnabled";

    private final File file;
    private Properties options;

    public GroupConfiguration(File file) {
        this.file = file;
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        try {
            options = new Properties();
            options.load(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GroupConfiguration save() {
        if (file.exists())
            file.delete();
        try {
            Path tmp = Files.createTempFile("superbot-groupcfg-" + getUniqueId() + "-" + System.nanoTime(), ".tmp");
            options.store(Files.newBufferedWriter(tmp), "GroupConfiguration: " + String.valueOf(getUniqueId()));
            Files.copy(tmp, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            if (!tmp.toFile().delete())
                tmp.toFile().deleteOnExit();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public Properties get() {
        Properties props = new Properties();
        options.forEach(props::put);
        return props;
    }

    public String get(String option) {
        return options.getProperty(option, null);
    }

    public String get(String option, Object def) {
        return options.getProperty(option, def.toString());
    }

    public boolean getBoolean(String option) {
        return Boolean.parseBoolean(get(option));
    }

    public boolean getBoolean(String option, boolean def) {
        return Boolean.parseBoolean(get(option, def));
    }

    public String set(String option, String value) {
        Object o = options.setProperty(option, value);
        return o != null ? o.toString() : null;
    }

    public boolean isCommandEnabled(Command cmd) {
        boolean a = isUseAlwaysEnabled() && cmd.alwaysEnabled();
        boolean b = isEverythingOn() && cmd.useEverythingOn();
        return getBoolean("cmd." + cmd.names()[0].toLowerCase(), a || b);
    }

    public String getProvider() {
        return get(KEY_PROVIDER);
    }

    public String getUniqueId() {
        return get(KEY_UNIQUE_ID);
    }

    public boolean isEverythingOn() {
        return getBoolean(KEY_EVERYTHING_ON, false);
    }

    public boolean isUseAlwaysEnabled() {
        return getBoolean(KEY_USE_ALWAYS_ENABLED, true);
    }

    public boolean isShowJoinMessage() {
        return isEverythingOn() || getBoolean(KEY_SHOW_JOINS);
    }

    public boolean isShowEditedMessages() {
        return getBoolean(KEY_SHOW_EDITS);
    }

    public boolean isDisabled() {
        return getBoolean(KEY_IS_DISABLED);
    }

    // global configuration data!

    private static final Map<String, Map<String, GroupConfiguration>> configs = new HashMap<>();

    public static GroupConfiguration getGroupConfiguration(Group group) {
        return getGroupConfiguration(group, true);
    }

    public static GroupConfiguration getGroupConfiguration(Group group, boolean create) {
        if (group.getType() != GroupType.GROUP) {
            return null;
        }
        String uniqueId = group.getUniqueId();
        Sys provider = group.getProvider();
        GroupConfiguration cfg = get(provider, uniqueId);
        if (cfg == null) {
            cfg = newGroupConfiguration(provider.getName() + "-" + group.getUniqueId());
            cfg.set(GroupConfiguration.KEY_PROVIDER, provider.getName());
            cfg.set(GroupConfiguration.KEY_UNIQUE_ID, uniqueId);
            cfg.set(GroupConfiguration.KEY_GROUP_NAME, group.getDisplayName());
            cfg.save();
            put(provider, group.getUniqueId(), cfg);
        } else {
            cfg.set(GroupConfiguration.KEY_GROUP_NAME, group.getDisplayName());
            cfg.save();
        }
        return cfg;
    }

    public static GroupConfiguration get(Sys provider, String uniqueId) {
        Map<String, GroupConfiguration> map = configs.get(provider.getName());
        return map != null ? map.get(uniqueId) : null;
    }

    public static void put(Sys provider, String uniqueId, GroupConfiguration cfg) {
        Map<String, GroupConfiguration> map = configs.get(provider.getName());
        if (map == null)
            map = new HashMap<>();
        map.put(uniqueId, cfg);
        configs.put(provider.getName(), map);
    }

    public static GroupConfiguration newGroupConfiguration(String name) {
        File file, dir = new File("groups");
        int n = 0;
        do {
            file = new File(dir, name + "-" + (n++) + ".cfg");
        } while (file.exists());
        return new GroupConfiguration(file);
    }
}
