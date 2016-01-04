package me.nickrobson.skype.superchat.cmd.fun;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import in.kyle.ezskypeezlife.api.SkypeConversationType;
import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.MessageBuilder;
import me.nickrobson.skype.superchat.SuperChatController;
import me.nickrobson.skype.superchat.cmd.Command;

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
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[] { userChat ? "[phrase]" : "[guess]", userChat ? "start a hangman game with phrase [phrase]" : "take a guess at a letter" };
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        if (group.getConversationType() == SkypeConversationType.USER)
            if (currentPhrase != null)
                group.sendMessage(encode("[Hangman] There is already a game in progress.") + "\n" + encode("[Hangman] To take a guess, send a message in a group."));
            else if (args.length == 0)
                sendUsage(user, group);
            else if (args[0].equalsIgnoreCase("random"))
                if (SuperChatController.HANGMAN_PHRASES.isEmpty())
                    group.sendMessage(encode("Sorry, you can't do random phrases (file missing)."));
                else {
                    int n = new Random().nextInt(SuperChatController.HANGMAN_PHRASES.size());
                    String s = SuperChatController.HANGMAN_PHRASES.get(n).toUpperCase();
                    if (s != null) {
                        currentPhrase = s;
                        found = currentPhrase.replaceAll("[A-Za-z]", "_");
                        guessed = "";
                        numCorrect = new HashMap<>();
                        group.sendMessage(encode("[Hangman] The phrase has been set to: ") + code(encode(found)));
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
                group.sendMessage(encode("[Hangman] The phrase has been set to: ") + code(encode(currentPhrase)));
            }
        else if (currentPhrase == null)
            group.sendMessage(encode("[Hangman] There is no game in progress currently!") + "\n" + encode("[Hangman] To set the phrase, PM me `" + SuperChatController.COMMAND_PREFIX + "hangman [phrase]`!"));
        else if (args.length != 1)
            group.sendMessage(bold(encode("Usage: ")) + encode(PREFIX + "hangman [guess]") + (currentPhrase != null ? "\n" + encode("Phrase so far: ") + code(encode(found)) : ""));
        else {
            char first = args[0].trim().toUpperCase().charAt(0);
            if (args[0].trim().length() != 1)
                group.sendMessage(encode("[Hangman] You can only guess one letter!"));
            else if (!('A' <= first && first <= 'Z'))
                group.sendMessage(encode("[Hangman] You can only guess letters!"));
            else {
                if (found.indexOf(first) != -1)
                    group.sendMessage(encode("[Hangman] " + first + " has already been guessed and found."));
                else if (guessed.indexOf(first) != -1)
                    group.sendMessage(encode("[Hangman] " + first + " has already been guessed and was not found."));
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
                            MessageBuilder stats = new MessageBuilder();
                            List<Entry<String, Integer>> contrib = numCorrect.entrySet().stream().sorted((e1, e2) -> e2.getValue() - e1.getValue()).collect(Collectors.toList());
                            String curr = currentPhrase.replaceAll("[^A-Za-z]", "");
                            for (Entry<String, Integer> player : contrib) {
                                if (stats.length() > 0)
                                    stats.newLine();
                                String ps = String.valueOf(player.getValue() * 100.0 / curr.length());
                                stats.bold(true).text(player.getKey() + ": ").bold(false);
                                stats.text(player.getValue().toString() + "/" + curr.length() + " (" + (ps.length() > 5 ? ps.substring(0, 5) : ps) + "%)");
                            }
                            group.sendMessage(encode("[Hangman] Congratulations! You've uncovered the phrase!") + "\n" + encode("It was: ") + code(encode(currentPhrase)) + (stats.length() > 0 ? "\n" + stats : ""));
                            currentPhrase = null;
                            found = null;
                            guessed = null;
                            numCorrect = null;
                        } else
                            group.sendMessage(encode("[Hangman] Congratulations! " + first + " is in the phrase!") + "\n" + encode("Phrase so far: ") + code(encode(found)));
                    } else {
                        guessed += first;
                        group.sendMessage(encode("[Hangman] Sorry, " + first + " isn't in the phrase!") + "\n" + encode("Phrase so far: ") + code(encode(found)));
                    }
                }
            }
        }
    }

}
