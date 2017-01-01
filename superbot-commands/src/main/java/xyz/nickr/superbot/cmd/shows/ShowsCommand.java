package xyz.nickr.superbot.cmd.shows;

import java.util.LinkedList;
import java.util.List;

import java.util.stream.Collectors;
import xyz.nickr.superbot.SuperBotShows;
import xyz.nickr.superbot.SuperBotShows.Show;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.PaginatedData;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class ShowsCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"shows"};
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] {"", "see which shows are being tracked"};
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        List<String> snd = new LinkedList<>();
        for (Show show : SuperBotShows.getShows()) {
            StringBuilder sb = new StringBuilder();
            for (String s : show.getLinks()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(s);
            }
            if (sb.length() > 0) {
                snd.add("[" + show.getDisplay() + "] " + sb.toString());
            }
        }
        snd.sort(String.CASE_INSENSITIVE_ORDER);
        List<MessageBuilder> mbs = snd.stream().map(s -> sys.message().escaped(s)).collect(Collectors.toList());
        PaginatedData pages = new PaginatedData(mbs, 20, true);
        final int maxpages = pages.getNumberOfPages();
        int page;
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
                if (page <= 0 || page > maxpages) {
                    final int x = page;
                    group.sendMessage(sys.message().bold(m -> m.escaped("Invalid page: %d, not in [1, %d]", x, maxpages)));
                } else {
                    pages.send(sys, group, page - 1);
                }
            } catch (Exception ex) {
                group.sendMessage(sys.message().bold(m -> m.escaped("Not a number: %s", args[0])));
            }
        } else {
            pages.send(sys, group, 0);
        }
    }

}
