package xyz.nickr.superbot.cmd.omdb;

import xyz.nickr.jomdb.JavaOMDB;
import xyz.nickr.jomdb.model.TitleResult;
import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class OmdbTitleCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{ "omdbtitle" };
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[]{ "[imdbId] (fullplot)", "get title information" };
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length == 0) {
            sendUsage(sys, user, group);
        } else {
            MessageBuilder<?> mb = sys.message();
            if (JavaOMDB.IMDB_ID_PATTERN.matcher(args[0]).matches()) {
                boolean fullPlot = args.length > 1 && args[1].equalsIgnoreCase("true");
                TitleResult title = SuperBotController.OMDB.titleById(args[0], fullPlot);
                mb.bold(true).escaped(title.title).bold(false).escaped(" (" + title.imdbID + ") is a " + title.genre + " " + title.type);
                mb.newLine().bold(true).escaped("Rating: ").bold(false).escaped(title.imdbRating + " from " + title.imdbVotes + " votes");
                mb.newLine().bold(true).escaped("Runtime: ").bold(false).escaped(title.runtime);
                mb.newLine().bold(true).escaped("Director: ").bold(false).escaped(title.director);
                mb.newLine().bold(true).escaped("Actors: ").bold(false).escaped(title.actors);
                mb.newLine().bold(true).escaped("Writer: ").bold(false).escaped(title.writer);
                mb.newLine().bold(true).escaped("Awards: ").bold(false).escaped(title.awards);
                mb.newLine().bold(true).escaped("Plot: ").bold(false).escaped(title.plot);
            } else {
                mb.escaped("Invalid IMDB ID (" + args[0] + ")");
            }
            group.sendMessage(mb);
        }
    }

}
