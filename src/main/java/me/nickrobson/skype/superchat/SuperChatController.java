package me.nickrobson.skype.superchat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import me.nickrobson.skype.superchat.SuperChatShows.Show;
import me.nickrobson.skype.superchat.cmd.Command;
import me.nickrobson.skype.superchat.cmd.ConvertCommand;
import me.nickrobson.skype.superchat.cmd.GIDCommand;
import me.nickrobson.skype.superchat.cmd.GitCommand;
import me.nickrobson.skype.superchat.cmd.HangmanCommand;
import me.nickrobson.skype.superchat.cmd.HelpCommand;
import me.nickrobson.skype.superchat.cmd.ProgressCommand;
import me.nickrobson.skype.superchat.cmd.SetProgressCommand;
import me.nickrobson.skype.superchat.cmd.ShowsCommand;
import me.nickrobson.skype.superchat.cmd.StopCommand;
import me.nickrobson.skype.superchat.cmd.TimetableCommand;
import me.nickrobson.skype.superchat.cmd.ViewingOrderCommand;
import me.nickrobson.skype.superchat.cmd.WhoCommand;
import me.nickrobson.skype.superchat.cmd.WipeCommand;
import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.SkypeAPI;
import xyz.gghost.jskype.internal.packet.requests.GroupMetaRequest;

/**
 * @author Nick Robson
 */
public class SuperChatController {
	
	public static final Map<String, GroupConfiguration> GCONFIGS = new HashMap<>();
	
	public static final Map<String, Command> COMMANDS = new HashMap<>();
	public static final Map<String, Map<String, String>> PROGRESS = new TreeMap<>();
	
	public static final List<String> HANGMAN_PHRASES = new LinkedList<>();
	
	public static final String COMMAND_PREFIX = "~";
	
	public static final String WELCOME_MESSAGE = "Welcome to %s";
	public static final String WELCOME_MESSAGE_JOIN = "Welcome, %s, to %s";
	
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
			
			loadProgress();
			commands();
			loadGroups();
			loadHangmanWords();

			HELP_IGNORE_WHITESPACE = properties.getOrDefault("help.whitespace", "false").equalsIgnoreCase("true");
			HELP_WELCOME_CENTRED = properties.getOrDefault("help.welcome.centred", "false").equalsIgnoreCase("true");
			
			System.out.println("Logging in with " + properties.get("username") + " : ********");
			
			skype = new SkypeAPI(properties.get("username"), properties.get("password"));
			skype.login();
			skype.getEventManager().registerListener(new SuperChatListener());
			
			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(1000 * 60 * 60 * 8);
					} catch (InterruptedException e) {}
					System.exit(0);
				}
			}).start();
			
			while (true) {}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void loadHangmanWords() {
		File f = new File("hangman.txt");
		if (f.exists()) {
			try (BufferedReader reader = Files.newBufferedReader(f.toPath())) {
				String line;
				while ((line = reader.readLine()) != null)
					if (!line.startsWith("Chapter"))
						HANGMAN_PHRASES.add(line.toUpperCase());
				HANGMAN_PHRASES.sort((s1, s2) -> s1.substring(3, 5).compareTo(s2.substring(3, 5)));
			} catch (IOException ex) {}
		}
	}

	private static void register(Command cmd) {
		for (String name : cmd.names())
			COMMANDS.put(name, cmd);
		cmd.init();
	}
	
	private static void commands() {
		register(new HelpCommand());
		register(new ProgressCommand());
		register(new SetProgressCommand());
		register(new ShowsCommand());
		register(new StopCommand());
		register(new WipeCommand());
		register(new GitCommand());
		register(new ViewingOrderCommand());
		register(new WhoCommand());
		register(new HangmanCommand());
		register(new ConvertCommand());
		register(new GIDCommand());
		register(new TimetableCommand());
	}

	public static void loadProgress() {
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
					PROGRESS.put(file.getName().substring(0, file.getName().length() - 4), map);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
 	}

	public static void saveProgress() {
		Map<String, Map<String, String>> map = PROGRESS;
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
	
	public static void loadGroups() {
		File dir = new File("groups");
		if (!dir.exists())
			dir.mkdir();
		for (File f : dir.listFiles()) {
			GroupConfiguration cfg = new GroupConfiguration(f);
			if (cfg.getLongGroupId() != null) {
				GCONFIGS.put(cfg.getLongGroupId(), cfg);
			}
		}
	}
	
	public static String whichEarlier(String a, String b) {
		String[] ad = a.substring(1).split("E");
		String[] bd = b.substring(1).split("E");
		int as = Integer.parseInt(ad[0]);
		int ae = Integer.parseInt(ad[1]);
		int bs = Integer.parseInt(bd[0]);
		int be = Integer.parseInt(bd[1]);
		if (as < bs) return a;
		if (bs < as) return b;
		return ae < be ? a : b;
	}
	
	public static AtomicBoolean wipe(String toRemove) {
		AtomicBoolean wiped = new AtomicBoolean(false);
		PROGRESS.forEach((s, prg) -> {
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
		
		List<String> users = getProgress(show).entrySet()
				.stream()
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
	
	public static Map<String, String> getProgress(Show show) {
		return getProgress(show == null ? null : show.getMainName());
	}
	
	public static Map<String, String> getProgress(String show) {
		if (show == null) return null;
		Map<String, String> prg = PROGRESS.get(show);
		if (prg == null)
			prg = new HashMap<>();
		return prg;
	}
	
	public static Map<Show, String> getUserProgress(String username) {
		Map<Show, String> prg = new HashMap<>();
		PROGRESS.forEach((s, m) -> {
			if (m.containsKey(username))
				prg.put(SuperChatShows.getShow(s), m.get(username));
		}); 
		return prg;
	}

	public static Group getChatGroup() {
		return new GroupMetaRequest(skype).getGroup("19:c0cbadc10ca4415bac6be16bcec01450@thread.skype");
	}

}
