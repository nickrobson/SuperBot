package xyz.nickr.superchat.cmd;

import xyz.nickr.superchat.sys.Group;
import xyz.nickr.superchat.sys.User;

public interface Permission {

    boolean has(Group convo, User user);

}
