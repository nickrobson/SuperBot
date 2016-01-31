package xyz.nickr.superbot.cmd.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class CurrencyCommand implements Command {

    public static final Pattern CURRENCY_PATTERN   = Pattern.compile("[A-Z]{3}");
    public static final Pattern CONVERSION_PATTERN = Pattern.compile("<span class=\"?bld\"?>([0-9]+(?:\\.[0-9]+)? [A-Z]{3})</span>");

    @Override
    public String[] names() {
        return new String[] { "currency" };
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] { "[from] [to] [amount]", "converts [amount] of [from] to [to]" };
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length < 3) {
            sendUsage(null, user, group);
        } else {
            String from = args[0].toUpperCase();
            String to = args[1].toUpperCase();
            double amount = -1;
            if (!CURRENCY_PATTERN.matcher(from).matches()) {
                group.sendMessage(sys.message().text(from + " is not a valid currency name. Must be 3 uppercase latin characters."));
                return;
            }
            if (!CURRENCY_PATTERN.matcher(to).matches()) {
                group.sendMessage(sys.message().text(to + " is not a valid currency name. Must be 3 uppercase latin characters."));
                return;
            }
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException ex) {
                group.sendMessage(sys.message().text(args[2] + " is not a valid number."));
                return;
            }
            if (amount <= 0) {
                group.sendMessage(sys.message().text("Conversion amount must be > 0. (" + amount + ")"));
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
                        group.sendMessage(sys.message().bold(true).text("Conversion Result: ").bold(false).text(matcher.group(1)));
                        found = true;
                    }
                }
                if (!found)
                    group.sendMessage(sys.message().text("Something went wrong.. didn't find the span tag I was looking for!"));
            } catch (IOException e) {
                group.sendMessage(sys.message().text("Something went wrong!").newLine().text(e.getClass().getSimpleName() + " : " + e.getMessage()));
            }
        }
    }

}
