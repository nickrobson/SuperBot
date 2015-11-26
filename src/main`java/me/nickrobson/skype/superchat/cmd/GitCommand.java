package me.nickrobson.skype.superchat.cmd;

import me.nickrobson.skype.superchat.MessageBuilder;
import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.message.Message;
import xyz.gghost.jskype.user.GroupUser;

public class GitCommand implements Command {

	@Override
	public String[] names() {
		return new String[]{ "git" };
	}

	@Override
	public String[] help(GroupUser user, boolean userChat) {
		return new String[]{ "", "tells you the bot's git repo" };
	}
    
    @Override
    public boolean userchat() {
        return true;
    }

	@Override
	public void exec(GroupUser user, Group group, String used, String[] args, Message message) {
		group.sendMessage(new MessageBuilder().link("http://github.com/nickrobson/SuperChat").html("Click here for the GitHub repo link!").build());
	}

}
