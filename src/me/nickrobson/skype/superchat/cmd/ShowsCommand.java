package me.nickrobson.skype.superchat.cmd;

import java.util.LinkedList;
import java.util.List;

import me.nickrobson.skype.superchat.SuperChatShows;
import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.message.FormatUtils;
import xyz.gghost.jskype.message.Message;
import xyz.gghost.jskype.user.User;

public class ShowsCommand implements Command {

	@Override
	public String[] names() {
		return new String[]{ "shows" };
	}

	@Override
	public void exec(User user, Group group, String used, String[] args, Message message) {
		List<String> send = new LinkedList<>();
		for (String key : SuperChatShows.DISPLAY_NAMES.keySet()) {
			StringBuilder sb = new StringBuilder();
			for (String s : SuperChatShows.TRACKED_SHOWS) {
				if (s.startsWith(key + ":")) {
					if (sb.length() > 0)
						sb.append(", ");
					sb.append(s.split(":", 2)[1]);
				}
			}
			if (sb.length() > 0)
				send.add(FormatUtils.bold(FormatUtils.encodeRawText(SuperChatShows.DISPLAY_NAMES.get(key) + ": ")) + FormatUtils.encodeRawText(sb.toString()));
		}
		send.sort(null);
		String toSend = "";
		for (String s : send) {
			if (toSend.length() > 0)
				toSend += "\n";
			toSend += s;
		}
		sendMessage(group, toSend, false);
	}

}
