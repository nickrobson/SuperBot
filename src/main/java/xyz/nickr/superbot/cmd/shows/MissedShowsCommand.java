package xyz.nickr.superbot.cmd.shows;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import xyz.nickr.jomdb.JOMDBException;
import xyz.nickr.jomdb.model.SeasonEpisodeResult;
import xyz.nickr.jomdb.model.SeasonResult;
import xyz.nickr.superbot.Joiner;
import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.SuperBotShows.Show;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Profile;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class MissedShowsCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"missedshows", "missed"};
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[] {"", "shows episodes that you have missed"};
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        Profile prof = user.getProfile().orElse(null);
        if (prof == null) {
            this.sendNoProfile(sys, user, group);
            return;
        }
        Map<Show, String> progress = SuperBotController.getUserProgress(prof.getName());
        Calendar now = Calendar.getInstance();
        List<String> lines = new LinkedList<>();
        for (Entry<Show, String> entry : progress.entrySet()) {
            Show show = entry.getKey();
            if (show == null) {
                continue;
            }
            String[] spl = entry.getValue().substring(1).split("E");
            int episode = Integer.parseInt(spl[1]);
            try {
                SeasonResult sres = SuperBotController.OMDB.seasonById(show.imdb, spl[0]);
                List<String> missed = new LinkedList<>();
                for (SeasonEpisodeResult ep : sres) {
                    try {
                        Calendar release = ep.getReleaseDate();
                        if (release == null || release.after(now)) {
                            break;
                        }
                        if (!missed.isEmpty() || Integer.parseInt(ep.getEpisode()) > episode) {
                            missed.add(String.format("S%sE%s", spl[0], ep.getEpisode()));
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
                if (!missed.isEmpty()) {
                    MessageBuilder<?> mb = sys.message();
                    String line = Joiner.join(", ", missed);
                    lines.add(mb.bold(m -> m.escaped(show.getDisplay())).escaped(": ").escaped(line).build());
                }
            } catch (JOMDBException ex) {
                ex.printStackTrace();
            }
        }
        MessageBuilder<?> mb = sys.message();
        if (lines.isEmpty()) {
            mb.escaped("You're up to date!");
        } else {
            mb.escaped("You've missed:");
            lines.forEach(l -> mb.newLine().raw(l));
        }
        group.sendMessage(mb);
    }

}
