package xyz.nickr.superbot.sys;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class Sys {

    public static final Pattern START_OF_LINE = Pattern.compile("^", Pattern.MULTILINE);

    private final Map<String, GroupConfiguration> configs = new HashMap<>();

    public abstract String getName();

    public abstract String prefix();

    public abstract boolean isUIDCaseSensitive();

    public boolean hasKeyboards() {
        return false;
    }

    public abstract MessageBuilder message();

    public abstract String getUserFriendlyName(String uniqueId);

    public abstract Group getGroup(String uniqueId);

    public GroupConfiguration getGroupConfiguration(String uniqueId) {
        return this.configs.get(uniqueId);
    }

    public void addGroupConfiguration(GroupConfiguration cfg) {
        this.configs.put(cfg.getUniqueId(), cfg);
    }

    public void onLoaded() {}
}
