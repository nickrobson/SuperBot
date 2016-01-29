package xyz.nickr.superchat.sys;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import xyz.nickr.superchat.cmd.Command;

public class GroupConfiguration {

    public static final String KEY_PROVIDER      = "provider";
    public static final String KEY_UNIQUE_ID     = "uniqueId";
    public static final String KEY_IS_DISABLED   = "disabled";
    public static final String KEY_SHOW_JOINS    = "showJoins";
    public static final String KEY_SHOW_EDITS    = "showEdits";
    public static final String KEY_EVERYTHING_ON = "everythingOn";

    private final Map<String, String> options = new HashMap<>();
    private final File                file;

    public GroupConfiguration(File file) {
        this.file = file;
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("=")) {
                    String[] spl = line.split("=", 2);
                    options.put(spl[0].trim(), spl[1]);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public GroupConfiguration save() {
        if (file.exists())
            file.delete();
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardOpenOption.CREATE)) {
            options.forEach((opt,val) -> {
                try {
                    writer.write(opt + "=" + val);
                    writer.newLine();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public Map<String, String> get() {
        return new HashMap<>(options);
    }

    public String get(String option) {
        return options.getOrDefault(option, null);
    }

    public String get(String option, Object def) {
        return options.getOrDefault(option, def.toString());
    }

    public boolean getBoolean(String option) {
        return Boolean.parseBoolean(get(option));
    }

    public boolean getBoolean(String option, boolean def) {
        return Boolean.parseBoolean(get(option, def));
    }

    public String set(String option, String value) {
        return options.put(option, value);
    }

    public String getProvider() {
        return get(KEY_PROVIDER);
    }

    public String getUniqueId() {
        return get(KEY_UNIQUE_ID);
    }

    public boolean isEverythingOn() {
        return Boolean.parseBoolean(get(KEY_EVERYTHING_ON));
    }

    public boolean isCommandEnabled(Command cmd) {
        return isEverythingOn() || getBoolean("cmd." + cmd.names()[0].toLowerCase(), cmd.alwaysEnabled());
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
