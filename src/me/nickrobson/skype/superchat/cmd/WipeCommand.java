package me.nickrobson.skype.superchat.cmd;

import java.util.concurrent.atomic.AtomicBoolean;

import me.nickrobson.skype.superchat.SuperChatController;
import me.nickrobson.skype.superchat.SuperChatListener;
import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.message.Message;
import xyz.gghost.jskype.user.GroupUser;
import xyz.gghost.jskype.user.User;

public class WipeCommand implements Command {

	@Override
	public String[] names() {
		return new String[]{ "wipe", "clear" };
	}
	
	@Override
	public GroupUser.Role role() {
		return GroupUser.Role.MASTER;
	}

	@Override
	public String[] help(User user) {
		return new String[]{"[user]", "wipe [user]'s progress"};
	}

	@Override
	public void exec(User user, Group group, String used, String[] args, Message message) {
		if (args.length == 0)
			return;
		String toRemove = args[0];
		AtomicBoolean wiped = SuperChatListener.wipe(toRemove);
		if (wiped.get()) {
			sendMessage(group, "Wiped " + toRemove, true);
			SuperChatController.save();
		} else {
			sendMessage(group, "No data to wipe on " + toRemove, true);
		}
	}

}
