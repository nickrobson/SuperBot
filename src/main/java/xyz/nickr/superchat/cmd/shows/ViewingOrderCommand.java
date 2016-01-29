package xyz.nickr.superchat.cmd.shows;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import xyz.nickr.superchat.cmd.Command;
import xyz.nickr.superchat.sys.Group;
import xyz.nickr.superchat.sys.Message;
import xyz.nickr.superchat.sys.MessageBuilder;
import xyz.nickr.superchat.sys.Sys;
import xyz.nickr.superchat.sys.User;

public class ViewingOrderCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "vo", "viewingorder" };
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] { "[mcu,af]", "shows the advised viewing order for the show(s)" };
    }

    @Override
    public void exec(Sys sys, User user, Group conv, String used, String[] args, Message message) {
        MessageBuilder<?> mb = sys.message();
        try {
            if (args.length == 0)
                sendUsage(null, user, conv);
            else {
                InputStream stream = getClass().getResourceAsStream("/viewingorder/" + args[0].toLowerCase() + ".txt");
                if (stream == null)
                    mb.text("No viewing order found with name " + args[0].toLowerCase());
                else {
                    mb.bold(true).text("Viewing order for " + args[0].toLowerCase() + ":").bold(false);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        mb.html("\n");
                        if (line.startsWith("**"))
                            mb.bold(true).text(line.substring(2)).bold(false);
                        else if (line.startsWith("//"))
                            mb.italic(true).text(line.substring(2)).italic(false);
                        else
                            mb.text(line);
                    }
                    reader.close();
                }
            }
            conv.sendMessage(mb.build());
        } catch (Exception ex) {
            conv.sendMessage(mb.text("Looks like an error occurred!"));
            ex.printStackTrace();
        }
    }

}
