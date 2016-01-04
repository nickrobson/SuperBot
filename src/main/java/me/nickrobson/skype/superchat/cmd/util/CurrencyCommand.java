package me.nickrobson.skype.superchat.cmd.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.cmd.Command;

public class CurrencyCommand implements Command {

    public static final Pattern CURRENCY_PATTERN   = Pattern.compile("[A-Z]{3}");
    public static final Pattern CONVERSION_PATTERN = Pattern.compile("<span class=\"?bld\"?>([0-9]+(?:\\.[0-9]+)? [A-Z]{3})</span>");

    @Override
    public String[] names() {
        return new String[] { "currency" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[] { "[from] [to] [amount]", "converts [amount] of [from] to [to]" };
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        if (args.length < 3) {
            group.sendMessage(bold(encode("Usage: ")) + encode(PREFIX + "currency [from] [to] [amount]"));
        } else {
            String from = args[0].toUpperCase();
            String to = args[1].toUpperCase();
            double amount = -1;
            if (!CURRENCY_PATTERN.matcher(from).matches()) {
                group.sendMessage(encode(from + " is not a valid currency name. Must be 3 uppercase latin characters."));
                return;
            }
            if (!CURRENCY_PATTERN.matcher(to).matches()) {
                group.sendMessage(encode(to + " is not a valid currency name. Must be 3 uppercase latin characters."));
                return;
            }
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException ex) {
                group.sendMessage(encode(args[2] + " is not a valid number."));
                return;
            }
            if (amount <= 0) {
                group.sendMessage(encode("Conversion amount must be > 0. (" + amount + ")"));
                return;
            }
            String queryURL = String.format("https://www.google.co.uk/finance/converter?from=%s&to=%s&a=%s", from, to, String.valueOf(amount));
            try {
                URL url = new URL(queryURL);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                String line;
                boolean found = false;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = CONVERSION_PATTERN.matcher(line);
                    if (matcher.find()) {
                        group.sendMessage(encode("Conversion Result: " + matcher.group(1)));
                        found = true;
                    }
                }
                if (!found)
                    group.sendMessage(encode("Something went wrong.. didn't find the span tag I was looking for!"));
            } catch (IOException e) {
                group.sendMessage(encode("Something went wrong!") + "\n" + encode(e.getClass().getSimpleName() + " : " + e.getMessage()));
            }
        }
    }

}
