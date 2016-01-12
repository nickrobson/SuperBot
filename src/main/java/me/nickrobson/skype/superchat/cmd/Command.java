package me.nickrobson.skype.superchat.cmd;

import in.kyle.ezskypeezlife.Chat;
import in.kyle.ezskypeezlife.api.SkypeConversationType;
import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.MessageBuilder;
import me.nickrobson.skype.superchat.SuperChatController;
import me.nickrobson.skype.superchat.SuperChatPermissions;

public interface Command {

    static final String PREFIX = SuperChatController.COMMAND_PREFIX;

    static final Permission DEFAULT_PERMISSION = (c, u) -> true;

    String[] names();

    default Permission perm() {
        return DEFAULT_PERMISSION;
    }

    String[] help(SkypeUser user, boolean userchat);

    void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message);

    default void init() {}

    default boolean alwaysEnabled() {
        return false;
    }

    default boolean userchat() {
        return false;
    }

    /* UTILITY FUNCTIONS */

    default String name(SkypeUser user) {
        return user.getDisplayName().orElse(user.getUsername());
    }

    default SkypeMessage sendUsage(SkypeUser user, SkypeConversation group) {
        String[] help = help(user, group.getConversationType() == SkypeConversationType.USER);
        String h = help != null && help[0] != null && !help[0].isEmpty() ? " " + help[0] : "";
        return group.sendMessage(bold(encode("Usage: ")) + encode(PREFIX + names()[0] + h));
    }

    default Permission admin() {
        return (c, u) -> c.isAdmin(u);
    }

    default Permission string(String perm) {
        return (c, u) -> SuperChatPermissions.has(u.getUsername(), perm);
    }

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
