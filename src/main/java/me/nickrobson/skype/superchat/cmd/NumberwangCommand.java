package me.nickrobson.skype.superchat.cmd;

import java.util.Random;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;

public class NumberwangCommand implements Command {

    private final Random random = new Random();

    @Override
    public String[] names() {
        return new String[]{ "numberwang" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[]{ "", "guess a number" };
    }

    @Override
    public boolean alwaysEnabled() {
        return true;
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        if (args.length == 0)
            group.sendMessage(encode("You need to guess a number!"));
        else if (random.nextInt(random.nextInt(7)+1) == 0)
            group.sendMessage(encode("That's numberwang!"));
        else
            group.sendMessage(encode("Sorry, that's not numberwang!"));
    }

}
