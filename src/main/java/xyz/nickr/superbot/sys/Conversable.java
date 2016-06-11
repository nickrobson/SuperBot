package xyz.nickr.superbot.sys;

public interface Conversable {

    Sys getProvider();

    String getUniqueId();

    Message sendMessage(MessageBuilder mb);

    default Message sendMessage(String message, Object... params) {
        return sendMessage(getProvider().message().escaped(message, params));
    }

}
