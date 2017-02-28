package xyz.nickr.superbot.cmd.omdb;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import java.util.Locale;
import xyz.nickr.jomdb.JavaOMDB;
import xyz.nickr.jomdb.model.SeasonEpisodeResult;
import xyz.nickr.jomdb.model.SeasonResult;
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

public class OmdbSeasonsCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"omdbseasons"};
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[] {"[imdbId]", "get information about the show's seasons"};
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM ''uu").withLocale(Locale.US);

    String toString(LocalDateTime ldt) {
        return ldt != null ? formatter.format(ldt) : "not released";
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length < 1) {
            this.sendUsage(sys, user, group);
        } else {
            MessageBuilder mb = sys.message();
            Show show = SuperBotShows.getShow(args[0], false);
            List<SeasonResult> seasons = new LinkedList<>();
            String showName = "";
            int totalSeasons = -1;
            if (show != null) {
                args[0] = show.getIMDB();
                seasons.addAll(show.getSeasons());
                showName = show.getDisplay();
                totalSeasons = show.getTotalSeasons();
            } else {
                if (JavaOMDB.IMDB_ID_PATTERN.matcher(args[0]).matches()) {
                    TitleResult title = SuperBotResource.OMDB.titleById(args[0]);
                    showName = title.getTitle();
                    totalSeasons = title.getTotalSeasons();
                    for (SeasonResult season : title) {
                        seasons.add(season);
                    }
                } else {
                    mb.escaped("Invalid IMDB ID (" + args[0] + ")");
                    group.sendMessage(mb);
                    return;
                }
            }
            String extra = totalSeasons > 0 ? " (" + totalSeasons + " total)" : "";
            mb.italic(true).escaped(showName + " seasons" + extra + ":").italic(false);
            for (SeasonResult season : seasons) {
                mb.newLine().bold(true).escaped("Season " + season.getSeason()).bold(false);
                SeasonEpisodeResult[] episodes = season.getEpisodes();
                if (episodes.length > 0) {
                    SeasonEpisodeResult first = episodes[0],
                                    last = episodes[episodes.length - 1];
                    mb.escaped(": " + episodes.length + " episodes, " + this.toString(first.getReleaseDate()) + " - " + this.toString(last.getReleaseDate()));
                }
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
