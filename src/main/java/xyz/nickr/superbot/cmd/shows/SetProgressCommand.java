package xyz.nickr.superbot.cmd.shows;

import java.lang.ArrayIndexOutOfBoundsException;
import java.util.Map;
import java.util.regex.Matcher;

import xyz.nickr.jomdb.model.SeasonEpisodeResult;
import xyz.nickr.jomdb.model.SeasonResult;
import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.SuperBotShows;
import xyz.nickr.superbot.SuperBotShows.Show;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Profile;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class SetProgressCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"me", "setprg"};
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] {"[show] [episode]", "set your progress on [show] to [episode]"};
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length < 2) {
            this.sendUsage(sys, user, group);
        } else {
            MessageBuilder<?> mb = sys.message();
            Profile profile = user.getProfile().orElse(null);
            if (profile == null) {
                this.sendNoProfile(sys, user, group);
                return;
            }
            String profileName = profile.getName();
            Show show = SuperBotShows.getShow(args[0]);
            String episodeCodeCommand = args[1].toUpperCase();
            String oldprg = show != null ? SuperBotController.getUserProgress(profileName).get(show) : null;
            if (show == null) {
                mb.escaped("Invalid show name: ").bold(true).escaped(args[0]).bold(false);
            } else if (episodeCodeCommand.equalsIgnoreCase("none") || episodeCodeCommand.equalsIgnoreCase("remove")) {
                Map<String, String> prg = SuperBotController.getProgress(show);
                prg.remove(profileName.toLowerCase());
                SuperBotController.PROGRESS.put(show.imdb, prg);
                mb.escaped("Removed ").bold(true).escaped(profileName).bold(false).escaped("'s progress on ").bold(true).escaped(show.getDisplay());
                SuperBotController.saveProgress();
            } else if (!SuperBotShows.EPISODE_PATTERN.matcher(episodeCodeCommand).matches() && !episodeCodeCommand.equals("NEXT")) {
                mb.escaped("Invalid episode: ").bold(true).escaped(episodeCodeCommand).bold(false).escaped(" (doesn't match S<season>E<episode> format)");
            } else if (oldprg == null && episodeCodeCommand.equals("NEXT")) {
                mb.escaped("You have no progress registered.");
            } else {
                if (oldprg != null && episodeCodeCommand.equals("NEXT")) {
                    String[] spl = oldprg.substring(1).split("E");
                    int de;
                    try {
                        de = args.length > 2 ? Integer.parseInt(args[2]) : 1;
                    } catch (NumberFormatException ex) {
                        group.sendMessage(mb.escaped("Not a number: %s!", args[2]));
                        return;
                    }
                    int episode = Integer.parseInt(spl[1]) - 1 + de;
                    if (de >= 1) {
                        try {
                            SeasonResult res = show.getSeason(spl[0]);
                            SeasonEpisodeResult[] eps = res.getEpisodes();
                            SeasonEpisodeResult r = eps[episode];
                            episodeCodeCommand = String.format("S%sE%s", spl[0], r.getEpisode());
                        } catch (NullPointerException | ArrayIndexOutOfBoundsException ex) {
                            group.sendMessage(mb.escaped("There is no episode ").bold(true).escaped("S%sE%s", spl[0], episode + 1).bold(false).escaped(" for ").bold(true).escaped(show.display).bold(false));
                            return;
                        }
                    } else {
                        group.sendMessage(mb.escaped("Invalid number of episodes to count as next."));
                        return;
                    }
                }

                Matcher matcher = SuperBotShows.EPISODE_PATTERN.matcher(episodeCodeCommand);
                matcher.matches();
                int season = Integer.parseInt(matcher.group(1));
                int episode = Integer.parseInt(matcher.group(2));

                episodeCodeCommand = String.format("S%dE%d", season, episode);

                if (season == 0 || episode == 0) {
                    group.sendMessage(mb.escaped("Invalid season or episode number."));
                    return;
                }

                Map<String, String> prg = SuperBotController.getProgress(show);
                prg.put(profileName.toLowerCase(), episodeCodeCommand);
                SuperBotController.PROGRESS.put(show.imdb, prg);
                mb.escaped("Set ").bold(true).escaped(profileName).bold(false).escaped("'s progress on ").bold(true).escaped(show.getDisplay()).bold(false).escaped(" to ").bold(true).escaped(episodeCodeCommand).bold(false);
                if (oldprg != null) {
                    mb.escaped(" (was %s)", oldprg);
                }
                SuperBotController.saveProgress();
            }
            group.sendMessage(mb);
        }
    }

}
