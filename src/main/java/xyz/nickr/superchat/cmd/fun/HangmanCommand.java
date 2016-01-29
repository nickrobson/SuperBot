package xyz.nickr.superchat.cmd.fun;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import xyz.nickr.superchat.SuperChatController;
import xyz.nickr.superchat.cmd.Command;
import xyz.nickr.superchat.sys.Group;
import xyz.nickr.superchat.sys.GroupType;
import xyz.nickr.superchat.sys.Message;
import xyz.nickr.superchat.sys.MessageBuilder;
import xyz.nickr.superchat.sys.Sys;
import xyz.nickr.superchat.sys.User;

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
    public void exec(Sys sys, User user, Group conv, String used, String[] args, Message message) {
        MessageBuilder<?> mb = sys.message();
        if (conv.getType() == GroupType.USER)
            if (currentPhrase != null)
                conv.sendMessage(mb.text("[Hangman] There is already a game in progress.").newLine().text("[Hangman] To take a guess, send a message in a group."));
            else if (args.length == 0)
                sendUsage(null, user, conv);
            else if (args[0].equalsIgnoreCase("random"))
                if (SuperChatController.HANGMAN_PHRASES.isEmpty())
                    conv.sendMessage(mb.text("Sorry, you can't do random phrases (file missing)."));
                else {
                    int n = new Random().nextInt(SuperChatController.HANGMAN_PHRASES.size());
                    String s = SuperChatController.HANGMAN_PHRASES.get(n).toUpperCase();
                    if (s != null) {
                        currentPhrase = s;
                        found = currentPhrase.replaceAll("[A-Za-z]", "_");
                        guessed = "";
                        numCorrect = new HashMap<>();
                        conv.sendMessage(mb.text("[Hangman] The phrase has been set to: ").code(true).text(found));
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
                conv.sendMessage(mb.text("[Hangman] The phrase has been set to: ").code(true).text(currentPhrase));
            }
        else if (currentPhrase == null)
            conv.sendMessage(mb.text("[Hangman] There is no game in progress currently!").newLine().text("[Hangman] To set the phrase, PM me `" + SuperChatController.COMMAND_PREFIX + "hangman [phrase]`!"));
        else if (args.length != 1)
            conv.sendMessage(mb.bold(true).text("Usage: ").bold(false).text(PREFIX + "hangman [guess]").html(currentPhrase != null ? sys.message().newLine().text("Phrase so far: ").code(true).text(found).build() : ""));
        else {
            char first = args[0].trim().toUpperCase().charAt(0);
            if (args[0].trim().length() != 1)
                conv.sendMessage(mb.text("[Hangman] You can only guess one letter!"));
            else if (!('A' <= first && first <= 'Z'))
                conv.sendMessage(mb.text("[Hangman] You can only guess letters!"));
            else {
                if (found.indexOf(first) != -1)
                    conv.sendMessage(mb.text("[Hangman] " + first + " has already been guessed and found."));
                else if (guessed.indexOf(first) != -1)
                    conv.sendMessage(mb.text("[Hangman] " + first + " has already been guessed and was not found."));
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
                                stats.bold(true).text(player.getKey() + ": ").bold(false);
                                stats.text(player.getValue().toString() + "/" + curr.length() + " (" + (ps.length() > 5 ? ps.substring(0, 5) : ps) + "%)");
                            }
                            conv.sendMessage(mb.text("[Hangman] Congratulations! You've uncovered the phrase!").newLine().text("It was: ").code(true).text(currentPhrase).code(false).html(stats.length() > 0 ? "\n" + stats.build() : ""));
                            currentPhrase = null;
                            found = null;
                            guessed = null;
                            numCorrect = null;
                        } else
                            conv.sendMessage(mb.text("[Hangman] Congratulations! " + first + " is in the phrase!").newLine().text("Phrase so far: ").code(true).text(found));
                    } else {
                        guessed += first;
                        conv.sendMessage(mb.text("[Hangman] Sorry, " + first + " isn't in the phrase!").newLine().text("Phrase so far: ").code(true).text(found));
                    }
                }
            }
        }
    }

}
