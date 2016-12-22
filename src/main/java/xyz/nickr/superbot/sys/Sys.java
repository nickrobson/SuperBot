package xyz.nickr.superbot.sys;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import net.dv8tion.jda.core.entities.MessageChannel;

public abstract class Sys {

    private final Map<String, GroupConfiguration> configs = new HashMap<>();

    public abstract String getName();

    public abstract String prefix();

    public abstract boolean isUIDCaseSensitive();

    public boolean hasKeyboards() {
        return false;
    }

    public abstract String getUserFriendlyName(String uniqueId);

    public abstract Group getGroup(String uniqueId);

    public GroupConfiguration getGroupConfiguration(String uniqueId) {
        return this.configs.get(uniqueId);
    }

    public void addGroupConfiguration(GroupConfiguration cfg) {
        this.configs.put(cfg.getUniqueId(), cfg);
    }

    public void onLoaded() {}

    public final MessageBuilder message() {
        return new MessageBuilder();
    }

    public final MessageBuilder message(MessageBuilder mb) {
        return new MessageBuilder(mb);
    }

}
