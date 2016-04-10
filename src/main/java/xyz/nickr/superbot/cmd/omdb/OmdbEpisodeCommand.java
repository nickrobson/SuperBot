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

public class OmdbEpisodeCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{ "omdbepisode" };
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[]{ "[imdbId] [season] [ep]", "get episode information" };
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length < 3) {
            sendUsage(sys, user, group);
        } else {
            MessageBuilder<?> mb = sys.message();
            if (JavaOMDB.IMDB_ID_PATTERN.matcher(args[0]).matches()) {
                SeasonResult season = SuperBotController.OMDB.seasonById(args[0], args[1]);
                for (SeasonEpisodeResult episode : season) {
                    if (args[2].equals(episode.episode)) {
                        mb.bold(true).escaped(season.title).bold(false).escaped(" S" + season.season + "E" + episode.episode + ":");
                        mb.newLine().bold(true).escaped(episode.title).bold(false);
                        mb.escaped(" (" + episode.imdbRating + ", " + episode.released + ")");
                        mb.newLine().escaped("For more information, use ");
                        mb.bold(true).escaped(sys.prefix() + "omdbtitle " + episode.imdbId + " (true|false)");
                        break;
                    }
                }
                if (mb.length() == 0) {
                    mb.escaped("No such episode.");
                }
            } else {
                mb.escaped("Invalid IMDB ID (" + args[0] + ")");
            }
            group.sendMessage(mb);
        }
    }

}
