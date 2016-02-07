package xyz.nickr.superbot.sys;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
        return new Properties(options);
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

    public boolean isCommandEnabled(Command cmd) {
        boolean a = isUseAlwaysEnabled() && cmd.alwaysEnabled();
        boolean b = isEverythingOn() && cmd.useEverythingOn();
        return getBoolean("cmd." + cmd.names()[0].toLowerCase(), a || b);
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

}
