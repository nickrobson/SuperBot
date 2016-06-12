package xyz.nickr.superbot.sys;

import java.io.File;
import java.net.URL;

import xyz.nickr.superbot.Imgur;

public interface Conversable {

    Sys getProvider();

    String getUniqueId();

    Message sendMessage(MessageBuilder mb);

    default boolean supportsMultiplePhotos() {
        return false;
    }

    default void sendPhoto(File file) {
        sendPhoto(Imgur.upload(file));
    }

    default void sendPhoto(URL url) {
        sendMessage(getProvider().message().link(url.toString()));
    }

}
