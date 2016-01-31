package xyz.nickr.superbot.cmd.fun;

import xyz.nickr.superbot.Joiner;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

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
