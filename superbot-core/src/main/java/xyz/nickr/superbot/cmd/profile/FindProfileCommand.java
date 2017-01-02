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

public class FindProfileCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"findprofile"};
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[] {"(uid)", "get yours or (uid)'s " + user.getProvider().getName() + " profile"};
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        MessageBuilder mb = sys.message();
        if (args.length == 0) {
            Optional<Profile> prof = user.getProfile();
            if (prof.isPresent()) {
                mb.bold(true).escaped("Your profile (" + prof.get().getName() + "):").bold(false);
                for (Entry<String, String> acc : prof.get().getAccounts().entrySet()) {
                    Sys sy = SuperBotResource.PROVIDERS.get(acc.getKey());
                    mb.newLine().escaped("   " + acc.getKey() + ": " + (sy != null ? sy.getUserFriendlyName(acc.getValue()) : acc.getValue()));
                }
            } else {
                this.sendNoProfile(sys, user, group);
                return;
            }
        } else {
            String s = sys.isUIDCaseSensitive() ? args[0] : args[0].toLowerCase();
            Optional<Profile> prof = Profile.get(sys, s);
            if (prof.isPresent()) {
                mb.bold(true).escaped(" " + s + "'s profile (" + prof.get().getName() + "):").bold(false);
                for (Entry<String, String> acc : prof.get().getAccounts().entrySet()) {
                    Sys sy = SuperBotResource.PROVIDERS.get(acc.getKey());
                    mb.newLine().escaped("   " + acc.getKey() + ": " + (sy != null ? sy.getUserFriendlyName(acc.getValue()) : acc.getValue()));
                }
            } else {
                mb.escaped("No profile with (provider: " + sys.getName() + ", name: " + s + ")");
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
