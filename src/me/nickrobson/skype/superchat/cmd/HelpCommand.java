package me.nickrobson.skype.superchat.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import me.nickrobson.skype.superchat.SuperChatController;
import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.message.FormatUtils;
import xyz.gghost.jskype.message.Message;
import xyz.gghost.jskype.user.User;

public class HelpCommand implements Command {

	@Override
	public String[] names() {
		return new String[]{ "help" };
	}

	@Override
	public String[] help(User user) {
		return new String[]{"", "see this help message"};
	}
	
	String getCmdHelp(Command cmd, User user) {
		String s = "";
		for (String n : cmd.names()) {
			if (s.length() > 0) s += ",";
			s += n.trim();
		}
		String cmdHelp = cmd.help(user)[0];
		if (cmdHelp.length() > 0)
			s += " " + cmd.help(user)[0];
		return s;
	}
	
	String pad(String str, int len) {
		while (str.length() < len)
			str = " " + str;
		return str;
	}

	@Override
	public void exec(User user, Group group, String used, String[] args, Message message) {
		List<Command> cmds = new ArrayList<>(SuperChatController.COMMANDS.size());
		SuperChatController.COMMANDS.forEach((name, cmd) -> {
			boolean go = true;
			for (Command c : cmds)
				if (c == cmd) go = false;
			if (go) cmds.add(cmd);
		});
		AtomicInteger maxLen = new AtomicInteger(0);
		cmds.forEach(c -> {
			String cmdHelp = getCmdHelp(c, user);
			if (cmdHelp.length() > maxLen.get())
				maxLen.set(cmdHelp.length());
		});
		List<String> strings = new ArrayList<>(SuperChatController.COMMANDS.size());
		StringBuilder builder = new StringBuilder();
		cmds.forEach(c -> {
			String[] help = c.help(user);
			strings.add("\n" + pad(getCmdHelp(c, user), maxLen.get()) + " - " + help[1]);
		});
		if (SuperChatController.HELP_IGNORE_WHITESPACE)
			strings.sort((s1, s2) -> s1.trim().compareTo(s2.trim()));
		else
			strings.sort(null);
		maxLen.set(0);
		strings.forEach(s -> {
			if (s.length() > maxLen.get())
				maxLen.set(s.length());
			builder.append(s);
		});
		String welcome = SuperChatController.WELCOME_MESSAGE;
		while (welcome.length() < strings.get(0).length())
			welcome = " " + welcome + " ";
		sendMessage(group, FormatUtils.bold(FormatUtils.encodeRawText(welcome.replaceFirst("\\s+$", ""))) + FormatUtils.code(FormatUtils.encodeRawText(builder.toString())), false);
	}
	

}
