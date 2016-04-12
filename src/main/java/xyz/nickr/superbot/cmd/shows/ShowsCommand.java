package xyz.nickr.superbot.cmd.shows;

import java.util.LinkedList;
import java.util.List;

import xyz.nickr.superbot.SuperBotShows;
import xyz.nickr.superbot.SuperBotShows.Show;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class ShowsCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "shows" };
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] { "", "see which shows are being tracked" };
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        List<String> send = new LinkedList<>();
        for (Show show : SuperBotShows.SHOWS_BY_ID.values()) {
            StringBuilder sb = new StringBuilder();
            for (String s : show.links) {
                if (sb.length() > 0)
                    sb.append(", ");
                sb.append(s);
            }
            if (sb.length() > 0)
                send.add("[" + show.getDisplay() + "] " + sb.toString());
        }
        send.sort(String.CASE_INSENSITIVE_ORDER);
        boolean cols = sys.columns();
        int rows = cols ? send.size() / 2 + send.size() % 2 : send.size();
        int maxLen1 = (cols ? send.subList(0, rows) : send).stream().max((s1, s2) -> s1.length() - s2.length()).orElse("").length();
        MessageBuilder<?> builder = sys.message();
        for (int i = 0; i < rows; i++) {
            String spaces = "";
            for (int j = send.get(i).length(); j < maxLen1; j++)
                spaces += ' ';
            builder.code(true).escaped(send.get(i) + spaces);
            if (cols && send.size() > rows + i) {
                builder.escaped("    " + send.get(rows + i));
            }
            builder.code(false);
            if (i != rows - 1)
                builder.newLine();
        }
        group.sendMessage(builder.build());
    }

}
