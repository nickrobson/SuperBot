package me.nickrobson.skype.superchat.cmd;

import me.nickrobson.skype.superchat.SuperChatListener;
import me.nickrobson.skype.superchat.SuperChatShows;
import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.message.Message;
import xyz.gghost.jskype.message.MessageBuilder;
import xyz.gghost.jskype.user.User;

public class ProgressCommand implements Command {

	@Override
	public String[] names() {
		return new String[]{ "progress", "prg" };
	}

	@Override
	public String[] help(User user) {
		return new String[]{"(shows...)", "see progress on all or provided shows"};
	}

	@Override
	public void exec(User user, Group group, String used, String[] args, Message message) {
		MessageBuilder builder = new MessageBuilder();
		boolean sent = false;
		
		if (args.length > 0) {
			sent = true;
			for (int i = 0; i < args.length; i++) {
				String main = SuperChatShows.getMainName(args[i]);
				if (i > 0)
					builder.addText("\n");
				if (main == null)
					builder = builder.addText("Invalid show: " + args[i]);
				else {
					builder = show(main, builder);
				}
			}
		} else {
			for (String s : SuperChatShows.DISPLAY_NAMES.keySet()) {
				if (!SuperChatListener.getProgress(s).isEmpty()) {
					if (sent)
						builder.addText("\n");
					sent = true;
					builder = show(s, builder);
				}
			}
		}
		if (sent) {
			sendMessage(group, builder.build(), false);
		} else {
			sendMessage(group, "No progress submitted for any show.", true);
		}
	}

}
