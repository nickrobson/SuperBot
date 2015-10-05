package me.nickrobson.skype.superchat.cmd;

import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.message.Message;
import xyz.gghost.jskype.user.User;

public class GitCommand implements Command {

	@Override
	public String[] names() {
		return new String[]{ "git" };
	}

	@Override
	public String[] help(User user) {
		return new String[]{ "", "tells you the bot's git repo" };
	}

	@Override
	public void exec(User user, Group group, String used, String[] args, Message message) {
		group.sendMessage("<a href=\"http://github.com/nickrobson/SuperChat/\">Click me for the GitHub repo!</a>");
	}

}
