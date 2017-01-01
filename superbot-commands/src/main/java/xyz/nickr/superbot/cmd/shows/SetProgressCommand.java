package xyz.nickr.superbot.cmd.shows;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.regex.Matcher;

import xyz.nickr.jomdb.model.SeasonEpisodeResult;
import xyz.nickr.jomdb.model.SeasonResult;
import xyz.nickr.superbot.SuperBotResource;
import xyz.nickr.superbot.SuperBotShows;
import xyz.nickr.superbot.SuperBotShows.Show;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.*;

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
        if (args.length < 1) {
            this.sendUsage(sys, user, group);
        } else {
            MessageBuilder mb = sys.message();
            Profile profile = user.getProfile().orElse(null);
            if (profile == null) {
                this.sendNoProfile(sys, user, group);
                return;
            }
            String profileName = profile.getName();
            Show show = SuperBotShows.getShow(args[0]);
            String episodeCode = args.length > 1 ? args[1].toUpperCase() : null;
            String oldprg = show != null ? SuperBotResource.getUserProgress(profileName).get(show) : null;
            if (show == null) {
                mb.escaped("Invalid show name: ").bold(true).escaped(args[0]).bold(false);
            } else if (args.length < 2) {
                if (sys.hasKeyboards()) {
                    Keyboard kb = new Keyboard();
                    KeyboardRow kbr = new KeyboardRow();
                    BiFunction<String, String, KeyboardButtonResponse> setProgress = (pname, s) -> {
                        pname = pname.toLowerCase();
                        Map<String, String> prg = SuperBotResource.getProgress(show);
                        String opr = prg.get(pname);
                        prg.put(pname, s);
                        SuperBotResource.PROGRESS.put(show.getIMDB(), prg);
                        SuperBotResource.saveProgress();
                        return new KeyboardButtonResponse("Set progress to " + s + (opr != null ? " (was " + opr + ")" : ""), true);
                    };
                    kbr.add(new KeyboardButton("«", u -> {
                        Optional<Profile> p = u.getProfile();
                        if (!p.isPresent()) {
                            return new KeyboardButtonResponse("You need to make a profile!", true);
                        }
                        String epCode = SuperBotResource.getUserProgress(p.get().getName()).get(show);
                        if (epCode == null) {
                            return new KeyboardButtonResponse("You have no progress on " + show.getDisplay(), true);
                        }
                        String[] spl = epCode.substring(1).split("E");
                        SeasonResult season = show.getSeason(spl[0]);
                        if (season == null) {
                            return new KeyboardButtonResponse("Invalid season!", true);
                        }
                        int nextEp = Integer.parseInt(spl[1]) - 1;
                        String newCode = String.format("S%sE%s", spl[0], nextEp);
                        if (nextEp >= 1) {
                            return setProgress.apply(p.get().getName(), newCode);
                        } else {
                            int prevSe = Integer.parseInt(spl[0]) - 1;
                            SeasonResult prevSeason = show.getSeason(String.valueOf(prevSe));
                            SeasonEpisodeResult[] ser = prevSeason != null ? prevSeason.getEpisodes() : null;
                            if (prevSe >= 1 && prevSeason != null) {
                                newCode = String.format("S%sE%s", prevSe, ser[ser.length - 1].getEpisode());
                                return setProgress.apply(p.get().getName(), newCode);
                            } else {
                                return new KeyboardButtonResponse("There is no episode " + newCode + "!\nThere is no season " + prevSe + "!", true);
                            }
                        }
                    }));
                    kbr.add(new KeyboardButton("Check", u -> {
                        Optional<Profile> p = u.getProfile();
                        if (!p.isPresent()) {
                            return new KeyboardButtonResponse("You need to make a profile!", true);
                        }
                        String epCode = SuperBotResource.getUserProgress(p.get().getName()).get(show);
                        if (epCode == null) {
                            return new KeyboardButtonResponse("You have no progress on " + show.getDisplay(), true);
                        }
                        return new KeyboardButtonResponse(String.format("You are currently on " + epCode), true);
                    }));
                    kbr.add(new KeyboardButton("»", u -> {
                        Optional<Profile> p = u.getProfile();
                        if (!p.isPresent()) {
                            return new KeyboardButtonResponse("You need to make a profile!", true);
                        }
                        String epCode = SuperBotResource.getUserProgress(p.get().getName()).get(show);
                        if (epCode == null) {
                            return new KeyboardButtonResponse("You have no progress on " + show.getDisplay(), true);
                        }
                        String[] spl = epCode.substring(1).split("E");
                        SeasonResult season = show.getSeason(spl[0]);
                        if (season == null) {
                            return new KeyboardButtonResponse("Invalid season!", true);
                        }
                        SeasonEpisodeResult[] episodes = season.getEpisodes();
                        SeasonEpisodeResult last = episodes[episodes.length - 1];
                        int nextEp = Integer.parseInt(spl[1]) + 1;
                        String newCode = String.format("S%sE%s", spl[0], nextEp);
                        if (Integer.parseInt(last.getEpisode()) >= nextEp) {
                            return setProgress.apply(p.get().getName(), newCode);
                        } else {
                            String latest = show.getLatestEpisode();
                            int nextSe = Integer.parseInt(spl[0]) + 1;
                            String[] latSpl = latest.substring(1).split("E");
                            if (nextSe <= Integer.parseInt(latSpl[0])) {
                                newCode = String.format("S%sE%s", nextSe, 1);
                                return setProgress.apply(p.get().getName(), newCode);
                            } else {
                                return new KeyboardButtonResponse("There is no episode " + newCode + "!\nThere is no season " + nextSe + "!", true);
                            }
                        }
                    }));
                    kb.add(kbr);
                    mb.bold(z -> z.escaped(show.getDisplay()));
                    String latest = show.getLatestEpisode();
                    if (latest != null && !latest.isEmpty())
                        mb.newLine().italic(true).escaped("Latest: ").italic(false).escaped(latest);
                    mb.setKeyboard(kb);
                } else {
                    sendUsage(sys, user, group);
                }
            } else if (episodeCode.equalsIgnoreCase("none") || episodeCode.equalsIgnoreCase("remove")) {
                Map<String, String> prg = SuperBotResource.getProgress(show);
                prg.remove(profileName.toLowerCase());
                SuperBotResource.PROGRESS.put(show.getIMDB(), prg);
                mb.escaped("Removed ").bold(true).escaped(profileName).bold(false).escaped("'s progress on ").bold(true).escaped(show.getDisplay());
                SuperBotResource.saveProgress();
            } else if (!SuperBotShows.EPISODE_PATTERN.matcher(episodeCode).matches() && !episodeCode.equals("NEXT")) {
                mb.escaped("Invalid episode: ").bold(true).escaped(episodeCode).bold(false).escaped(" (doesn't match S<season>E<episode> format)");
            } else if (oldprg == null && episodeCode.equals("NEXT")) {
                mb.escaped("You have no progress registered.");
            } else {
                if (oldprg != null && episodeCode.equals("NEXT")) {
                    String[] spl = oldprg.substring(1).split("E");
                    int de;
                    try {
                        de = args.length > 2 ? Integer.parseInt(args[2]) : 1;
                    } catch (NumberFormatException ex) {
                        group.sendMessage(mb.escaped("Not a number: %s!", args[2]));
                        return;
                    }
                    if (de >= 1) {
                        int episode = Integer.parseInt(spl[1]) + de;
                        try {
                            SeasonResult res = show.getSeason(spl[0]);
                            SeasonEpisodeResult[] eps = res.getEpisodes();
                            SeasonEpisodeResult last = eps[eps.length - 1];
                            if (Integer.parseInt(last.getEpisode()) < episode) {
                                throw new NullPointerException(); // gets caught right below
                            }
                            episodeCode = String.format("S%sE%s", spl[0], episode);
                        } catch (NullPointerException | ArrayIndexOutOfBoundsException ex) {
                            group.sendMessage(mb.escaped("There is no episode ").bold(true).escaped("S%sE%s", spl[0], episode).bold(false).escaped(" for ").bold(true).escaped(show.getDisplay()).bold(false));
                            return;
                        }
                    } else {
                        group.sendMessage(mb.escaped("Invalid number of episodes to count as next."));
                        return;
                    }
                }

                Matcher matcher = SuperBotShows.EPISODE_PATTERN.matcher(episodeCode);
                if (matcher.matches()) {
                    int season = Integer.parseInt(matcher.group(1));
                    int episode = Integer.parseInt(matcher.group(2));

                    final String epCode = String.format("S%dE%d", season, episode);

                    if (season == 0 || episode == 0) {
                        group.sendMessage(mb.escaped("Invalid season or episode number."));
                        return;
                    }

                    Map<String, String> prg = SuperBotResource.getProgress(show);
                    prg.put(profileName.toLowerCase(), epCode);
                    SuperBotResource.PROGRESS.put(show.getIMDB(), prg);
                    mb.escaped("Set ").bold(m -> m.escaped(profileName)).escaped("'s progress on ").bold(m -> m.escaped(show.getDisplay())).escaped(" to ").bold(m -> m.escaped(epCode));
                    if (oldprg != null) {
                        mb.escaped(" (was %s)", oldprg);
                    }
                    SuperBotResource.saveProgress();
                } else {
                    group.sendMessage(mb.escaped("Invalid episode code format."));
                }
            }
            group.sendMessage(mb);
        }
    }

}
