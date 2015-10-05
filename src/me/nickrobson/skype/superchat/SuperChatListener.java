package me.nickrobson.skype.superchat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import me.nickrobson.skype.superchat.cmd.Command;
import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.event.EventListener;
import xyz.gghost.jskype.events.UserChatEvent;
import xyz.gghost.jskype.events.UserJoinEvent;
import xyz.gghost.jskype.events.UserLeaveEvent;
import xyz.gghost.jskype.message.FormatUtils;
import xyz.gghost.jskype.message.Message;
import xyz.gghost.jskype.user.GroupUser;
import xyz.gghost.jskype.user.User;

public class SuperChatListener implements EventListener {
	
	public static final Map<String, Map<String, String>> PROGRESS = new TreeMap<>();
	
	public static void sendMessage(Group group, String message, boolean encode) {
		if (encode)
			message = FormatUtils.encodeRawText(message);
		group.sendMessage((message));
	}
	
	public static AtomicBoolean wipe(String toRemove) {
		AtomicBoolean wiped = new AtomicBoolean(false);
		PROGRESS.keySet().forEach(s -> {
			Map<String, String> prg = PROGRESS.get(s);
			if (prg.containsKey(toRemove)) {
				prg.remove(toRemove);
				wiped.set(true);
				PROGRESS.put(s, prg);
			}
		});
		return wiped;
	}

	public static String getUsersOn(String show, String ep) {
		if (!SuperChatShows.EPISODE_PATTERN.matcher(ep).matches())
			return null;
		
		List<String> users = getProgress(show).entrySet().stream()
			.filter(e -> e.getValue().equals(ep))
			.map(e -> e.getKey())
			.collect(Collectors.toList());
		
		StringBuilder sb = new StringBuilder();
		for (String s : users) {
			if (sb.length() > 0)
				sb.append(", ");
			sb.append(s);
		}
		return sb.toString();
	}
	
	public static Map<String, String> getProgress(String show) {
		Map<String, String> prg = PROGRESS.get(show);
		if (prg == null)
			prg = new HashMap<>();
		return prg;
	}
	
	public void join(UserJoinEvent event) {
		sendMessage(event.getGroup(), FormatUtils.bold(FormatUtils.encodeRawText(String.format(SuperChatController.WELCOME_MESSAGE_JOIN, event.getUser().getDisplayName()))) + "\n" +
				FormatUtils.encodeRawText("I'm tracking everyone's progress through common TV shows through commands.\n" +
				"Only discuss episodes that everyone has seen! (Earliest in the progress command)\n" +
				"You can see episodic progress through `~progress` and `~progress [show]`\n" +
				"You can set your progress through `~me [show] [episode]`, in format S1E2 / S2E15\n"), false);
	}
	
	public void leave(UserLeaveEvent event) {
		wipe(event.getUser().getUsername());
	}
	
	public void chat(UserChatEvent event) {
		Message message = event.getMsg();
		User user = message.getSender();
		Group group = event.getChat();
		String msg = message.getMessage().trim();
		String[] words = msg.split("\\s+");
		
		if (words.length == 0 || !words[0].startsWith("~")) {
			return;
		}
		
		String cmdName = words[0].substring(1).toLowerCase();
		Command cmd = SuperChatController.COMMANDS.get(cmdName);
		
		if (cmd == null)
			return;
		
		String[] args = new String[words.length - 1];
		for (int i = 1; i < words.length; i++)
			args[i-1] = words[i];
		
		GroupUser guser = group.getClients().stream().filter(c -> c.getUser().getUsername().equals(user.getUsername())).findFirst().orElse(null);
		if (guser != null)
			if (cmd.role() == GroupUser.Role.USER || guser.role == GroupUser.Role.MASTER)
				cmd.exec(user, group, cmdName, args, message);
	}

}
