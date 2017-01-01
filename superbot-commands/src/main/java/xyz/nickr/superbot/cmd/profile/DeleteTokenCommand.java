package xyz.nickr.superbot.cmd.profile;

import java.util.Optional;

import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Profile;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class DeleteTokenCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"deletetoken"};
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[] {"", "deletes your token"};
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        Optional<Profile> profile = user.getProfile();
        if (profile.isPresent()) {
            MessageBuilder mb = sys.message();
            Profile prof = profile.get();
            if (prof.has("token")) {
                prof.remove("token");
                prof.save();
                mb.escaped("Deleted your old token.");
            } else {
                mb.escaped("No token registered to your profile.");
            }
            group.sendMessage(mb);
        } else {
            this.sendNoProfile(sys, user, group);
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
