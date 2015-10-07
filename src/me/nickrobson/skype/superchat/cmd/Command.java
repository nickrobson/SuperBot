package me.nickrobson.skype.superchat.cmd;

import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.message.FormatUtils;
import xyz.gghost.jskype.message.Message;
import xyz.gghost.jskype.message.MessageBuilder;
import xyz.gghost.jskype.user.GroupUser;

public interface Command {
	
	String[] names();
	
	default GroupUser.Role role() {
		return GroupUser.Role.USER;
	}
	
	String[] help(GroupUser user);
	
	void exec(GroupUser user, Group group, String used, String[] args, Message message);
	
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
	
}
