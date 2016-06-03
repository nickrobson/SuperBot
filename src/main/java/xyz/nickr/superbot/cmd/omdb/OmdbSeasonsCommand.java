package xyz.nickr.superbot.cmd.omdb;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import xyz.nickr.jomdb.JavaOMDB;
import xyz.nickr.jomdb.model.SeasonEpisodeResult;
import xyz.nickr.jomdb.model.SeasonResult;
import xyz.nickr.jomdb.model.TitleResult;
import xyz.nickr.superbot.SuperBotController;
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

    String toString(Calendar c) {
        return c != null ? new SimpleDateFormat("d MMM yyyy").format(c.getTime()) : "not released";
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length < 1) {
            this.sendUsage(sys, user, group);
        } else {
            MessageBuilder<?> mb = sys.message();
            Show show = SuperBotShows.getShow(args[0], false);
            List<SeasonResult> seasons = new LinkedList<>();
            if (show != null) {
                args[0] = show.imdb;
                seasons.addAll(show.getSeasons());
            } else {
                if (JavaOMDB.IMDB_ID_PATTERN.matcher(args[0]).matches()) {
                    TitleResult title = SuperBotController.OMDB.titleById(args[0]);
                    mb.italic(true).escaped(title.getTitle() + " seasons:").italic(false);
                    for (SeasonResult season : title.seasons()) {
                        seasons.add(season);
                    }
                } else {
                    mb.escaped("Invalid IMDB ID (" + args[0] + ")");
                    group.sendMessage(mb);
                    return;
                }
            }
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
