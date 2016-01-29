package xyz.nickr.superchat.sys;

public interface Message {

    Sys getProvider();

    String getUniqueId();

    Conversable getConversation();

    User getSender();

    String getMessage();

    void edit(String message);

    default void edit(MessageBuilder<?> message) {
        edit(message.build());
    }

    boolean isEdited();

}
