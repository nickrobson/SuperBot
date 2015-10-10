package me.nickrobson.skype.superchat;

import me.nickrobson.skype.superchat.cmd.Command;
import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.event.EventListener;
import xyz.gghost.jskype.events.APILoadedEvent;
import xyz.gghost.jskype.events.UserChatEvent;
import xyz.gghost.jskype.events.UserJoinEvent;
import xyz.gghost.jskype.events.UserLeaveEvent;
import xyz.gghost.jskype.events.UserPendingContactRequestEvent;
import xyz.gghost.jskype.internal.impl.GroupImpl;
import xyz.gghost.jskype.message.FormatUtils;
import xyz.gghost.jskype.message.Message;
import xyz.gghost.jskype.message.MessageBuilder;
import xyz.gghost.jskype.user.GroupUser;
import xyz.gghost.jskype.user.OnlineStatus;
import xyz.gghost.jskype.user.User;

public class SuperChatListener implements EventListener {
	
	public static void sendMessage(Group group, String message, boolean encode) {
		String[] lines = message.split("\\n");
		String fin = "";
		for (String line : lines) {
			if (encode)
				line = FormatUtils.encodeRawText(line);
			fin += (fin.length() > 0 ? "\n" : "") + line;
		}
		group.sendMessage(fin.replaceAll("\\\\n", "\n"));
	}
	
	public void loaded(APILoadedEvent event) {
		SuperChatController.skype.updateStatus(OnlineStatus.ONLINE);
		Group g = SuperChatController.getChatGroup();
		if (g != null)
			g.sendMessage(new MessageBuilder().setItalic(true).addText("SuperBot activated!").build());
		
		try {
			for (User user : SuperChatController.skype.getContactRequests())
				user.sendContactRequest(SuperChatController.skype, "FYI, you can send commands here too!");
		} catch (Exception ex) {}
	}
	
	public void join(UserJoinEvent event) {
		sendMessage(event.getGroup(), FormatUtils.bold(FormatUtils.encodeRawText(String.format(SuperChatController.WELCOME_MESSAGE_JOIN, event.getUser().getDisplayName()))) + "\n" +
				FormatUtils.encodeRawText("I'm tracking everyone's progress through common TV shows through commands.\n" +
				"Only discuss episodes that everyone has seen! (Earliest in the progress command)\n" +
				"You can see episodic progress through `~progress` and `~progress [show]`\n" +
				"You can set your progress through `~me [show] [episode]`, in format S1E2 / S2E15\n"), false);
	}
	
	public void leave(UserLeaveEvent event) {
		SuperChatController.wipe(event.getUser().getUsername());
	}
	
	public void contactRequest(UserPendingContactRequestEvent event) {
		event.accept(SuperChatController.skype);
	}
	
	public void chat(UserChatEvent event) {
		Message message = event.getMsg();
		User user = message.getSender();
		Group group = event.getChat();
		String msg = message.getMessage().trim();
		String[] words = msg.split("\\s+");
		
		if (words.length == 0 || !words[0].startsWith(SuperChatController.COMMAND_PREFIX)) {
			return;
		}
		
		String cmdName = words[0].substring(SuperChatController.COMMAND_PREFIX.length()).toLowerCase();
		Command cmd = SuperChatController.COMMANDS.get(cmdName);
		
		if (cmd == null)
			return;
		
		String[] args = new String[words.length - 1];
		for (int i = 1; i < words.length; i++)
			args[i-1] = words[i];
		
		GroupUser guser;
		if (group.isUserChat())
			if (cmd.role() == GroupUser.Role.MASTER)
				try {
					guser = SuperChatController.getChatGroup().getClients().stream().filter(c -> c.getUser().getUsername().equals(user.getUsername())).findFirst().orElse(null);
				} catch (Exception ex) {
					guser = null;
				}
			else
				guser = new GroupUser(user, GroupUser.Role.USER, (GroupImpl) group);
		else
			guser = group.getClients().stream().filter(c -> c.getUser().getUsername().equals(user.getUsername())).findFirst().orElse(null);
		
		if (guser != null)
			if (cmd.role() == GroupUser.Role.USER || guser.role == GroupUser.Role.MASTER)
				cmd.exec(guser, group, cmdName, args, message);
	}

}
