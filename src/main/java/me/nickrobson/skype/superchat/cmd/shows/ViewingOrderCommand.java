package me.nickrobson.skype.superchat.cmd.shows;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.MessageBuilder;
import me.nickrobson.skype.superchat.cmd.Command;

public class ViewingOrderCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "vo", "viewingorder" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[] { "[mcu,af]", "shows the advised viewing order for the show" };
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        try {
            MessageBuilder builder = new MessageBuilder();
            if (args.length == 0)
                builder.bold(true).text("Usage: ").bold(false).text(PREFIX + "vo [mcu,af]");
            else {
                InputStream stream = getClass().getResourceAsStream("/viewingorder/" + args[0].toLowerCase() + ".txt");
                if (stream == null)
                    builder.text("No viewing order found with name " + args[0].toLowerCase());
                else {
                    builder.bold(true).text("Viewing order for " + args[0].toLowerCase() + ":").bold(false);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.html("\n");
                        String s = "";
                        if (line.startsWith("**"))
                            s = bold(encode(line.substring(2)));
                        else if (line.startsWith("//"))
                            s = italic(encode(line.substring(2)));
                        else
                            s = encode(line);
                        builder.html(s);
                    }
                    reader.close();
                }
            }
            group.sendMessage(builder.build());
        } catch (Exception ex) {
            group.sendMessage(encode("Looks like an error occurred!"));
            ex.printStackTrace();
        }
    }

}
