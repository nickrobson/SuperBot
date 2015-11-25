package me.nickrobson.skype.superchat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SuperChatShows {

	public static final List<Show> TRACKED_SHOWS = new ArrayList<>();
	public static final Map<String, Show> SHOWS_BY_NAME = new HashMap<>();
	public static final Pattern EPISODE_PATTERN = Pattern.compile("S[1-9][0-9]?E[1-9][0-9]?");

    public static void setup() {
        if (TRACKED_SHOWS.size() > 0)
            return;
        // Format:
        //     register(new Show("Display Name", "main", "other_name", "any_other_name", "blah"));
        // Note:
        //     You only need the first two strings ("Display Name" and "main"), the others are optional!
        //     Please keep the "main" string to only contain lowercase english letters (as it is used for file names)!
        register(new Show("Arrow", "Wednesday", "arrow", "thearrow"));
        register(new Show("The Flash", "Tuesday", "flash", "theflash"));
        register(new Show("Agents of SHIELD", "Tuesday", "aos", "agents"));
        register(new Show("Agent Carter", null, "ac", "agentcarter"));
        register(new Show("Jessica Jones", "Netflix", "jj", "jessicajones"));
        register(new Show("Daredevil", "Netflix", "dd", "daredevil"));
        register(new Show("Supergirl", "Monday", "sg", "supergirl"));
        register(new Show("Gotham", "Monday", "gotham", "goth"));
        register(new Show("Constantine", null, "const", "constantine"));
        register(new Show("Narcos", "Netflix", "narcos", "narc"));
        register(new Show("Mr. Robot", null, "robot", "mrrobot"));
        register(new Show("sense8", "Netflix", "sensate", "sense8", "s8"));
        register(new Show("SleepyHollow", "Thursday", "sho", "sleho"));
        register(new Show("Grimm", "Friday", "grimm", "gri"));
        register(new Show("The Walking Dead", "Monday", "twd", "walkingdead"));
        register(new Show("Heroes", null, "heroes", "her"));
        register(new Show("Doctor Who", "Sunday", "dw", "doctorwho"));
        register(new Show("The Tomorrow People", null, "ttp", "tmp"));
        register(new Show("The Messengers", null, "tmsg", "messengers"));
        register(new Show("Limitless", "Tuesday", "limitless", "lmtl"));
        register(new Show("iZombie", "Tuesday", "izombie", "izo"));
        register(new Show("Blindspot", "Monday", "blindspot", "blind", "bli"));
        register(new Show("Empire", "Wednesday", "empire", "emp"));
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
		
		public final String display;
		public final String[] names;
		public final String day;
		
		public Show(String display, String day, String... names) {
			this.display = display;
			this.day = day;
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
