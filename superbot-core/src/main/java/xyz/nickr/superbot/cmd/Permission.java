package xyz.nickr.superbot.cmd;

import java.util.Optional;

import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Profile;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public interface Permission {

    boolean has(Sys sys, Group convo, User user, Optional<Profile> profile);

}
