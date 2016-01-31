package xyz.nickr.superbot.cmd.profile;

import java.util.Map.Entry;
import java.util.Optional;

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
        return new String[]{ "getprofile", "profile" };
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[]{ "(user)", "get yours or (user)'s profile" };
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        MessageBuilder<?> mb = sys.message();
        if (args.length == 0) {
            Optional<Profile> prof = user.getProfile();
            if (prof.isPresent()) {
                mb.bold(true).text("Your profile (" + prof.get().getName() + "):").bold(false);
                for (Entry<String, String> acc : prof.get().getAccounts().entrySet()) {
                    mb.newLine().text("   " + acc.getKey() + ": " + acc.getValue());
                }
            } else {
                sendNoProfile(sys, user, group);
            }
        } else {
            String s = sys.isUIDCaseSensitive() ? args[0] : args[0].toLowerCase();
            Optional<Profile> prof = Profile.get(sys, s);
            if (prof.isPresent()) {
                mb.bold(true).text(" " + s + "'s profile (" + prof.get().getName() + "):").bold(false);
                for (Entry<String, String> acc : prof.get().getAccounts().entrySet()) {
                    mb.newLine().text("   " + acc.getKey() + ": " + acc.getValue());
                }
            } else {
                mb.text(" " + s + " doesn't have a profile yet.");
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
