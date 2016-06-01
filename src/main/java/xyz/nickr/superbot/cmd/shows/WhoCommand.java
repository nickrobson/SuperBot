package xyz.nickr.superbot.cmd.shows;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import xyz.nickr.jomdb.model.SeasonEpisodeResult;
import xyz.nickr.jomdb.model.SeasonResult;
import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.SuperBotShows.Show;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Profile;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class WhoCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"who", "whois"};
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] {"(username)", "gets (username)'s progress on all shows"};
    }

    String pad(String s, int len) {
        StringBuilder builder = new StringBuilder(s);
        while (builder.length() < len) {
            builder.insert(builder.indexOf("("), ' ');
        }
        return builder.toString();
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        String prefix = sys.prefix();
        String username;
        if (args.length > 0) {
            Optional<Profile> profile = Profile.getProfile(args[0]);
            if (profile.isPresent()) {
                username = profile.get().getName();
            } else {
                group.sendMessage("No profile with name: " + args[0]);
                return;
            }
        } else {
            Optional<Profile> profile = user.getProfile();
            if (profile.isPresent()) {
                username = profile.get().getName();
            } else {
                group.sendMessage("You need a profile to use this. Use " + prefix + "createprofile.");
                return;
            }
        }
        List<String> shows = new ArrayList<>();
        Map<Show, String> progress = SuperBotController.getUserProgress(username.toLowerCase());

        Calendar now = Calendar.getInstance();

        int maxEpLen = progress.values().stream().mapToInt(String::length).max().orElse(0);

        progress.forEach((show, ep) -> {
            if (show != null) {
                boolean hasNewEpisode = false;

                String[] split = ep.substring(1).split("E");
                int start = Integer.parseInt(split[0]);
                int season = start;
                int episode = Integer.parseInt(split[1]);

                while (true) {
                    SeasonResult res = show.getSeason(String.valueOf(season));
                    boolean done = false;
                    if (res == null) {
                        break;
                    }
                    for (SeasonEpisodeResult ser : res) {
                        try {
                            Calendar release = ser.getReleaseDate();
                            if (release == null || release.after(now)) {
                                done = true;
                                break;
                            }
                            if (season > start || Integer.parseInt(ser.getEpisode()) > episode) {
                                done = hasNewEpisode = true;
                                break;
                            }
                        } catch (NumberFormatException ex) {
                        }
                    }
                    if (done) {
                        break;
                    }
                    season++;
                }
                shows.add(show.getDisplay() + (hasNewEpisode ? " (new)" + this.pad("(", maxEpLen - ep.length() + 1).substring(0, maxEpLen) : "      ") + " (" + ep + ")");
            }
        });
        boolean cols = sys.columns();
        int rows = cols ? shows.size() / 2 + shows.size() % 2 : shows.size();
        shows.sort(String.CASE_INSENSITIVE_ORDER);
        int maxLen1 = shows.subList(0, rows).stream().mapToInt(String::length).max().orElse(0);
        int maxLen2 = shows.subList(rows, shows.size()).stream().mapToInt(String::length).max().orElse(0);
        String s = "";

        for (int i = 0; i < rows; i++) {
            if (shows.size() > i) {
                String t = this.pad(shows.get(i), maxLen1);
                if (cols && shows.size() > rows + i) {
                    t += "   |   " + this.pad(shows.get(rows + i), maxLen2);
                }
                MessageBuilder<?> mb = sys.message().code(true).escaped(t).code(false);
                if (i != rows - 1) {
                    mb.newLine().escaped("   ");
                }
                s += mb.build();
            }
        }

        MessageBuilder<?> mb = sys.message();
        if (shows.size() > 0) {
            group.sendMessage(mb.bold(true).escaped("Shows " + username + " is watching: (" + shows.size() + ")").bold(false).newLine().escaped("   ").raw(s));
        } else {
            group.sendMessage(mb.bold(true).escaped("Error: ").bold(false).escaped("It doesn't look like " + username + " uses me. :("));
        }
    }

}
