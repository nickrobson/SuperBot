package xyz.nickr.superbot.cmd.omdb;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import xyz.nickr.jomdb.JavaOMDB;
import xyz.nickr.jomdb.model.SeasonEpisodeResult;
import xyz.nickr.jomdb.model.SeasonResult;
import xyz.nickr.jomdb.model.TitleResult;
import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class OmdbSeasonsCommand implements Command {

    @Override
    public String[] names() {
        return new String[]{ "omdbseasons" };
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[]{ "[imdbId]", "get information about the show's seasons" };
    }

    String toString(Calendar c) {
        return new SimpleDateFormat("dd MM yyyy").format(c.getTime());
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length < 1) {
            sendUsage(sys, user, group);
        } else {
            MessageBuilder<?> mb = sys.message();
            if (JavaOMDB.IMDB_ID_PATTERN.matcher(args[0]).matches()) {
                TitleResult title = SuperBotController.OMDB.titleById(args[0]);
                mb.italic(true).escaped(title.title + " seasons:").italic(false);
                for (SeasonResult season : title.seasons()) {
                    mb.newLine().bold(true).escaped("Season " + season.season).bold(false);
                    if (season.episodes.length > 0) {
                        SeasonEpisodeResult first = season.episodes[0], last = season.episodes[season.episodes.length - 1];
                        mb.escaped(": " + season.episodes.length + " episodes, " + toString(first.getReleaseDate()) + " - " + toString(last.getReleaseDate()));
                    }
                }
            } else {
                mb.escaped("Invalid IMDB ID (" + args[0] + ")");
            }
            group.sendMessage(mb);
        }
    }

}
