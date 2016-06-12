package xyz.nickr.superbot.cmd.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class ColourCommand implements Command {

    public static final Pattern PATTERN_COLOUR = Pattern.compile("(?:[A-F0-9]{2,3})|(?:[A-F0-9]{5,6})");

    @Override
    public String[] names() {
        return new String[] {"colour", "color"};
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[] {"[hex]", "get an image of the colour"};
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length > 0) {
            String colour = args[0].toUpperCase();
            while (colour.startsWith("#")) {
                colour = colour.substring(1);
            }
            List<String> colours = new LinkedList<>();
            if (PATTERN_COLOUR.matcher(colour).matches()) {
                String hex = "0123456789ABCDEF";
                if (group.supportsMultiplePhotos() && colour.length() == 2) {
                    for (char z : hex.toCharArray()) {
                        String co = colour + z;
                        String c = "";
                        for (int x = 0; x < 6; x++) {
                            c += co.charAt(x / 2);
                        }
                        colours.add(c);
                    }
                } else if (group.supportsMultiplePhotos() && colour.length() == 5) {
                    for (char c : hex.toCharArray()) {
                        colours.add(colour + c);
                    }
                } else if (colour.length() == 3 || colour.length() == 6) {
                    String c = colour.length() == 3 ? "" : colour;
                    if (colour.length() == 3) {
                        for (int i = 0; i < 6; i++) {
                            c += colour.charAt(i / 2);
                        }
                    }
                    colours.add(c);
                } else {
                    group.sendMessage(sys.message().escaped("Invalid colour."));
                }
                for (String co : colours) {
                    try {
                        URL url = new URL("http://nickr.xyz/photo/" + co + ".jpg");
                        group.sendPhoto(url);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                group.sendMessage(sys.message().escaped("Not hexadecimal: '%s'", colour));
            }
        } else {
            this.sendUsage(sys, user, group);
        }
    }

}
