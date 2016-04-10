package xyz.nickr.superbot.cmd.omdb;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private String pad(String s, int len) {
        while (s.length() < len)
            s += " ";
        return s;
    }

    String toString(Sys sys, SeasonEpisodeResult episode) {
        return sys.message().code(true).escaped("E" + episode.episode + " (" + episode.imdbRating + "): " + episode.title).build();
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
                List<SeasonEpisodeResult> episodes = Arrays.asList(season.episodes);
                List<String> infos = episodes.stream().map(s -> toString(sys, s)).collect(Collectors.toList());
                boolean cols = sys.columns();
                int rows = cols ? episodes.size() / 2 + episodes.size() % 2 : episodes.size();
                int maxLen = infos.subList(0, rows).stream().mapToInt(s -> s.length()).max().orElse(0);
                for (int i = 0; i < rows; i++) {
                    String s = pad(infos.get(i), maxLen);
                    if (cols && episodes.size() > i + rows) {
                        s += "  |  " + infos.get(i + rows);
                    }
                    mb.newLine().raw(s);
                }
            } else {
                mb.escaped("Invalid IMDB ID (" + args[0] + ")");
            }
            group.sendMessage(mb);
        }
    }

}
