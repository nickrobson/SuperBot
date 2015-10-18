package me.nickrobson.skype.superchat.cmd;

import java.util.Random;

import me.nickrobson.skype.superchat.SuperChatController;
import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.message.Message;
import xyz.gghost.jskype.user.GroupUser;

public class HangmanCommand implements Command {

	private volatile String currentPhrase = null;
	private volatile String found = null;
	private volatile String guessed = null;
	
	@Override
	public String[] names() {
		return new String[]{ "hangman" };
	}

	@Override
	public String[] help(GroupUser user, boolean userChat) {
		return new String[]{ userChat ? "[phrase]" : "[guess]", userChat ? "start a hangman game with [phrase] as the phrase" : "take a guess at a letter" };
	}

	@Override
	public void exec(GroupUser user, Group group, String used, String[] args, Message message) {
		if (group.isUserChat())
			if (currentPhrase != null) 
				sendMessage(group, encode("[Hangman] There is already a game in progress.") + "\n" + encode("[Hangman] To take a guess, send a message in a group."));
			else
				if (args.length == 0)
					sendMessage(group, bold(encode("Usage: ")) + encode("~hangman [phrase]"));
				else if (args[0].equalsIgnoreCase("random"))
					if (SuperChatController.HANGMAN_PHRASES.isEmpty())
						sendMessage(group, "Sorry, you can't do random phrases (file missing).", true);
					else {
						int n = new Random().nextInt(SuperChatController.HANGMAN_PHRASES.size());
						String s = SuperChatController.HANGMAN_PHRASES.get(n).toUpperCase();
						if (s != null) {
							currentPhrase = s;
							found = currentPhrase.replaceAll("[A-Za-z]", "_");
							guessed = "";
							sendMessage(group, encode("[Hangman] The phrase has been set to: ") + code(encode(found)));
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
					sendMessage(group, encode("[Hangman] The phrase has been set to: ") + code(encode(currentPhrase)));
				}
		else
			if (currentPhrase == null)
				sendMessage(group, encode("[Hangman] There is no game in progress currently!") + "\n" + encode("[Hangman] To set the phrase, PM me!"));
			else
				if (args.length != 1)
					sendMessage(group, bold(encode("Usage: ")) + encode("~hangman [guess]") + (currentPhrase != null ? "\n" + encode("Phrase so far: ") + code(encode(found)) : ""));
				else {
					char first = args[0].trim().toUpperCase().charAt(0);
					if (args[0].trim().length() != 1)
						sendMessage(group, "[Hangman] You can only guess one letter!", true);
					else if (!('A' <= first && first <= 'Z'))
						sendMessage(group, "[Hangman] You can only guess letters!", true);
					else {
						if (found.indexOf(first) != -1)
							sendMessage(group, "[Hangman] " + first + " has already been guessed and found." , true);
						else if (guessed.indexOf(first) != -1)
							sendMessage(group, "[Hangman] " + first + " has already been guessed and was not found." , true);
						else {
							if (currentPhrase.indexOf(first) != -1) {
								StringBuilder sb = new StringBuilder(found);
								for (int i = 0; i < currentPhrase.length(); i++) {
									if (currentPhrase.charAt(i) == first) {
										sb.setCharAt(i, first);
									}
								}
								found = sb.toString();
								if (currentPhrase.equals(found)) {
									sendMessage(group, encode("[Hangman] Congratulations! You've uncovered the phrase!") + "\n" + encode("It was: ") + code(encode(currentPhrase)));
									currentPhrase = null;
									found = null;
									guessed = null;
								} else
									sendMessage(group, encode("[Hangman] Congratulations! " + first + " is in the phrase!") + "\n" + encode("Phrase so far: ") + code(encode(found)), false);
							} else {
								guessed += first;
								sendMessage(group, encode("[Hangman] Sorry, " + first + " isn't in the phrase!") + "\n" + encode("Phrase so far: ") + code(encode(found)), false);
							}
						}
					}
				}
	}

}
