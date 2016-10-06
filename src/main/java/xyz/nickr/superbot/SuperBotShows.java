package xyz.nickr.superbot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import xyz.nickr.jomdb.JOMDBException;
import xyz.nickr.jomdb.JavaOMDB;
import xyz.nickr.jomdb.model.SeasonEpisodeResult;
import xyz.nickr.jomdb.model.SeasonResult;

/**
 * Contains all logic pertaining to the different shows the bot tracks.
 *
 * @author Nick Robson
 * @author Horrgs
 */
public class SuperBotShows {

    public static final Map<String, String> SHOWS_BY_NAME = new HashMap<>();
    public static final Map<String, Show> SHOWS_BY_ID = new HashMap<>();
    public static final Pattern EPISODE_PATTERN = Pattern.compile("S0*([1-9][0-9]*)E0*([1-9][0-9]*)", Pattern.CASE_INSENSITIVE);

    private static final Map<Integer, String> days = new HashMap<>();

    static {
        SuperBotShows.days.put(Calendar.SUNDAY, "Sunday");
        SuperBotShows.days.put(Calendar.MONDAY, "Monday");
        SuperBotShows.days.put(Calendar.TUESDAY, "Tuesday");
        SuperBotShows.days.put(Calendar.WEDNESDAY, "Wednesday");
        SuperBotShows.days.put(Calendar.THURSDAY, "Thursday");
        SuperBotShows.days.put(Calendar.FRIDAY, "Friday");
        SuperBotShows.days.put(Calendar.SATURDAY, "Saturday");
    }

    public static void setup() {
        if (SuperBotShows.SHOWS_BY_ID.isEmpty()) {
            SuperBotShows.loadShows();
        }
    }

    public static boolean addLink(String imdb, String link) {
        if (imdb == null || link == null) {
            return false;
        }
        Show show = SuperBotShows.getShow(imdb, false);
        if (show == null) {
            return false;
        }
        SuperBotShows.SHOWS_BY_NAME.put(link.toLowerCase(), imdb);
        show.addLink(link);
        return SuperBotShows.saveShows();
    }

    public static boolean removeLink(String link) {
        if (link == null) {
            return false;
        }
        link = link.toLowerCase();
        String imdb = SuperBotShows.SHOWS_BY_NAME.remove(link);
        if (imdb == null) {
            return false;
        }
        if (!SuperBotShows.SHOWS_BY_NAME.values().contains(imdb)) {
            SuperBotShows.SHOWS_BY_ID.remove(imdb);
        } else {
            Show show = SuperBotShows.SHOWS_BY_ID.get(imdb);
            show.links.remove(link);
        }
        return SuperBotShows.saveShows();
    }

    public static Collection<Show> getShows() {
        return SuperBotShows.SHOWS_BY_ID.values();
    }

    public static Show getShow(String ident) {
        return SuperBotShows.getShow(ident, false);
    }

    public static Show getShow(String ident, boolean create) {
        if (ident == null) {
            return null;
        }
        ident = ident.toLowerCase();
        if (!JavaOMDB.IMDB_ID_PATTERN.matcher(ident).matches()) {
            ident = SuperBotShows.SHOWS_BY_NAME.get(ident);
        } else if (create && !SuperBotShows.SHOWS_BY_ID.containsKey(ident)) {
            SuperBotShows.SHOWS_BY_ID.put(ident, new Show(ident, SuperBotController.OMDB.titleById(ident).getTitle()));
        }
        return ident != null ? SuperBotShows.SHOWS_BY_ID.get(ident) : null;
    }

    public static void addShow(String imdb, Show show) {
        SuperBotShows.SHOWS_BY_ID.put(imdb, show);
        for (String link : show.links) {
            SuperBotShows.addLink(imdb, link);
        }
    }

    public static void loadShows() {
        JsonObject data = SuperBotShows.readJson();
        if (data == null) {
            return;
        }
        SuperBotShows.SHOWS_BY_NAME.clear();
        SuperBotShows.SHOWS_BY_ID.clear();
        for (JsonElement el : data.getAsJsonArray("shows")) {
            String imdb;
            JsonObject obj = null;
            if (el.isJsonObject()) {
                obj = el.getAsJsonObject();
                imdb = obj.get("imdb").getAsString();
            } else {
                imdb = el.getAsString();
            }
            String display = obj != null && obj.has("display") ? obj.get("display").getAsString() : SuperBotController.OMDB.titleById(imdb).getTitle();
            Show show = new Show(imdb, display);
            SuperBotShows.SHOWS_BY_ID.put(imdb, show);
        }
        for (JsonElement el : data.getAsJsonArray("links")) {
            if (el != null && el.isJsonObject()) {
                JsonObject obj = el.getAsJsonObject();
                SuperBotShows.addLink(obj.get("imdb").getAsString(), obj.get("link").getAsString());
            }
        }
    }

    public static boolean saveShows() {
        JsonObject data = new JsonObject();
        JsonArray shows = new JsonArray();
        JsonArray links = new JsonArray();
        for (Show show : SuperBotShows.SHOWS_BY_ID.values()) {
            JsonObject s = new JsonObject();
            s.addProperty("imdb", show.imdb);
            if (show.display != null) {
                s.addProperty("display", show.display);
            }
            shows.add(s);
            for (String link : show.links) {
                JsonObject obj = new JsonObject();
                obj.addProperty("imdb", show.imdb);
                obj.addProperty("link", link);
                links.add(obj);
            }
        }
        data.add("shows", shows);
        data.add("links", links);
        return SuperBotShows.writeJson(data);
    }

    public static JsonObject readJson() {
        String fname = "shows.json";
        File f = new File(fname);
        File bk = new File(fname + ".bak");
        JsonObject data = SuperBotShows.readJson(f);
        if (data == null) {
            data = SuperBotShows.readJson(bk);
        }
        return data;
    }

    private static JsonObject readJson(File f) {
        try {
            return SuperBotController.GSON.fromJson(new FileReader(f), JsonObject.class);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static boolean writeJson(JsonObject data) {
        String fname = "shows.json";
        File f = new File(fname);
        File bk = new File(fname + ".bak");
        try {
            if (f.exists()) {
                f.renameTo(bk);
            }
            BufferedWriter writer = Files.newBufferedWriter(f.toPath(), StandardOpenOption.CREATE);
            SuperBotController.GSON.toJson(data, writer);
            writer.close();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            try {
                Files.copy(bk.toPath(), f.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static final class Show {

        private final String imdb, display;
        private final Set<String> links;
        private String latest;
        private Integer totalSeasons;
        private boolean dateCached;
        private SeasonResult season;
        private Calendar date;

        private Map<String, SeasonResult> seasons = new TreeMap<>((s1, s2) -> {
            try {
                return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));
            } catch (Exception ex) {
                return s1.compareTo(s2);
            }
        });

        public Show(String imdb, String display, String... links) {
            this(imdb, display, Arrays.asList(links));
        }

        public Show(String imdb, String display, Collection<String> links) {
            this.imdb = imdb;
            this.display = display;
            this.links = new TreeSet<>(links);
        }

        public String getDisplay() {
            return this.display;
        }

        public String getIMDB() {
            return this.imdb;
        }

        public Set<String> getLinks() {
            return links;
        }

        public void addLink(String link) {
            this.links.add(link.toLowerCase());
        }

        public SeasonResult getSeason(String season) {
            try {
                if (!this.seasons.containsKey(season)) {
                    this.seasons.put(season, SuperBotController.OMDB.seasonById(this.imdb, String.valueOf(season)));
                }
                return this.seasons.get(season);
            } catch (JOMDBException ex) {
                this.seasons.put(season, null);
                return null;
            }
        }

        public List<SeasonResult> getSeasons() {
            List<SeasonResult> ss = new LinkedList<>();
            for (String key : this.seasons.keySet()) {
                ss.add(this.seasons.get(key));
            }
            return ss;
        }

        public SeasonResult getSeason() {
            if (this.season != null) {
                return this.season;
            }
            Calendar today = Calendar.getInstance();
            Calendar now = Calendar.getInstance();
            now.clear();
            now.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE));
            SeasonResult next = null;
            int n = 0;
            try {
                do {
                    next = this.getSeason(String.valueOf(++n));
                    if (next != null) {
                        this.season = next;
                    }
                    if (next != null && next.getEpisodes().length > 0) {
                        SeasonEpisodeResult[] episodes = next.getEpisodes();
                        SeasonEpisodeResult first = next.getEpisodes()[0],
                                        last = episodes[episodes.length - 1];
                        Calendar a = first.getReleaseDate(), b = last.getReleaseDate();
                        if (a == null || b == null) {
                            break;
                        } else if (a.after(now) && b.after(now)) {
                            this.season = next;
                        } else if (!a.after(now) && !b.before(now)) {
                            this.season = next;
                            break;
                        }
                    }
                } while (next != null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return this.season;
        }

        public Calendar getDate() {
            if (this.dateCached) {
                return this.date;
            }
            this.dateCached = true;
            try {
                SeasonResult season = this.getSeason();
                Calendar today = Calendar.getInstance();
                Calendar now = Calendar.getInstance();
                now.clear();
                now.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE));
                if (season != null) {
                    List<SeasonEpisodeResult> eps = Arrays.asList(season.getEpisodes());
                    for (Iterator<SeasonEpisodeResult> it = eps.iterator(); it.hasNext();) {
                        Calendar cal = it.next().getReleaseDate();
                        if (cal != null && cal.after(now)) {
                            return this.date = cal;
                        }
                    }
                }
            } catch (JOMDBException ex) {
                // noop
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        public int getTotalSeasons() {
            if (this.totalSeasons == null) {
                try {
                    this.totalSeasons = SuperBotController.OMDB.titleById(this.imdb).getTotalSeasons();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return -1;
                }
            }
            return this.totalSeasons;
        }

        public String getLatestEpisode() {
            if (this.latest != null)
                return this.latest;
            Calendar now = Calendar.getInstance();
            int season = 1;
            while (true) {
                SeasonResult res = getSeason(String.valueOf(season));
                boolean done = false;
                if (res == null) {
                    break;
                }
                for (SeasonEpisodeResult ser : res) {
                    try {
                        Calendar release = ser.getReleaseDate();
                        if (release == null || release.after(now)) {
                            done = true;
                            break;
                        }
                        this.latest = String.format("S%sE%s", season, ser.getEpisode());
                    } catch (NumberFormatException ex) {}
                }
                if (done) {
                    break;
                }
                season++;
            }
            return this.latest;
        }

        public String getDateString() {
            return Show.getDateString(this.getDate());
        }

        public String getDay() {
            Calendar date = this.getDate();
            return date != null ? SuperBotShows.days.getOrDefault(date.get(Calendar.DAY_OF_WEEK), "N/A") : "N/A";
        }

        @Override
        public Show clone() {
            return new Show(this.imdb, this.display, this.links);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || o == this || o.getClass() != this.getClass()) {
                return o == this;
            }
            Show s = (Show) o;
            return this.imdb == s.imdb || this.imdb != null && this.imdb.equals(s.imdb);
        }

        public static String getDateString(Calendar date) {
            return date != null ? new SimpleDateFormat("E, d MMM yyyy").format(date.getTime()) : "N/A";
        }

    }

}
