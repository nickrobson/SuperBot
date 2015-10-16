package me.nickrobson.skype.superchat.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import me.nickrobson.skype.superchat.SuperChatController;
import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.message.FormatUtils;
import xyz.gghost.jskype.message.Message;
import xyz.gghost.jskype.user.GroupUser;
import xyz.gghost.jskype.user.GroupUser.Role;

public class HelpCommand implements Command {

	@Override
	public String[] names() {
		return new String[]{ "help" };
	}

	@Override
	public String[] help(GroupUser user, boolean userChat) {
		return new String[]{"", "see this help message"};
	}
	
	String getCmdHelp(Command cmd, GroupUser user, boolean userChat) {
		String pre = SuperChatController.COMMAND_PREFIX;
		String s = pre;
		for (String n : cmd.names()) {
			if (s.length() > pre.length()) s += ",";
			s += n.trim();
		}
		String cmdHelp = cmd.help(user, userChat)[0];
		if (cmdHelp.length() > 0)
			s += " " + cmd.help(user, userChat)[0];
		return s;
	}
	
	String pad(String str, int len) {
		while (str.length() < len)
			str += " "; // + str;
		return str;
	}

	@Override
	public void exec(GroupUser user, Group group, String used, String[] args, Message message) {
		List<Command> cmds = new ArrayList<>(SuperChatController.COMMANDS.size());
		SuperChatController.COMMANDS.forEach((name, cmd) -> {
			boolean go = true;
			for (Command c : cmds)
				if (c == cmd) go = false;
			if (go) cmds.add(cmd);
		});
		AtomicInteger maxLen = new AtomicInteger(0);
		cmds.forEach(c -> {
			String cmdHelp = getCmdHelp(c, user, group.isUserChat());
			if (c.role() == Role.USER && cmdHelp.length() > maxLen.get())
				maxLen.set(cmdHelp.length());
		});
		List<String> strings = new ArrayList<>(SuperChatController.COMMANDS.size());
		StringBuilder builder = new StringBuilder();
		cmds.forEach(c -> {
			String[] help = c.help(user, group.isUserChat());
			if (c.role() == Role.USER)
				strings.add("\n" + pad(getCmdHelp(c, user, group.isUserChat()), maxLen.get()) + " - " + help[1]);
		});
		if (SuperChatController.HELP_IGNORE_WHITESPACE)
			strings.sort((s1, s2) -> s1.trim().compareTo(s2.trim()));
		else
			strings.sort(null);
		String welcome = SuperChatController.WELCOME_MESSAGE;
		int mid = welcome.length() / 2;
		String wel = pad(welcome.substring(0, mid), maxLen.get());
		String come = welcome.substring(mid);
		maxLen.set(0);
		strings.forEach(s -> {
			if (s.length() > maxLen.get())
				maxLen.set(s.length());
			builder.append(s);
		});
		String spaces = SuperChatController.HELP_WELCOME_CENTRED ? strings.get(0).replaceAll("\\S.+", "") : wel.replaceAll("\\S+", "");
		sendMessage(group, FormatUtils.code(FormatUtils.encodeRawText(spaces.substring(1))) + FormatUtils.bold(FormatUtils.encodeRawText(wel.trim() + come)) + FormatUtils.code(FormatUtils.encodeRawText(builder.toString())), false);
	}
	

}
