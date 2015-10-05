package me.nickrobson.skype.superchat.cmd;

import me.nickrobson.skype.superchat.SuperChatController;
import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.message.Message;
import xyz.gghost.jskype.user.GroupUser;
import xyz.gghost.jskype.user.User;

public class StopCommand implements Command {

	@Override
	public String[] names() {
		return new String[]{ "stop" };
	}
	
	@Override
	public GroupUser.Role role() {
		return GroupUser.Role.MASTER;
	}

	@Override
	public void exec(User user, Group group, String used, String[] args, Message message) {
		sendMessage(group, "Goodbye!", true);
		SuperChatController.save();
		System.exit(0);
	}

}
