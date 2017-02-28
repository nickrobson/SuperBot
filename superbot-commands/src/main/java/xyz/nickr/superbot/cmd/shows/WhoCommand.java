package xyz.nickr.superbot.cmd.shows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import xyz.nickr.jomdb.model.SeasonEpisodeResult;
import xyz.nickr.jomdb.model.SeasonResult;
import xyz.nickr.superbot.SuperBotResource;
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
        String username;
        if (args.length > 0) {
            Optional<Profile> profile = Profile.getProfile(args[0]);
            if (profile.isPresent()) {
                username = profile.get().getName();
            } else {
                group.sendMessage(sys.message().escaped("No profile with name: " + args[0]));
                return;
            }
        } else {
            Optional<Profile> profile = user.getProfile();
            if (profile.isPresent()) {
                username = profile.get().getName();
            } else {
                this.sendNoProfile(sys, user, group);
                return;
            }
        }
        List<String> shows = new ArrayList<>();
        Map<Show, String> progress = SuperBotResource.getUserProgress(username.toLowerCase());

        LocalDateTime now = LocalDateTime.now();

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
                            LocalDateTime release = ser.getReleaseDate();
                            if (release == null || release.isAfter(now)) {
                                done = true;
                                break;
                            }
                            if (season > start || Integer.parseInt(ser.getEpisode()) > episode) {
                                done = hasNewEpisode = true;
                                break;
                            }
                        } catch (NumberFormatException ex) {}
                    }
                    if (done) {
                        break;
                    }
                    season++;
                }
                shows.add(show.getDisplay() + (hasNewEpisode ? " (new)" + this.pad("(", maxEpLen - ep.length() + 1).substring(0, maxEpLen - ep.length()) : "      ") + " (" + ep + ")");
            }
        });
        shows.sort(String.CASE_INSENSITIVE_ORDER);
        MessageBuilder mb = sys.message();
        if (shows.size() > 0) {
            int maxLen = shows.stream().mapToInt(String::length).max().orElse(0);
            mb.bold(true).escaped("Shows " + username + " is watching: (" + shows.size() + ") ").bold(false).codeblock(true);
            for (int i = 0; i < shows.size(); i++) {
                mb.newLine().escaped(this.pad(shows.get(i), maxLen));
            }
            group.sendMessage(mb.codeblock(false));
        } else {
            group.sendMessage(mb.bold(true).escaped("Error: ").bold(false).escaped("It doesn't look like " + username + " uses me. :("));
        }
    }

}
