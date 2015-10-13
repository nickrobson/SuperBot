package me.nickrobson.skype.superchat.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.nickrobson.skype.superchat.SuperChatController;
import me.nickrobson.skype.superchat.SuperChatShows;
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
		while (s.length() < len)
			s = " " + s;
		return s;
	}

	@Override
	public void exec(GroupUser user, Group group, String used, String[] args, Message message) {
		String username = args.length > 0 ? args[0].toLowerCase() : user.getUser().getUsername();
		List<String> shows = new ArrayList<>();
		for (Show show : SuperChatShows.TRACKED_SHOWS) {
			Map<String, String> progress = SuperChatController.getProgress(show);
			if (progress.containsKey(username)) {
				shows.add(show.getDisplay() + " (" + progress.get(username) + ")");
			}
		}
		shows.sort(String.CASE_INSENSITIVE_ORDER);
		int maxLen = shows.stream().mapToInt(s -> s.length()).max().orElse(0);
		String s = "";
		int rows = (shows.size() / 2) + (shows.size() % 2);
		for (int i = 0; i < rows; i++) {
			if (shows.size() > i) {
				String t = pad(shows.get(i), maxLen);
				if (shows.size() > rows+i) {
					t += "      " + pad(shows.get(rows+i), maxLen);
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
