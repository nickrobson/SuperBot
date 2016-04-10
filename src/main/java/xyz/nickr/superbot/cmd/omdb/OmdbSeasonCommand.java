package xyz.nickr.superbot.cmd.omdb;

import xyz.nickr.jomdb.JavaOMDB;
import xyz.nickr.jomdb.model.SeasonEpisodeResult;
import xyz.nickr.jomdb.model.SeasonResult;
import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class OmdbSeasonCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{ "omdbseason" };
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[]{ "[imdbId] [season]", "get season information" };
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length < 2) {
            sendUsage(sys, user, group);
        } else {
            MessageBuilder<?> mb = sys.message();
            if (JavaOMDB.IMDB_ID_PATTERN.matcher(args[0]).matches()) {
                SeasonResult season = SuperBotController.OMDB.seasonById(args[0], args[1]);
                mb.escaped(season.title + ", season " + args[1] + ": ");
                for (SeasonEpisodeResult episode : season) {
                    mb.newLine().bold(true).escaped("E" + episode.episode + " (" + episode.imdbRating + ")").bold(false);
                    mb.escaped(": " + episode.title);
                }
            } else {
                mb.escaped("Invalid IMDB ID (" + args[0] + ")");
            }
            group.sendMessage(mb);
        }
    }

}
