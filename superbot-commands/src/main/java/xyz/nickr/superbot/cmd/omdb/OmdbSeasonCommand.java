package xyz.nickr.superbot.cmd.omdb;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

public class OmdbSeasonCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"omdbseason"};
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[] {"[imdbId] [season]", "get season information"};
    }

    private String pad(String s, int len) {
        while (s.length() < len) {
            s += " ";
        }
        return s;
    }

    String toString(Sys sys, SeasonEpisodeResult episode) {
        return "E" + episode.getEpisode() + " (" + episode.getImdbRating() + "): " + episode.getTitle();
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length < 2) {
            this.sendUsage(sys, user, group);
        } else {
            MessageBuilder mb = sys.message();
            Show show = SuperBotShows.getShow(args[0], false);
            if (show != null) {
                args[0] = show.getIMDB();
            }
            if (JavaOMDB.IMDB_ID_PATTERN.matcher(args[0]).matches()) {
                SeasonResult season = SuperBotResource.OMDB.seasonById(args[0], args[1]);
                if (season == null) {
                    group.sendMessage(mb.escaped("Failed to find a show with id of %s or season %s", args[0], args[1]));
                    return;
                }
                mb.bold(true).escaped(season.getTitle()).bold(false).escaped(", season " + args[1] + ":");
                List<SeasonEpisodeResult> episodes = Arrays.asList(season.getEpisodes());
                List<String> infos = episodes.stream().map(s -> this.toString(sys, s)).collect(Collectors.toList());
                int maxLen = infos.stream().mapToInt(s -> s.length()).max().orElse(0);
                for (int i = 0; i < infos.size(); i++) {
                    mb.newLine().code(true).escaped(this.pad(infos.get(i), maxLen)).code(false);
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
