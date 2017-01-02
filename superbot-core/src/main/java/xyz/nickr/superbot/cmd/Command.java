package xyz.nickr.superbot.cmd;

import xyz.nickr.superbot.SuperBotPermissions;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupType;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public interface Command {

    Permission DEFAULT_PERMISSION = (s, c, u, p) -> true;

    String[] names();

    default Permission perm() {
        return DEFAULT_PERMISSION;
    }

    String[] help(User user, boolean userchat);

    void exec(Sys sys, User user, Group group, String used, String[] args, Message message);

    default void init() {}

    default boolean alwaysEnabled() {
        return false;
    }

    default boolean userchat() {
        return false;
    }

    default boolean useEverythingOn() {
        return true;
    }

    /* UTILITY FUNCTIONS */

    default Message sendUsage(Sys sys, User user, Group group) {
        String[] help = help(user, group.getType() == GroupType.USER);
        String h = help != null && help[0] != null && !help[0].isEmpty() ? " " + help[0] : "";
        return group.sendMessage(sys.message().bold(true).escaped("Usage:").bold(false).escaped(" " + sys.prefix() + names()[0] + h));
    }

    default Message sendNoProfile(Sys sys, User user, Group group) {
        return group.sendMessage(sys.message().escaped("You don't have a profile! Create one first."));
    }

    default Message sendNoPermission(Sys sys, Group group) {
        MessageBuilder mb = sys.message();
        mb.bold(true).escaped("Error:").bold(false);
        mb.escaped(" You don't have permission to use " + sys.prefix() + names()[0] + "!");
        return group.sendMessage(mb);
    }

    default Permission string(String perm) {
        return (s, c, u, p) -> p.isPresent() && SuperBotPermissions.has(p.get().getName(), perm);
    }

}
