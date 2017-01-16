package xyz.nickr.superbot.cmd.shows;

import java.util.LinkedList;
import java.util.List;

import java.util.Map;
import java.util.TreeMap;
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
        Map<String, MessageBuilder> snd = new TreeMap<>();
        for (Show show : SuperBotShows.getShows()) {
            String links = String.join(", ", show.getLinks());
            if (!links.isEmpty())
                snd.put(show.getDisplay(), sys.message().italic(z -> z.escaped(show.getDisplay())).escaped(": " + links));
        }
        List<MessageBuilder> list = new LinkedList<>(snd.values());
        PaginatedData pages = new PaginatedData(list, 20, false);
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
