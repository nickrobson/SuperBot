package me.nickrobson.skype.superchat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import me.nickrobson.skype.superchat.cmd.Command;
import me.nickrobson.skype.superchat.cmd.GitCommand;
import me.nickrobson.skype.superchat.cmd.HelpCommand;
import me.nickrobson.skype.superchat.cmd.ProgressCommand;
import me.nickrobson.skype.superchat.cmd.SetProgressCommand;
import me.nickrobson.skype.superchat.cmd.ShowsCommand;
import me.nickrobson.skype.superchat.cmd.StopCommand;
import me.nickrobson.skype.superchat.cmd.WipeCommand;
import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.SkypeAPI;
import xyz.gghost.jskype.internal.packet.packets.GroupInfoPacket;
import xyz.gghost.jskype.message.MessageBuilder;

/**
 * @author Nick Robson
 */
public class SuperChatController {
	
	public static final Map<String, Command> COMMANDS = new HashMap<>();
	
	public static final String COMMAND_PREFIX = "~";
	
	public static final String WELCOME_MESSAGE = "Welcome to the SuperChat";
	public static final String WELCOME_MESSAGE_JOIN = "Welcome, %s, to the SuperChat";
	
	public static boolean HELP_IGNORE_WHITESPACE = false;
	public static boolean HELP_WELCOME_CENTRED = true;
	
	public static SkypeAPI skype;
	
	public static void main(String[] args) {
		try {
			File config = new File("config.cfg");
			
			Map<String, String> properties = new HashMap<>();
			
			try {
				BufferedReader reader = Files.newBufferedReader(config.toPath());
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.contains("=")) {
						String[] data = line.split("=", 2);
						properties.put(data[0], data[1]);
					}
				}
			} catch (IOException ex) {}
			
			SuperChatShows.setup();
			
			load();
			commands();

			HELP_IGNORE_WHITESPACE = properties.getOrDefault("help.whitespace", "false").equalsIgnoreCase("true");
			HELP_WELCOME_CENTRED = properties.getOrDefault("help.welcome.centred", "false").equalsIgnoreCase("true");
			
			System.out.println("Logging in with " + properties.get("username") + " : ********");
			
			skype = new SkypeAPI(properties.get("username"), properties.get("password"));
			skype.login();
			skype.getEventManager().registerListener(new SuperChatListener());
			
			Thread.sleep(500);

			Group g = new GroupInfoPacket(skype).getGroup("19:c0cbadc10ca4415bac6be16bcec01450@thread.skype");
			if (g != null)
				g.sendMessage(new MessageBuilder().setItalic(true).addText("SuperBot activated!").build());
			
			while (true) {}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void register(Command cmd) {
		for (String name : cmd.names())
			COMMANDS.put(name, cmd);
	}
	
	private static void commands() {
		register(new HelpCommand());
		register(new ProgressCommand());
		register(new SetProgressCommand());
		register(new ShowsCommand());
		register(new StopCommand());
		register(new WipeCommand());
		register(new GitCommand());
	}

	public static void load() {
		File dir = new File("superchat_data");
		if (!dir.exists())
			dir.mkdir();
		for (File file : dir.listFiles()) {
			if (file.isFile() && file.getName().endsWith(".mrv")) {
				try {
					Map<String, String> map = new HashMap<>();
					BufferedReader reader = Files.newBufferedReader(file.toPath());
					String line;
					while ((line = reader.readLine()) != null) {
						String[] data = line.split("=", 2);
						map.put(data[0], data[1]);
					}
					SuperChatListener.PROGRESS.put(file.getName().substring(0, file.getName().length() - 4), map);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
 	}

	public static void save() {
		Map<String, Map<String, String>> map = SuperChatListener.PROGRESS;
		File dir = new File("superchat_data");
		if (!dir.exists())
			dir.mkdir();
		for (Entry<String, Map<String, String>> entry : map.entrySet()) {
			try {
				File file = new File(dir, entry.getKey() + ".mrv");
				BufferedWriter writer = Files.newBufferedWriter(file.toPath());
				for (Entry<String, String> e : entry.getValue().entrySet()) {
					writer.write(e.getKey() + "=" + e.getValue());
					writer.newLine();
				}
				writer.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
