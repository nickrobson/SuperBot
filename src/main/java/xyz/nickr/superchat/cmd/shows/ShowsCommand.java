package xyz.nickr.superchat.cmd.shows;

import java.util.LinkedList;
import java.util.List;

import xyz.nickr.superchat.SuperChatShows;
import xyz.nickr.superchat.SuperChatShows.Show;
import xyz.nickr.superchat.cmd.Command;
import xyz.nickr.superchat.sys.Group;
import xyz.nickr.superchat.sys.Message;
import xyz.nickr.superchat.sys.MessageBuilder;
import xyz.nickr.superchat.sys.Sys;
import xyz.nickr.superchat.sys.User;

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
    public void exec(Sys sys, User user, Group conv, String used, String[] args, Message message) {
        List<String> send = new LinkedList<>();
        for (Show show : SuperChatShows.TRACKED_SHOWS) {
            StringBuilder sb = new StringBuilder();
            for (String s : show.getNames()) {
                if (sb.length() > 0)
                    sb.append(", ");
                sb.append(s);
            }
            if (sb.length() > 0)
                send.add(sys.message().code(true).text("[" + show.getDisplay() + "] " + sb.toString()).build());
        }
        send.sort(String.CASE_INSENSITIVE_ORDER);
        int rows = send.size() / 2 + send.size() % 2;
        int maxLen1 = send.subList(0, rows).stream().max((s1, s2) -> s1.length() - s2.length()).orElse("").length();
        MessageBuilder<?> builder = sys.message();
        for (int i = 0; i < rows; i++) {
            String spaces = "";
            for (int j = send.get(i).length(); j < maxLen1; j++)
                spaces += ' ';
            builder.html(send.get(i)).code(true).text(spaces);
            if (send.size() > rows + i) {
                builder.text("    ").html(send.get(rows + i));
            }
            if (i != rows - 1)
                builder.newLine();
        }
        conv.sendMessage(builder.build());
    }

}
