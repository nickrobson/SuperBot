package xyz.nickr.superbot.sys;

public interface Message {

    Sys getProvider();

    String getUniqueId();

    Conversable getConversation();

    User getSender();

    String getMessage();

    void edit(MessageBuilder message);

    boolean isEdited();

}
