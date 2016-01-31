package xyz.nickr.superbot.sys;

import java.util.regex.Pattern;

public interface Sys {

    final Pattern START_OF_LINE = Pattern.compile("^", Pattern.MULTILINE);

    String getName();

    boolean isUIDCaseSensitive();

    MessageBuilder<?> message();

    GroupConfiguration getGroupConfiguration(String uniqueId);

    void addGroupConfiguration(GroupConfiguration cfg);

}
