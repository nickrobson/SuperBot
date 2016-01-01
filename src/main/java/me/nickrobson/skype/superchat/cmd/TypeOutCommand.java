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
        SkypeMessage msg = group.sendMessage(" ");
        new Thread(() -> {
            int c = 0;
            while (c != str.length()) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
                msg.edit(encode(str.substring(0, c)));
            }
        }, "TypeOut Thread").start();
    }

}
