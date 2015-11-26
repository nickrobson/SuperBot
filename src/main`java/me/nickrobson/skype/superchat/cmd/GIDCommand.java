package me.nickrobson.skype.superchat.cmd;

import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.message.Message;
import xyz.gghost.jskype.user.GroupUser;

public class GIDCommand implements Command {

	@Override
	public String[] names() {
		return new String[]{ "gid" };
	}

	@Override
	public String[] help(GroupUser user, boolean userChat) {
		return new String[]{ "", "gets the group's longId" };
	}
    
    @Override
    public boolean userchat() {
        return true;
    }

	@Override
	public void exec(GroupUser user, Group group, String used, String[] args, Message message) {
		sendMessage(group, encode("This group's longId is: ") + bold(encode(group.getLongId())));
	}

    @Override
    public boolean alwaysEnabled() {
        return true;
    }

}
