package me.nickrobson.skype.superchat.cmd;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.nickrobson.skype.superchat.SuperChatController;
import me.nickrobson.skype.superchat.SuperChatShows;
import me.nickrobson.skype.superchat.SuperChatShows.Show;
import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.message.FormatUtils;
import xyz.gghost.jskype.message.Message;
import xyz.gghost.jskype.message.MessageBuilder;
import xyz.gghost.jskype.user.GroupUser;

public class ProgressCommand implements Command {

	@Override
	public String[] names() {
		return new String[]{ "progress", "prg" };
	}

	@Override
	public String[] help(GroupUser user) {
		return new String[]{"(shows...)", "see progress on all or provided shows"};
	}

	@Override
	public void exec(GroupUser user, Group group, String used, String[] args, Message message) {
		MessageBuilder builder = new MessageBuilder();
		boolean sent = false;
		
		if (args.length > 0) {
			sent = true;
			for (int i = 0; i < args.length; i++) {
				Show show = SuperChatShows.getShow(args[i]);
				if (i > 0)
					builder.addText("\n");
				if (show == null)
					builder = builder.addText("Invalid show: " + args[i]);
				else {
					builder = show(show.getMainName(), builder);
				}
			}
		} else {
			for (Show show : SuperChatShows.TRACKED_SHOWS) {
				if (!SuperChatController.getProgress(show).isEmpty()) {
					if (sent)
						builder.addText("\n");
					sent = true;
					builder = show(show.getMainName(), builder);
				}
			}
		}
		if (sent) {
			sendMessage(group, builder.build(), false);
		} else {
			sendMessage(group, "No progress submitted for any show.", true);
		}
	}
	
	MessageBuilder show(String show, MessageBuilder builder) {
		Map<String, String> prg = SuperChatController.getProgress(show);
		List<String> eps = prg.values().stream()
				.filter(s -> SuperChatShows.EPISODE_PATTERN.matcher(s).matches())
				.sorted((e1, e2) -> SuperChatController.whichEarlier(e1, e2).equals(e1) ? -1 : 1)
				.collect(Collectors.toList());
		builder.addHtml(FormatUtils.bold(FormatUtils.encodeRawText("Episode progress: " + SuperChatShows.getShow(show).getDisplay())));
		if (eps.size() > 0) {
			builder.addText("\n- Earliest: " + eps.get(0) + " (" + SuperChatController.getUsersOn(show, eps.get(0)) + ")");
			builder.addText("\n- Latest:   " + eps.get(eps.size() - 1) + " (" + SuperChatController.getUsersOn(show, eps.get(eps.size() - 1)) + ")");
		} else {
			builder.addText("\nNo progress submitted.");
		}
		return builder;
	}

}
