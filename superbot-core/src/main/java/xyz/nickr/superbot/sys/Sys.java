package xyz.nickr.superbot.sys;

public abstract class Sys {

    public abstract String getName();

    public abstract String prefix();

    public abstract boolean isUIDCaseSensitive();

    public boolean hasKeyboards() {
        return false;
    }

    public abstract String getUserFriendlyName(String uniqueId);

    public abstract Group getGroup(String uniqueId);

    public void onLoaded() {}

    public final MessageBuilder message() {
        return new MessageBuilder();
    }

    public final MessageBuilder message(MessageBuilder mb) {
        return new MessageBuilder(mb);
    }

}
