package me.nickrobson.skype.superchat.cmd;

import in.kyle.ezskypeezlife.Chat;
import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.MessageBuilder;
import me.nickrobson.skype.superchat.SuperChatController;
import me.nickrobson.skype.superchat.perm.Permission;
import me.nickrobson.skype.superchat.perm.UserPermission;

public interface Command {

    static final String PREFIX = SuperChatController.COMMAND_PREFIX;

    default void init() {}

    default boolean alwaysEnabled() {
        return false;
    }

    default boolean userchat() {
        return false;
    }

    String[] names();

    default Permission perm() {
        return new UserPermission();
    }

    String[] help(SkypeUser user, boolean userChat);

    void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message);

    /* UTILITY FUNCTIONS */

    default String bold(String s) {
        return Chat.bold(s);
    }

    default String encode(String s) {
        return MessageBuilder.html_escape(s);
    }

    default String code(String s) {
        return Chat.code(s);
    }

    default String blink(String s) {
        return Chat.blink(s);
    }

    default String italic(String s) {
        return Chat.italic(s);
    }

    default String link(String text, String url) {
        return Chat.link(text, url);
    }

    default String strike(String s) {
        return Chat.strikeThrough(s);
    }

    default String under(String s) {
        return Chat.underline(s);
    }

    default String size(String s, int size) {
        return Chat.size(s, size);
    }

}
