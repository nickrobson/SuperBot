package me.nickrobson.skype.superchat.cmd;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.nickrobson.skype.superchat.SuperChatListener;
import me.nickrobson.skype.superchat.SuperChatShows;
import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.message.FormatUtils;
import xyz.gghost.jskype.message.Message;
import xyz.gghost.jskype.message.MessageBuilder;
import xyz.gghost.jskype.user.GroupUser;
import xyz.gghost.jskype.user.User;

public interface Command {
	
	String[] names();
	
	default GroupUser.Role role() {
		return GroupUser.Role.USER;
	}
	
	void exec(User user, Group group, String used, String[] args, Message message);
	
	/* UTILITY FUNCTIONS */

	default void sendMessage(Group group, String message, boolean encode) {
		if (encode)
			message = FormatUtils.encodeRawText(message);
		group.sendMessage((message));
	}
	
	default void setBold(MessageBuilder builder, boolean bold) {
		try {
			builder.getClass().getField("bold").setAccessible(true);
			builder.getClass().getField("bold").set(builder, bold);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
			ex.printStackTrace();
		}
	}
	
	default MessageBuilder show(String show, MessageBuilder builder) {
		Map<String, String> prg = SuperChatListener.getProgress(show);
		List<String> eps = prg.values().stream()
				.filter(s -> SuperChatShows.EPISODE_PATTERN.matcher(s).matches())
				.sorted((e1, e2) -> SuperChatShows.whichEarlier(e1, e2).equals(e1) ? -1 : 1)
				.collect(Collectors.toList());
		builder.addHtml(FormatUtils.bold(FormatUtils.encodeRawText("Episode progress: " + SuperChatShows.DISPLAY_NAMES.get(show))));
		if (eps.size() > 0) {
			builder.addText("\n- Earliest: " + eps.get(0) + " (" + SuperChatListener.getUsersOn(show, eps.get(0)) + ")");
			builder.addText("\n- Latest:   " + eps.get(eps.size() - 1) + " (" + SuperChatListener.getUsersOn(show, eps.get(eps.size() - 1)) + ")");
		} else {
			builder.addText("\nNo progress submitted.");
		}
		return builder;
	}
}
