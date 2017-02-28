package xyz.nickr.superbot.cmd.omdb;

import xyz.nickr.jomdb.JavaOMDB;
import xyz.nickr.jomdb.model.SeasonEpisodeResult;
import xyz.nickr.jomdb.model.SeasonResult;
import xyz.nickr.superbot.SuperBotResource;
import xyz.nickr.superbot.SuperBotShows;
import xyz.nickr.superbot.SuperBotShows.Show;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class OmdbEpisodeCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"omdbepisode"};
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[] {"[imdbId] [season] [ep]", "get episode information"};
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length < 3) {
            this.sendUsage(sys, user, group);
        } else {
            MessageBuilder mb = sys.message();
            Show show = SuperBotShows.getShow(args[0], false);
            if (show != null) {
                args[0] = show.getIMDB();
            }
            if (JavaOMDB.IMDB_ID_PATTERN.matcher(args[0]).matches()) {
                SeasonResult season = SuperBotResource.OMDB.seasonById(args[0], args[1]);
                for (SeasonEpisodeResult episode : season) {
                    if (args[2].equals(episode.getEpisode())) {
                        mb.bold(true).escaped(season.getTitle()).bold(false).escaped(" S" + season.getSeason() + "E" + episode.getEpisode() + ":");
                        mb.newLine().bold(true).escaped(episode.getTitle()).bold(false);
                        mb.escaped(" (" + episode.getImdbRating() + ", " + episode.getRelease() + ")");
                        mb.newLine().escaped("For more information, use ");
                        mb.bold(true).escaped(sys.prefix() + "omdbtitle " + episode.getImdbId() + " (true|false)");
                        break;
                    }
                }
                if (mb.isEmpty()) {
                    mb.escaped("No such episode.");
                }
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
