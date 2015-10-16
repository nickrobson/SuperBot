package me.nickrobson.skype.superchat.cmd;

import java.util.LinkedList;
import java.util.List;

import me.nickrobson.skype.superchat.SuperChatShows;
import me.nickrobson.skype.superchat.SuperChatShows.Show;
import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.message.FormatUtils;
import xyz.gghost.jskype.message.Message;
import xyz.gghost.jskype.user.GroupUser;

public class ShowsCommand implements Command {

	@Override
	public String[] names() {
		return new String[]{ "shows" };
	}

	@Override
	public String[] help(GroupUser user, boolean userChat) {
		return new String[]{"", "see which shows are being tracked"};
	}

	@Override
	public void exec(GroupUser user, Group group, String used, String[] args, Message message) {
		List<String> send = new LinkedList<>();
		for (Show show : SuperChatShows.TRACKED_SHOWS) {
			StringBuilder sb = new StringBuilder();
			for (String s : show.getNames()) {
				if (sb.length() > 0)
					sb.append(", ");
				sb.append(s);
			}
			if (sb.length() > 0)
				send.add(FormatUtils.bold(FormatUtils.encodeRawText(show.getDisplay() + ": ")) + FormatUtils.encodeRawText(sb.toString()));
		}
		send.sort(String.CASE_INSENSITIVE_ORDER);
		String toSend = "";
		for (String s : send) {
			if (toSend.length() > 0)
				toSend += "\n";
			toSend += s;
		}
		sendMessage(group, toSend, false);
	}

}
