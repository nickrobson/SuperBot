package xyz.nickr.superbot.cmd.fun;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import xyz.nickr.superbot.SuperBotResource;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupType;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Profile;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class HangmanCommand implements Command {

    private volatile String currentPhrase = null;
    private volatile String found = null;
    private volatile String guessed = null;
    private volatile Map<String, Integer> numCorrect = null;

    @Override
    public String[] names() {
        return new String[] {"hangman"};
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] {userChat ? "[phrase]" : "[guess]", userChat ? "start a hangman game with phrase [phrase]" : "take a guess at a letter"};
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        MessageBuilder mb = sys.message();
        String prefix = sys.prefix();
        Profile profile = user.getProfile().orElse(null);
        if (profile == null) {
            group.sendMessage(sys.message().escaped("[Hangman] You need a profile to use this. Use " + prefix + "createprofile."));
        } else if (group.getType() == GroupType.USER) {
            if (this.currentPhrase != null) {
                group.sendMessage(mb.escaped("[Hangman] There is already a game in progress.").newLine().escaped("[Hangman] To take a guess, send a message in a group."));
            } else if (args.length == 0) {
                this.sendUsage(sys, user, group);
            } else if (args[0].equalsIgnoreCase("random")) {
                if (SuperBotResource.HANGMAN_PHRASES.isEmpty()) {
                    group.sendMessage(mb.escaped("Sorry, you can't do random phrases (file missing)."));
                } else {
                    int n = new Random().nextInt(SuperBotResource.HANGMAN_PHRASES.size());
                    String s = SuperBotResource.HANGMAN_PHRASES.get(n).toUpperCase();
                    if (s != null) {
                        this.currentPhrase = s;
                        this.found = this.currentPhrase.replaceAll("[A-Za-z]", "_");
                        this.guessed = "";
                        this.numCorrect = new HashMap<>();
                        group.sendMessage(mb.escaped("[Hangman] The phrase has been set to: ").code(true).escaped(this.found));
                    }
                }
            } else {
                StringBuilder sb = new StringBuilder();
                for (String a : args) {
                    if (sb.length() > 0) {
                        sb.append(" ");
                    }
                    sb.append(a);
                }
                String s = sb.toString().toUpperCase();
                this.currentPhrase = s;
                this.found = this.currentPhrase.replaceAll("[A-Za-z]", "_");
                this.guessed = "";
                this.numCorrect = new HashMap<>();
                group.sendMessage(mb.escaped("[Hangman] The phrase has been set to: ").code(true).escaped(this.currentPhrase));
            }
        } else if (this.currentPhrase == null) {
            group.sendMessage(mb.escaped("[Hangman] There is no game in progress currently!").newLine().escaped("[Hangman] To set the phrase, PM me `" + prefix + "hangman [phrase]`!"));
        } else if (args.length != 1) {
            group.sendMessage(mb.bold(true).escaped("Usage: ").bold(false).escaped(prefix + "hangman [guess]").raw(this.currentPhrase != null ? sys.message().newLine().escaped("Phrase so far: ").code(true).escaped(this.found) : sys.message()));
        } else {
            char first = args[0].trim().toUpperCase().charAt(0);
            if (args[0].trim().length() != 1) {
                group.sendMessage(mb.escaped("[Hangman] You can only guess one letter!"));
            } else if (!('A' <= first && first <= 'Z')) {
                group.sendMessage(mb.escaped("[Hangman] You can only guess letters!"));
            } else {
                if (this.found.indexOf(first) != -1) {
                    group.sendMessage(mb.escaped("[Hangman] " + first + " has already been guessed and found."));
                } else if (this.guessed.indexOf(first) != -1) {
                    group.sendMessage(mb.escaped("[Hangman] " + first + " has already been guessed and was not found."));
                } else {
                    if (this.currentPhrase.indexOf(first) != -1) {
                        StringBuilder sb = new StringBuilder(this.found);
                        int numChanged = 0;
                        for (int i = 0; i < this.currentPhrase.length(); i++) {
                            if (this.currentPhrase.charAt(i) == first) {
                                sb.setCharAt(i, first);
                                numChanged++;
                            }
                        }
                        this.numCorrect.put(profile.getName(), this.numCorrect.getOrDefault(profile.getName(), 0) + numChanged);
                        this.found = sb.toString();
                        if (this.currentPhrase.equals(this.found)) {
                            MessageBuilder stats = sys.message();
                            List<Entry<String, Integer>> contrib = this.numCorrect.entrySet().stream().sorted((e1, e2) -> e2.getValue() - e1.getValue()).collect(Collectors.toList());
                            String curr = this.currentPhrase.replaceAll("[^A-Za-z]", "");
                            for (Entry<String, Integer> player : contrib) {
                                if (!stats.isEmpty()) {
                                    stats.newLine();
                                }
                                String ps = String.valueOf(player.getValue() * 100.0 / curr.length());
                                stats.bold(true).escaped(player.getKey() + ": ").bold(false);
                                stats.escaped(player.getValue().toString() + "/" + curr.length() + " (" + (ps.length() > 5 ? ps.substring(0, 5) : ps) + "%%)");
                            }
                            group.sendMessage(mb.escaped("[Hangman] Congratulations! You've uncovered the phrase!").newLine().escaped("It was: ").code(true).escaped(this.currentPhrase).code(false).raw(!stats.isEmpty() ? sys.message().newLine().raw(stats) : sys.message()));
                            this.currentPhrase = null;
                            this.found = null;
                            this.guessed = null;
                            this.numCorrect = null;
                        } else {
                            group.sendMessage(mb.escaped("[Hangman] Congratulations! " + first + " is in the phrase!").newLine().escaped("Phrase so far: ").code(true).escaped(this.found));
                        }
                    } else {
                        this.guessed += first;
                        group.sendMessage(mb.escaped("[Hangman] Sorry, " + first + " isn't in the phrase!").newLine().escaped("Phrase so far: ").code(true).escaped(this.found));
                    }
                }
            }
        }
    }

}
