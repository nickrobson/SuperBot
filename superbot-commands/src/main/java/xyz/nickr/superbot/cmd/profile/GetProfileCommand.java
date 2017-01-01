package xyz.nickr.superbot.cmd.profile;

import java.util.Map.Entry;
import java.util.Optional;

import xyz.nickr.superbot.SuperBotResource;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Profile;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class GetProfileCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"profile", "getprofile"};
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[] {"(profile)", "get yours or (profile)'s profile info"};
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        MessageBuilder mb = sys.message();
        if (args.length == 0) {
            Optional<Profile> profile = user.getProfile();
            if (profile.isPresent()) {
                Profile prof = profile.get();
                mb.bold(true).escaped("Your profile (" + prof.getName() + "):").bold(false);
                for (Entry<String, String> acc : prof.getAccounts().entrySet()) {
                    Sys sy = SuperBotResource.PROVIDERS.get(acc.getKey());
                    mb.newLine().escaped("   " + acc.getKey() + ": " + (sy != null ? sy.getUserFriendlyName(acc.getValue()) : acc.getValue()));
                }
            } else {
                this.sendNoProfile(sys, user, group);
                return;
            }
        } else {
            Optional<Profile> profile = Profile.getProfile(args[0]);
            if (profile.isPresent()) {
                Profile prof = profile.get();
                mb.bold(true).escaped("Profile (" + prof.getName() + "):").bold(false);
                for (Entry<String, String> acc : prof.getAccounts().entrySet()) {
                    Sys sy = SuperBotResource.PROVIDERS.get(acc.getKey());
                    mb.newLine().escaped("   " + acc.getKey() + ": " + (sy != null ? sy.getUserFriendlyName(acc.getValue()) : acc.getValue()));
                }
            } else {
                mb.escaped("No profile with (name: " + args[0].toLowerCase() + ")");
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
