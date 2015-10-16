package me.nickrobson.skype.superchat.cmd;

import me.nickrobson.skype.superchat.SuperChatListener;
import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.message.FormatUtils;
import xyz.gghost.jskype.message.Message;
import xyz.gghost.jskype.user.GroupUser;

public interface Command {
	
	default void init() {}
	
	String[] names();
	
	default GroupUser.Role role() {
		return GroupUser.Role.USER;
	}
	
	String[] help(GroupUser user, boolean userChat);
	
	void exec(GroupUser user, Group group, String used, String[] args, Message message);
	
	/* UTILITY FUNCTIONS */
	
	default void sendMessage(Group group, String message) {
		sendMessage(group, message, false);
	}

	default void sendMessage(Group group, String message, boolean encode) {
		SuperChatListener.sendMessage(group, message, encode);
	}
	
	default String bold(String s) {
		return FormatUtils.bold(s);
	}
	
	default String encode(String s) {
		return FormatUtils.encodeRawText(s);
	}
	
	default String code(String s) {
		return FormatUtils.code(s);
	}
	
	default String blink(String s) {
		return FormatUtils.blink(s);
	}
	
	default String italic(String s) {
		return FormatUtils.italic(s);
	}
	
	default String link(String s) {
		return FormatUtils.link(s);
	}
	
	default String strike(String s) {
		return FormatUtils.strikethrough(s);
	}
	
	default String under(String s) {
		return FormatUtils.underline(s);
	}
	
	default String size(String s, int size) {
		return FormatUtils.size(s, size);
	}
	
}
