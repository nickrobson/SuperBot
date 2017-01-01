package xyz.nickr.superbot.cmd.profile;

import java.util.Optional;

import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Profile;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class RegisterAccountCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"register"};
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[] {"[profile] [token]", "register your account"};
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        MessageBuilder mb = sys.message();
        if (args.length < 2) {
            this.sendUsage(sys, user, group);
        } else {
            Optional<Profile> profile = Profile.getProfile(args[0]);
            if (profile.isPresent()) {
                Profile prof = profile.get();
                if (prof.has("token")) {
                    String cfgtoken = prof.get("token");
                    String token = args[1];
                    if (cfgtoken.equals(token)) {
                        prof.remove("token");
                        prof.setAccount(sys, user, true);
                        mb.escaped("Added account (provider: " + sys.getName() + ", uid: " + user.getUniqueId() + ") to profile (name: " + prof.getName() + ")");
                    } else {
                        mb.escaped("Invalid token.");
                    }
                } else {
                    mb.escaped("No token registered to your profile.");
                }
            } else {
                mb.escaped("No profile with name = " + args[0].toLowerCase());
            }
        }
        group.sendMessage(mb);
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
