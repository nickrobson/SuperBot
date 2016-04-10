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
        return new String[]{ "[imdbId]", "get title information" };
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length == 0) {
            sendUsage(sys, user, group);
        } else {
            MessageBuilder<?> mb = sys.message();
            if (JavaOMDB.IMDB_ID_PATTERN.matcher(args[0]).matches()) {
                TitleResult title = SuperBotController.OMDB.titleById(args[0]);
                mb.escaped(title.title + " (" + title.imdbID + ") is a " + title.genre + " " + title.type + ", " + title.runtime);
                mb.newLine().escaped(title.imdbRating + " from " + title.imdbVotes + "votes");
                mb.newLine().escaped("Director: " + title.director);
                mb.newLine().escaped("Actors: " + title.actors);
                mb.newLine().escaped("Writer: " + title.writer);
                mb.newLine().escaped("Awards: " + title.awards);
            } else {
                mb.escaped("Invalid IMDB ID (" + args[0] + ")");
            }
            group.sendMessage(mb);
        }
    }

}
