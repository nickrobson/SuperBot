package xyz.nickr.superbot.cmd.omdb;

import java.util.LinkedList;
import java.util.List;

import xyz.nickr.jomdb.JavaOMDB;
import xyz.nickr.jomdb.model.SearchResult;
import xyz.nickr.jomdb.model.SearchResults;
import xyz.nickr.jomdb.model.SearchResultsPage;
import xyz.nickr.superbot.SuperBotResource;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.PaginatedData;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class OmdbSearchCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"omdbsearch"};
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[] {"[search terms...]", "search OMDB"};
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        JavaOMDB omdb = SuperBotResource.OMDB;
        String search = "";
        int page = 1;
        for (String arg : args) {
            if (arg.startsWith("-page=")) {
                String pg = arg.substring(6);
                try {
                    page = Integer.parseInt(pg);
                } catch (NumberFormatException ex) {
                    group.sendMessage(sys.message().escaped("Invalid page number (" + pg + ")"));
                    return;
                }
            } else {
                if (!search.isEmpty()) {
                    search += " ";
                }
                search += arg;
            }
        }
        if (search.length() > 0) {
            SearchResults res = omdb.search(search);
            if (res == null || res.getPageCount() == 0) {
                group.sendMessage(sys.message().escaped("No results."));
                return;
            }
            int maxPages = Math.min(10, res.getPageCount());
            if (page <= 0 || page > maxPages) {
                group.sendMessage(sys.message().escaped("Invalid page: %d, not in [%d, %d]", page, 1, maxPages));
                return;
            }
            if (maxPages > 0) {
                List<MessageBuilder> lines = new LinkedList<>();
                for (int i = 1; i <= maxPages; i++) {
                    SearchResultsPage pa = res.getPage(i);
                    for (SearchResult sr : pa) {
                        MessageBuilder m = sys.message();
                        m.bold(true).escaped(sr.getTitle()).bold(false);
                        m.escaped(" (" + sr.getYear() + "): " + sr.getImdbId() + ", a " + sr.getType());
                        lines.add(m);
                    }
                }
                PaginatedData pages = new PaginatedData(lines, 20, true);
                pages.send(sys, group, page - 1);
            } else {
                group.sendMessage(sys.message().escaped("No results."));
            }
        } else {
            this.sendUsage(sys, user, group);
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
