package me.nickrobson.skype.superchat.cmd;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.Joiner;

public class TypeOutCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{ "typeout" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[]{ "[message]", "slowly types it out" };
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        String str = Joiner.join(" ", args);
        int c = 0;
        SkypeMessage msg = group.sendMessage(" ");
        while (c != str.length()) {
            final int x = ++c;
            new Thread(() -> {
                try {
                    Thread.sleep(x * 100);
                } catch (Exception e) {}
                msg.edit(str.substring(0, x));
            }, "TypeOut Thread").start();
        }
    }

}
