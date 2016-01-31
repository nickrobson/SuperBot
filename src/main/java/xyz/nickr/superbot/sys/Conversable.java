package xyz.nickr.superbot.sys;

public interface Conversable {

    Sys getProvider();

    String getUniqueId();

    Message sendMessage(String message);

    default Message sendMessage(MessageBuilder<?> mb) {
        return sendMessage(mb.build());
    }

}
