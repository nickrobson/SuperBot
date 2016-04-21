package xyz.nickr.superbot.cmd.omdb;

import xyz.nickr.jomdb.JavaOMDB;
import xyz.nickr.jomdb.model.SearchResult;
import xyz.nickr.jomdb.model.SearchResults;
import xyz.nickr.jomdb.model.SearchResultsPage;
import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class OmdbSearchCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{ "omdbsearch" };
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[]{ "[search terms...]", "search OMDB" };
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        JavaOMDB omdb = SuperBotController.OMDB;
        MessageBuilder<?> mb = sys.message();
        String search = "";
        int page = 1;
        for (String arg : args) {
            if (arg.startsWith("-page=")) {
                String pg = arg.substring(6);
                try {
                    page = Integer.parseInt(pg);
                } catch (NumberFormatException ex) {
                    group.sendMessage(mb.escaped("Invalid page number (" + pg + ")"));
                    return;
                }
            } else {
                if (!search.isEmpty())
                    search += " ";
                search += arg;
            }
        }
        if (search.length() > 0) {
            SearchResults res = omdb.search(search);
            if (res.getPageCount() == 0) {
                mb.escaped("No results.");
            } else if (page < 1 || page > res.getPageCount()) {
                mb.escaped(String.format("Page number not in [1,%d]", res.getPageCount()));
            } else {
                SearchResultsPage pa = res.getPage(page);
                if (pa.size() > 0) {
                    mb.bold(true).italic(true).escaped("Results (" + page + "/" + res.getPageCount() + "): ").italic(false).bold(false);
                } else {
                    mb.bold(true).italic(true).escaped("No results.").italic(false).bold(false);
                }
                for (SearchResult sr : pa) {
                    mb.newLine().bold(true).escaped(sr.title).bold(false);
                    mb.escaped(" (" + sr.year + "): " + sr.imdbId + ", a " + sr.type);
                }
                if (page != res.getPageCount()) {
                    mb.newLine().escaped("For more results, use ").bold(true).escaped(sys.prefix() + names()[0] + " " + search + " -page=" + (page + 1));
                }
            }
            group.sendMessage(mb);
        } else {
            sendUsage(sys, user, group);
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
