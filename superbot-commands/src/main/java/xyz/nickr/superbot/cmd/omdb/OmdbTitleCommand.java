package xyz.nickr.superbot.cmd.omdb;

import xyz.nickr.jomdb.JavaOMDB;
import xyz.nickr.jomdb.model.TitleResult;
import xyz.nickr.superbot.SuperBotResource;
import xyz.nickr.superbot.SuperBotShows;
import xyz.nickr.superbot.SuperBotShows.Show;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class OmdbTitleCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"omdbtitle"};
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[] {"[imdbId] (true/false)", "get title information"};
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length == 0) {
            this.sendUsage(sys, user, group);
        } else {
            MessageBuilder mb = sys.message();
            Show show = SuperBotShows.getShow(args[0], false);
            if (show != null) {
                args[0] = show.getIMDB();
            }
            if (JavaOMDB.IMDB_ID_PATTERN.matcher(args[0]).matches()) {
                boolean fullPlot = args.length > 1 && args[1].equalsIgnoreCase("true");
                TitleResult title = SuperBotResource.OMDB.titleById(args[0], fullPlot);
                mb.bold(true).escaped(title.getTitle()).bold(false).escaped(" (" + title.getImdbID() + ") is a " + title.getGenre() + " " + title.getType());
                mb.newLine().bold(true).escaped("Rating: ").bold(false).escaped(title.getImdbRating() + " from " + title.getImdbVotes() + " votes");
                mb.newLine().bold(true).escaped("Runtime: ").bold(false).escaped(title.getRuntime());
                mb.newLine().bold(true).escaped("Director: ").bold(false).escaped(title.getDirector());
                mb.newLine().bold(true).escaped("Actors: ").bold(false).escaped(title.getActors());
                mb.newLine().bold(true).escaped("Writer: ").bold(false).escaped(title.getWriter());
                mb.newLine().bold(true).escaped("Awards: ").bold(false).escaped(title.getAwards());
                mb.newLine().bold(true).escaped("Plot: ").bold(false).escaped(title.getPlot());
            } else {
                mb.escaped("Invalid IMDB ID (" + args[0] + ")");
            }
            group.sendMessage(mb);
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
