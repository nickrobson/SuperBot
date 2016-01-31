package xyz.nickr.superbot.cmd;

import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.User;

public interface Permission {

    boolean has(Group convo, User user);

}
