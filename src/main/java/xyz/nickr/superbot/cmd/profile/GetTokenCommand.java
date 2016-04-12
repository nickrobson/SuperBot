package xyz.nickr.superbot.cmd.profile;

import java.security.SecureRandom;
import java.util.Optional;

import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.Profile;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class GetTokenCommand implements Command {

    final SecureRandom random = new SecureRandom();

    @Override
    public String[] names() {
        return new String[]{ "gettoken", "regtoken" };
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[]{ "", "get a registration token" };
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        Optional<Profile> profile = user.getProfile();
        if (profile.isPresent()) {
            Profile prof = profile.get();
            String token = "";
            if (prof.has("token")) {
                token = prof.get("token");
            } else {
                String chars = "abcdefghijklmnopqrstuvwxyz";
                chars += chars.toUpperCase() + "0123456789";
                for (int i = 0; i < 8; i++)
                    token += chars.charAt(random.nextInt(chars.length()));
                prof.set("token", token);
                prof.save();
            }
            user.sendMessage(sys.message().escaped("Your registration token: " + token));
            group.sendMessage(sys.message().escaped("I've sent you the token. If you didn't receive it, make sure I'm a contact and not blocked."));
        } else {
            sendNoProfile(sys, user, group);
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
