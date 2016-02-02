package xyz.nickr.superbot.cmd.fun;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupType;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class HangmanCommand implements Command {

    private volatile String               currentPhrase = null;
    private volatile String               found         = null;
    private volatile String               guessed       = null;
    private volatile Map<String, Integer> numCorrect    = null;

    @Override
    public String[] names() {
        return new String[] { "hangman" };
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] { userChat ? "[phrase]" : "[guess]", userChat ? "start a hangman game with phrase [phrase]" : "take a guess at a letter" };
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        MessageBuilder<?> mb = sys.message();
        String prefix = sys.prefix();
        if (group.getType() == GroupType.USER)
            if (currentPhrase != null)
                group.sendMessage(mb.escaped("[Hangman] There is already a game in progress.").newLine().escaped("[Hangman] To take a guess, send a message in a group."));
            else if (args.length == 0)
                sendUsage(sys, user, group);
            else if (args[0].equalsIgnoreCase("random"))
                if (SuperBotController.HANGMAN_PHRASES.isEmpty())
                    group.sendMessage(mb.escaped("Sorry, you can't do random phrases (file missing)."));
                else {
                    int n = new Random().nextInt(SuperBotController.HANGMAN_PHRASES.size());
                    String s = SuperBotController.HANGMAN_PHRASES.get(n).toUpperCase();
                    if (s != null) {
                        currentPhrase = s;
                        found = currentPhrase.replaceAll("[A-Za-z]", "_");
                        guessed = "";
                        numCorrect = new HashMap<>();
                        group.sendMessage(mb.escaped("[Hangman] The phrase has been set to: ").code(true).escaped(found));
                    }
                }
            else {
                StringBuilder sb = new StringBuilder();
                for (String a : args) {
                    if (sb.length() > 0)
                        sb.append(" ");
                    sb.append(a);
                }
                String s = sb.toString().toUpperCase();
                currentPhrase = s;
                found = currentPhrase.replaceAll("[A-Za-z]", "_");
                guessed = "";
                numCorrect = new HashMap<>();
                group.sendMessage(mb.escaped("[Hangman] The phrase has been set to: ").code(true).escaped(currentPhrase));
            }
        else if (currentPhrase == null)
            group.sendMessage(mb.escaped("[Hangman] There is no game in progress currently!").newLine().escaped("[Hangman] To set the phrase, PM me `" + prefix + "hangman [phrase]`!"));
        else if (args.length != 1)
            group.sendMessage(mb.bold(true).escaped("Usage: ").bold(false).escaped(prefix + "hangman [guess]").raw(currentPhrase != null ? sys.message().newLine().escaped("Phrase so far: ").code(true).escaped(found).build() : ""));
        else {
            char first = args[0].trim().toUpperCase().charAt(0);
            if (args[0].trim().length() != 1)
                group.sendMessage(mb.escaped("[Hangman] You can only guess one letter!"));
            else if (!('A' <= first && first <= 'Z'))
                group.sendMessage(mb.escaped("[Hangman] You can only guess letters!"));
            else {
                if (found.indexOf(first) != -1)
                    group.sendMessage(mb.escaped("[Hangman] " + first + " has already been guessed and found."));
                else if (guessed.indexOf(first) != -1)
                    group.sendMessage(mb.escaped("[Hangman] " + first + " has already been guessed and was not found."));
                else {
                    if (currentPhrase.indexOf(first) != -1) {
                        StringBuilder sb = new StringBuilder(found);
                        int numChanged = 0;
                        for (int i = 0; i < currentPhrase.length(); i++) {
                            if (currentPhrase.charAt(i) == first) {
                                sb.setCharAt(i, first);
                                numChanged++;
                            }
                        }
                        numCorrect.put(user.getUsername(), numCorrect.getOrDefault(user.getUsername(), 0) + numChanged);
                        found = sb.toString();
                        if (currentPhrase.equals(found)) {
                            MessageBuilder<?> stats = sys.message();
                            List<Entry<String, Integer>> contrib = numCorrect.entrySet().stream().sorted((e1, e2) -> e2.getValue() - e1.getValue()).collect(Collectors.toList());
                            String curr = currentPhrase.replaceAll("[^A-Za-z]", "");
                            for (Entry<String, Integer> player : contrib) {
                                if (stats.length() > 0)
                                    stats.newLine();
                                String ps = String.valueOf(player.getValue() * 100.0 / curr.length());
                                stats.bold(true).escaped(player.getKey() + ": ").bold(false);
                                stats.escaped(player.getValue().toString() + "/" + curr.length() + " (" + (ps.length() > 5 ? ps.substring(0, 5) : ps) + "%)");
                            }
                            group.sendMessage(mb.escaped("[Hangman] Congratulations! You've uncovered the phrase!").newLine().escaped("It was: ").code(true).escaped(currentPhrase).code(false).raw(stats.length() > 0 ? "\n" + stats.build() : ""));
                            currentPhrase = null;
                            found = null;
                            guessed = null;
                            numCorrect = null;
                        } else
                            group.sendMessage(mb.escaped("[Hangman] Congratulations! " + first + " is in the phrase!").newLine().escaped("Phrase so far: ").code(true).escaped(found));
                    } else {
                        guessed += first;
                        group.sendMessage(mb.escaped("[Hangman] Sorry, " + first + " isn't in the phrase!").newLine().escaped("Phrase so far: ").code(true).escaped(found));
                    }
                }
            }
        }
    }

}
