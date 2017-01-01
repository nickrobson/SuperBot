package xyz.nickr.superbot.cmd.permission;

import java.util.Optional;
import java.util.Set;

import xyz.nickr.superbot.SuperBotPermissions;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Profile;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class ListPermsCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"listperms", "showperms"};
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return userchat ? new String[] {"", "shows your permissions"} : new String[] {"[profile]", "shows [profile]'s permissions"};
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        MessageBuilder mb = sys.message();
        Optional<Profile> prof = args.length == 0 ? user.getProfile() : Profile.getProfile(args[0]);
        if (!prof.isPresent()) {
            if (args.length == 0) {
                this.sendNoProfile(sys, user, group);
            } else {
                group.sendMessage(mb.escaped("No profile with name = " + args[0].toLowerCase()));
            }
            return;
        }
        String name = prof.get().getName();
        Set<String> perms = SuperBotPermissions.get(name);
        if (perms.isEmpty()) {
            mb.bold(true).escaped("Profile " + name + " has no permissions.").bold(false);
        } else {
            mb.bold(true).escaped("Profile " + name + " has the following permissions:").bold(false).newLine();
            mb.escaped(String.join(", ", perms));
        }
        group.sendMessage(mb);
    }

}
