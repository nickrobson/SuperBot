package xyz.nickr.superbot.sys;

import java.util.regex.Pattern;

public interface Sys {

    final Pattern START_OF_LINE = Pattern.compile("^", Pattern.MULTILINE);

    String getName();

    boolean isUIDCaseSensitive();

    xyz.nickr.superbot.sys.MessageBuilder<?> message();

    xyz.nickr.superbot.sys.GroupConfiguration getGroupConfiguration(String uniqueId);

    void addGroupConfiguration(xyz.nickr.superbot.sys.GroupConfiguration cfg);

}
