package xyz.nickr.superbot.sys;

public interface Conversable {

    Sys getProvider();

    String getUniqueId();

    Message sendMessage(MessageBuilder mb);

}
