package me.nickrobson.skype.superchat;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class SuperChatShows {
	
	public static final Pattern EPISODE_PATTERN = Pattern.compile("S[0-9]E[0-9]{1,2}");
	
	public static final String[] TRACKED_SHOWS = {
		"arrow:thearrow", "arrow:arrow",
		"flash:theflash", "flash:flash",
		"aos:agentsofshield", "aos:aos",
		"jj:jessicajones", "jj:jj",
		"ac:agentcarter", "ac:ac",
		"dd:daredevil", "dd:dd",
		"sg:supergirl", "sg:sg",
		"gotham:gotham", "gotham:goth",
		"const:constantine", "const:const",
	};
	
	public static final Map<String, String> DISPLAY_NAMES = new TreeMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put("aos", "Agents of SHIELD");
			put("jj", "Jessica Jones");
			put("ac", "Agent Carter");
			put("dd", "Daredevil");
			put("arrow", "Arrow");
			put("flash", "The Flash");
			put("sg", "Supergirl");
			put("gotham", "Gotham");
			put("const", "Constantine");
		}
	};
	
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
	
	public static String getMainName(String name) {
		name = name.toLowerCase();
		for (String s : TRACKED_SHOWS) {
			if (s.split(":")[1].equals(name)) {
				return s.split(":")[0];
			}
		}
		return null;
	}

}
