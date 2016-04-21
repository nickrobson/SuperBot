package xyz.nickr.superbot.cmd.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class PasteFetchCommand implements Command {

    public static Pattern PASTEBIN = Pattern.compile("https?://(?:www\\.)?pastebin\\.com/(?:raw/)?([a-zA-Z0-9]+)");
    public static Pattern HASTEBIN = Pattern.compile("https?://(?:www\\.)?hastebin\\.com/(?:raw/)?([a-zA-Z0-9]+)(?:\\.[a-zA-Z0-9]+)?");
    public static Pattern NICKR = Pattern.compile("https?://(?:www\\.)?nickr\\.xyz/paste/(?:raw/)?([a-zA-Z0-9]+)");

    @Override
    public String[] names() {
        return new String[]{ "pastefetch" };
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[]{ "(-m) [url]", "fetches paste from URL, optionally Markdown" };
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        MessageBuilder<?> mb = sys.message();
        String url = "";
        boolean md = false;
        for (String arg : args) {
            if (arg.equals("-m"))
                md = true;
            else
                url += arg + " ";
        }
        final boolean markdown = md;
        if ((url = url.trim()).isEmpty()) {
            sendUsage(sys, user, group);
            return;
        }
        try {
            boolean match = true;
            Matcher m;
            List<String> lines = new LinkedList<>();
            if ((m = PASTEBIN.matcher(url)).matches()) {
                scrape(url, "http://pastebin.com/raw/" + m.group(1), mb, lines);
            } else if ((m = HASTEBIN.matcher(url)).matches()) {
                scrape(url, "http://hastebin.com/raw/" + m.group(1), mb, lines);
            } else if ((m = NICKR.matcher(url)).matches()) {
                scrape(url, "http://nickr.xyz/paste/raw/" + m.group(1), mb, lines);
            } else {
                mb.escaped(url + " does not match any providers.");
                match = false;
            }
            if (match) {
                if (markdown)
                    lines.forEach(l -> mb.newLine().raw(l.replaceAll("(\\b|^|[^*])(\\*)(\\b|[^*])", "$1_$3").replaceAll("\\*\\*", "*")));
                else
                    lines.forEach(l -> mb.newLine().escaped(l));
            }
        } catch (Exception ex) {
            mb.escaped("An error occurred: " + ex.getClass().getSimpleName());
        }
        try {
            group.sendMessage(mb);
        } catch (Exception ex) {
            group.sendMessage(sys.message().escaped("Invalid Markdown formatting."));
        }
    }

    public void scrape(String url, String raw, MessageBuilder<?> mb, List<String> lines) {
        try {
            URLConnection conn = new URL(raw).openConnection();
            String type = conn.getContentType();
            if (type != null && type.startsWith("text/plain")) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    mb.bold(true).escaped("Paste data from " + url + ":").bold(false);
                    reader.lines().forEach(lines::add);
                } catch (Exception ex) {
                    mb.escaped("An error occurred: " + ex.getClass().getSimpleName());
                }
            } else {
                mb.escaped("Invalid paste ID!");
            }
        } catch (Exception ex) {
            mb.escaped("An error occurred: " + ex.getClass().getSimpleName());
        }
    }

    @Override
    public boolean alwaysEnabled() {
        return true;
    }

    @Override
    public boolean userchat() {
        return true;
    }

}
