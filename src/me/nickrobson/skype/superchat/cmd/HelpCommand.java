package me.nickrobson.skype.superchat.cmd;

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
	public void exec(User user, Group group, String used, String[] args, Message message) {
		sendMessage(group, FormatUtils.bold(FormatUtils.encodeRawText(SuperChatController.WELCOME_MESSAGE)) + FormatUtils.code(FormatUtils.encodeRawText("\n" +
				"~help                - see this help message\n" +
				"~shows               - see all tracked shows\n" +
			 	"~progress            - see progress on all shows\n" +
				"~progress <show>     - see progress on <show>\n" +
				"~me <show> <episode> - submit your progress on <show> as <episode>\n" +
				"           <episode> must be formatted, e.g. S1E1 or S2E17 (SxEyy)")), false);
	}
	

}
