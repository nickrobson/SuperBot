package xyz.nickr.superbot.cmd.shows;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class ViewingOrderCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"vo", "viewingorder"};
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] {"[mcu,af]", "gets the show's advised viewing order"};
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        MessageBuilder mb = sys.message();
        try {
            if (args.length == 0) {
                this.sendUsage(sys, user, group);
            } else {
                InputStream stream = this.getClass().getResourceAsStream("/viewingorder/" + args[0].toLowerCase() + ".txt");
                if (stream == null) {
                    mb.escaped("No viewing order found with name " + args[0].toLowerCase());
                } else {
                    mb.bold(true).escaped("Viewing order for " + args[0].toLowerCase() + ":").bold(false);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        mb.newLine();
                        if (line.startsWith("**")) {
                            mb.bold(true).escaped(line.substring(2)).bold(false);
                        } else if (line.startsWith("//")) {
                            mb.italic(true).escaped(line.substring(2)).italic(false);
                        } else {
                            mb.escaped(line);
                        }
                    }
                    reader.close();
                }
            }
            group.sendMessage(mb);
        } catch (Exception ex) {
            group.sendMessage(mb.escaped("Looks like an error occurred!"));
            ex.printStackTrace();
        }
    }

}
