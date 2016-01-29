package xyz.nickr.superchat.cmd.fun;

import xyz.nickr.superchat.Joiner;
import xyz.nickr.superchat.cmd.Command;
import xyz.nickr.superchat.sys.Group;
import xyz.nickr.superchat.sys.Message;
import xyz.nickr.superchat.sys.Sys;
import xyz.nickr.superchat.sys.User;

public class TypeOutCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{ "typeout" };
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[]{ "[message]", "slowly types a message out" };
    }

    @Override
    public void exec(Sys sys, User user, Group conv, String used, String[] args, Message message) {
        if (args.length == 0) {
            sendUsage(null, user, conv);
            return;
        }
        String str = Joiner.join(" ", args);
        Message msg = conv.sendMessage(" ");
        new Thread(() -> {
            int c = 0;
            while (c < str.length()) {
                try {
                    Thread.sleep(250);
                } catch (Exception e) {}
                msg.edit(sys.message().text(str.substring(0, ++c)));
            }
        }, "TypeOut Thread").start();
    }

}
