package me.nickrobson.skype.superchat.cmd;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
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
	public String[] help(GroupUser user, boolean userChat) {
		return new String[]{"[-a] [shows...]", "see progress on all or provided shows"};
	}

	@Override
	public void exec(GroupUser user, Group group, String used, String[] args, Message message) {
		MessageBuilder builder = new MessageBuilder();
		boolean sent = false;
		boolean all_eps = false;
		
		List<String> argz = new LinkedList<>(Arrays.asList(args));
		
		for (Iterator<String> it = argz.iterator(); it.hasNext();) {
			String s = it.next();
			if (s.equals("-a")) {
				all_eps = true;
				it.remove();
			}
		}
		
		if (argz.size() > 0) {
			sent = true;
			for (int i = 0; i < argz.size(); i++) {
				Show show = SuperChatShows.getShow(argz.get(i));
				if (i > 0)
					builder.addHtml("\n");
				if (show == null)
					builder = builder.addText("Invalid show: " + argz.get(i));
				else {
					builder = show(show.getMainName(), builder, all_eps);
				}
			}
		} else {
			sendMessage(group, FormatUtils.bold(FormatUtils.encodeRawText("Usage: ")) + FormatUtils.encodeRawText(SuperChatController.COMMAND_PREFIX + "progress [show...]"), false);
			return;
		}
		if (sent) {
			sendMessage(group, builder.build(), false);
		} else {
			sendMessage(group, "No progress submitted for any show.", true);
		}
	}
	
	MessageBuilder show(String show, MessageBuilder builder, boolean all_eps) {
		Map<String, String> prg = SuperChatController.getProgress(show);
		List<String> eps = prg.values().stream()
				.filter(s -> SuperChatShows.EPISODE_PATTERN.matcher(s).matches())
				.sorted((e1, e2) -> SuperChatController.whichEarlier(e1, e2).equals(e1) ? -1 : 1)
				.collect(Collectors.toList());
		List<String> epz = new LinkedList<>();
		eps.forEach(e -> {
			if (!epz.contains(e))
				epz.add(e);
		});
		builder.addHtml(FormatUtils.bold(FormatUtils.encodeRawText("Episode progress: " + SuperChatShows.getShow(show).getDisplay())));
		if (epz.size() > 0) {
			if (all_eps) {
				for (String ep : epz) {
					builder.addText("\n- " + ep.toUpperCase() + ": " + SuperChatController.getUsersOn(show, ep));
				}
			} else {
				builder.addText("\n- Earliest: " + epz.get(0) + " (" + SuperChatController.getUsersOn(show, epz.get(0)) + ")");
				builder.addText("\n- Latest:   " + epz.get(epz.size() - 1) + " (" + SuperChatController.getUsersOn(show, epz.get(epz.size() - 1)) + ")");
			}
		} else {
			builder.addText("\nNo progress submitted.");
		}
		return builder;
	}

}
