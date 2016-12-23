package xyz.nickr.superbot.sys.telegram;

import lombok.AllArgsConstructor;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupConfiguration;
import xyz.nickr.superbot.sys.Sys;

@AllArgsConstructor
public class TelegramInlineSys extends Sys {

    private TelegramSys sys;

    @Override
    public String getName() {
        return this.sys.getName();
    }

    @Override
    public String prefix() {
        return this.sys.prefix();
    }

    @Override
    public boolean isUIDCaseSensitive() {
        return this.sys.isUIDCaseSensitive();
    }

    @Override
    public boolean hasKeyboards() {
        return true;
    }

    @Override
    public String getUserFriendlyName(String uniqueId) {
        return this.sys.getUserFriendlyName(uniqueId);
    }

    @Override
    public Group getGroup(String uniqueId) {
        return sys.getGroup(uniqueId);
    }

    @Override
    public GroupConfiguration getGroupConfiguration(String uniqueId) {
        return this.sys.getGroupConfiguration(uniqueId);
    }

    @Override
    public void addGroupConfiguration(GroupConfiguration cfg) {}

}
