package xyz.nickr.superchat.cmd;

import xyz.nickr.superchat.SuperChatController;
import xyz.nickr.superchat.SuperChatPermissions;
import xyz.nickr.superchat.sys.Group;
import xyz.nickr.superchat.sys.GroupType;
import xyz.nickr.superchat.sys.Message;
import xyz.nickr.superchat.sys.Sys;
import xyz.nickr.superchat.sys.User;

public interface Command {

    static final String PREFIX = SuperChatController.COMMAND_PREFIX;

    static final Permission DEFAULT_PERMISSION = (c, u) -> true;

    String[] names();

    default Permission perm() {
        return DEFAULT_PERMISSION;
    }

    String[] help(User user, boolean userchat);

    void exec(Sys sys, User user, Group conv, String used, String[] args, Message message);

    default void init() {}

    default boolean alwaysEnabled() {
        return false;
    }

    default boolean userchat() {
        return false;
    }

    /* UTILITY FUNCTIONS */

    default Message sendUsage(Sys sys, User user, Group group) {
        String[] help = help(user, group.getType() == GroupType.USER);
        String h = help != null && help[0] != null && !help[0].isEmpty() ? " " + help[0] : "";
        return group.sendMessage(sys.message().bold(true).text("Usage: ").bold(false).text(PREFIX + names()[0] + h));
    }

    default Permission admin() {
        return (c, u) -> c.isAdmin(u);
    }

    default Permission string(String perm) {
        return (c, u) -> SuperChatPermissions.has(u.getUsername(), perm);
    }

}
