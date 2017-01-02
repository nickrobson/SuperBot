package xyz.nickr.superbot.sys;

import java.io.File;
import java.net.URL;

import xyz.nickr.superbot.Imgur;

public interface Conversable {

    Sys getProvider();

    String getUniqueId();

    default Message sendMessage(MessageBuilder mb) {
        return sendMessage(mb, true);
    }

    Message sendMessage(MessageBuilder mb, boolean event);

    default boolean supportsMultiplePhotos() {
        return false;
    }

    default void sendPhoto(File file) {
        sendPhoto(file, true, false);
    }

    default void sendPhoto(File file, boolean event, boolean cache) {
        sendPhoto(Imgur.upload(file, cache), event);
    }

    default void sendPhoto(URL url, boolean event) {
        sendMessage(getProvider().message().link(url.toString()), event);
    }

}
