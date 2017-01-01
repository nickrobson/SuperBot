package xyz.nickr.superbot.cmd.profile;

import java.util.Optional;

import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Profile;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class CreateProfileCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"createprofile"};
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[] {"[name]", "creates a profile with the given name"};
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length == 0) {
            this.sendUsage(sys, user, group);
        } else {
            Optional<Profile> existing = user.getProfile();
            Optional<Profile> matching = Profile.getProfile(args[0]);
            MessageBuilder mb = sys.message();
            if (existing.isPresent()) {
                mb.escaped("You already have a profile (" + existing.get().getName() + ")");
            } else if (matching.isPresent()) {
                mb.escaped("A profile with that name already exists!");
            } else {
                Profile prof = new Profile(args[0]).register();
                prof.setAccount(sys, user, true);
                mb.escaped("Successfully created profile (name: " + prof.getName() + ")");
            }
            group.sendMessage(mb);
        }
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public boolean alwaysEnabled() {
        return true;
    }

}
