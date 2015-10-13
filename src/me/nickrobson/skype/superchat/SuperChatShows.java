package me.nickrobson.skype.superchat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SuperChatShows {

	public static final List<Show> TRACKED_SHOWS = new ArrayList<>();
	public static final Map<String, Show> SHOWS_BY_NAME = new HashMap<>();
	public static final Pattern EPISODE_PATTERN = Pattern.compile("S[0-9]E[0-9]{1,2}");
	
	public static void setup() {
		if (TRACKED_SHOWS.size() > 0)
			return;
		// format:
		// you only need the first two strings ("Display Name" and "main"), the others are optional!
		// Please keep the "main" string to only contain lowercase english letters (as it is used for file names)!
		// register(new Show("Display Name", "main", "other_name", "any_other_name", "blah"));
		register(new Show("Arrow", "arrow", "thearrow"));
		register(new Show("The Flash", "flash", "theflash"));
		register(new Show("Agents of SHIELD", "aos", "agentsofshield"));
		register(new Show("Agent Carter", "ac", "agentcarter"));
		register(new Show("Jessica Jones", "jj", "jessicajones"));
		register(new Show("Daredevil", "dd", "daredevil"));
		register(new Show("Supergirl", "sg", "supergirl"));
		register(new Show("Gotham", "gotham", "goth"));
		register(new Show("Constantine", "const", "constantine"));
		register(new Show("Narcos", "narcos", "narc"));
		register(new Show("Mr. Robot", "robot", "mrrobot"));
		register(new Show("sense8", "sensate", "sense8", "s8"));
		register(new Show("SleepyHollow", "sh", "sleho"));
		register(new Show("Grimm", "grimm", "gr"));
		register(new Show("The Walking Dead", "twd", "thewalkingdead"));
		register(new Show("Heroes", "heroes", "her"));
	}
	
	public static void register(Show show) {
		TRACKED_SHOWS.add(show);
		for (String s : show.names)
			SHOWS_BY_NAME.put(s.toLowerCase(), show);
	}
	
	public static Show getShow(String name) {
		return SHOWS_BY_NAME.get(name.toLowerCase());
	}
	
	public static final class Show {
		
		String display;
		String[] names;
		
		public Show(String display, String... names) {
			this.display = display;
			this.names = names;
		}
		
		public String getDisplay() {
			return display;
		}
		
		public String[] getNames() {
			return names;
		}
		
		public String getMainName() {
			return names[0];
		}
		
	}

}
