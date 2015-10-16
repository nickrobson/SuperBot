package me.nickrobson.skype.superchat.cmd;

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
		if (group.isUserChat()) {
			if (currentPhrase == null) 
				sendMessage(group, "[Hangman] There is already a game in progress.", true);
			else
				if (args.length == 0) {
					sendMessage(group, bold(encode("Usage: ")) + encode("~hangman [phrase]"));
				} else {
					StringBuilder sb = new StringBuilder();
					for (String a : args) {
						if (sb.length() > 0)
							sb.append(" ");
						sb.append(a);
					}
					String s = sb.toString().toUpperCase();
					for (char c : s.toCharArray())
						if (c != ' ' && !('A' <= c && c <= 'Z')) {
							sendMessage(group, "[Hangman] Phrases can only have letters in A-Z.", true);
							return;
						}
					currentPhrase = s;
					found = currentPhrase.replaceAll("\\S", "_");
					guessed = "";
					sendMessage(group, encode("[Hangman] The phrase has been set to: ") + code(encode(currentPhrase)));
				}
		} else {
			if (currentPhrase == null)
				sendMessage(group, "[Hangman] There is no game in progress currently!", true);
			else
				if (args.length != 1)
					sendMessage(group, bold(encode("Usage: ")) + encode("~hangman [guess]"));
				else
					if (args[0].trim().length() != 1)
						sendMessage(group, "[Hangman] You can only guess one letter!", true);
					else if (!('A' <= args[0].charAt(0) && args[0].charAt(0) <= 'Z'))
						sendMessage(group, "[Hangman] You can only guess letters!", true);
					else {
						if (found.indexOf(args[0].toUpperCase()) != -1)
							sendMessage(group, "[Hangman] " + args[0].toUpperCase() + " has already been guessed and found." , true);
						else if (guessed.indexOf(args[0].toUpperCase()) != -1)
							sendMessage(group, "[Hangman] " + args[0].toUpperCase() + " has already been guessed and was not found." , true);
						else {
							char first = args[0].toUpperCase().charAt(0);
							if (currentPhrase.indexOf(args[0].toUpperCase()) != -1) {
								StringBuilder sb = new StringBuilder(found);
								for (int i = 0; i < currentPhrase.length(); i++) {
									if (currentPhrase.charAt(i) == first) {
										sb.setCharAt(i, first);
									}
								}
								found = sb.toString();
								sendMessage(group, encode("[Hangman] Congratulations! " + first + " is in the phrase!") + "\n" + encode("Phrase so far: ") + code(encode(found)), false);
							} else {
								guessed += first;
								sendMessage(group, encode("[Hangman] Sorry, " + first + " isn't in the phrase!") + "\n" + encode("Phrase so far: ") + code(encode(found)), false);
							}
							if (currentPhrase.equals(found)) {
								sendMessage(group, encode("[Hangman] Congratulations! You've uncovered the phrase!") + "\n" + encode("It was: ") + code(encode(currentPhrase)));
								currentPhrase = null;
								found = null;
								guessed = null;
							}
						}
					}
		}
	}

}
