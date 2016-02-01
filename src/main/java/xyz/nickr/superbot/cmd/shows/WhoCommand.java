package xyz.nickr.superbot.cmd.shows;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.SuperBotShows.Show;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class WhoCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "who", "whois" };
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] { "(username)", "gets (username)'s progress on all shows" };
    }

    String pad(String s, int len) {
        StringBuilder builder = new StringBuilder(s);
        while (builder.length() < len)
            builder.insert(builder.indexOf("("), ' ');
        return builder.toString();
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        String username = args.length > 0 ? args[0].toLowerCase() : user.getUsername();
        List<String> shows = new ArrayList<>();
        Map<Show, String> progress = SuperBotController.getUserProgress(username);
        progress.forEach((show, ep) -> {
            if (show != null)
                shows.add(show.getDisplay() + "    (" + ep + ")");
        });
        boolean cols = sys.columns();
        int rows = cols ? shows.size() / 2 + shows.size() % 2 : shows.size();
        shows.sort(String.CASE_INSENSITIVE_ORDER);
        int maxLen1 = (cols ? shows.subList(0, rows) : shows).stream().max((s1, s2) -> s1.length() - s2.length()).orElse("").length();
        int maxLen2 = cols ? shows.subList(rows, shows.size()).stream().max((s1, s2) -> s1.length() - s2.length()).orElse("").length() : 0;
        String s = "";
        for (int i = 0; i < rows; i++) {
            if (shows.size() > i) {
                String t = pad(shows.get(i), maxLen1);
                if (cols && shows.size() > rows + i)
                    t += "    |    " + pad(shows.get(rows + i), maxLen2);
                s += sys.message().text(t).build();
                if (i != rows - 1)
                    s += "\n   ";
            }
        }
        MessageBuilder<?> mb = sys.message();
        if (shows.size() > 0)
            group.sendMessage(mb.bold(true).text("Shows " + username + " is watching:").bold(false).html("\n").code(true).text("   " + s));
        else
            group.sendMessage(mb.bold(true).text("Error: ").bold(false).text("It doesn't look like " + username + " uses me. :("));
    }

}
