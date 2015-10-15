package me.nickrobson.skype.superchat.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.nickrobson.skype.superchat.SuperChatController;
import me.nickrobson.skype.superchat.SuperChatShows.Show;
import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.message.FormatUtils;
import xyz.gghost.jskype.message.Message;
import xyz.gghost.jskype.user.GroupUser;

public class WhoCommand implements Command {

	@Override
	public String[] names() {
		return new String[] { "who", "whois" };
	}

	@Override
	public String[] help(GroupUser user) {
		return new String[] { "(username)", "gets information about which shows someone watches" };
	}
	
	String pad(String s, int len) {
		StringBuilder builder = new StringBuilder(s);
		while (builder.length() < len)
			builder.insert(builder.indexOf("("), ' ');
		return builder.toString();
	}

	@Override
	public void exec(GroupUser user, Group group, String used, String[] args, Message message) {
		String username = args.length > 0 ? args[0].toLowerCase() : user.getUser().getUsername();
		List<String> shows = new ArrayList<>();
		Map<Show, String> progress = SuperChatController.getUserProgress(username);
		progress.forEach((show, ep) -> {
			shows.add(show.getDisplay() + "    (" + ep + ")");
		});
		shows.sort(String.CASE_INSENSITIVE_ORDER);
		int rows = (shows.size() / 2) + (shows.size() % 2);
		int maxLen1 = 0;
		for (int i = 0; i < rows; i++)
			maxLen1 = Math.max(maxLen1, shows.get(i).length());
		int maxLen2 = 0;
		for (int i = rows; i < shows.size(); i++)
			maxLen2 = Math.max(maxLen2, shows.get(i).length());
		String s = "";
		for (int i = 0; i < rows; i++) {
			if (shows.size() > i) {
				String t = pad(shows.get(i), maxLen1);
				if (shows.size() > rows+i) {
					t += "    |    " + pad(shows.get(rows+i), maxLen2);
				}
				s += FormatUtils.encodeRawText(t);
				if (i != rows - 1)
					s += '\n';
			}
		}
		if (shows.size() > 0)
			sendMessage(group, FormatUtils.bold(FormatUtils.encodeRawText("Shows " + username + " is watching:")) + "\n" + FormatUtils.code(s), false);
		else
			sendMessage(group, FormatUtils.bold(FormatUtils.encodeRawText("Error: ")) + FormatUtils.encodeRawText("It doesn't look like " + username + " uses me. :("), false);
	}

}
