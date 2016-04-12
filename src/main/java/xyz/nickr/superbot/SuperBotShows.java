package xyz.nickr.superbot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import xyz.nickr.jomdb.JOMDBException;
import xyz.nickr.jomdb.JavaOMDB;
import xyz.nickr.jomdb.model.SeasonEpisodeResult;
import xyz.nickr.jomdb.model.SeasonResult;
import xyz.nickr.jomdb.model.TitleResult;

/**
 * Contains all logic pertaining to the different shows the bot tracks.
 *
 * @author Nick Robson
 * @author Horrgs
 */
public class SuperBotShows {

    public static final Map<String, String> SHOWS_BY_NAME   = new HashMap<>();
    public static final Map<String, Show>   SHOWS_BY_ID     = new HashMap<>();
    public static final Pattern             EPISODE_PATTERN = Pattern.compile("S[1-9][0-9]?E[1-9][0-9]?");

    private static final Map<Integer, String> days = new HashMap<>();

    static {
        days.put(Calendar.SUNDAY, "Sunday");
        days.put(Calendar.MONDAY, "Monday");
        days.put(Calendar.TUESDAY, "Tuesday");
        days.put(Calendar.WEDNESDAY, "Wednesday");
        days.put(Calendar.THURSDAY, "Thursday");
        days.put(Calendar.FRIDAY, "Friday");
        days.put(Calendar.SATURDAY, "Saturday");
    }

    public static void setup() {
        if (SHOWS_BY_ID.isEmpty())
            loadShows();
    }

    public static boolean addLink(String imdb, String link) {
        if (imdb == null || link == null)
            return false;
        Show show = getShow(imdb, false);
        if (show == null)
            return false;
        SHOWS_BY_NAME.put(link.toLowerCase(), imdb);
        show.addLink(link);
        return saveShows();
    }

    public static boolean removeLink(String link) {
        if (link == null)
            return false;
        SHOWS_BY_NAME.remove(link.toLowerCase());
        return saveShows();
    }

    public static Show getShow(String ident) {
        return getShow(ident, false);
    }

    public static Show getShow(String ident, boolean create) {
        if (ident == null)
            return null;
        if (!JavaOMDB.IMDB_ID_PATTERN.matcher(ident).matches()) {
            ident = SHOWS_BY_NAME.get(ident);
        } else if (create && !SHOWS_BY_ID.containsKey(ident)) {
            SHOWS_BY_ID.put(ident, new Show(ident));
        }
        return ident != null ? SHOWS_BY_ID.get(ident) : null;
    }

    public static void loadShows() {
        JsonObject data = readJson();
        if (data == null)
            return;
        SHOWS_BY_NAME.clear();
        SHOWS_BY_ID.clear();
        for (JsonElement el : data.getAsJsonArray("shows")) {
            String imdb = el.getAsString();
            Show show = new Show(imdb);
            SHOWS_BY_ID.put(imdb, show);
        }
        for (JsonElement el : data.getAsJsonArray("links")) {
            if (el != null && el.isJsonObject()) {
                JsonObject obj = el.getAsJsonObject();
                addLink(obj.get("imdb").getAsString(), obj.get("link").getAsString());
            }
        }
    }

    public static boolean saveShows() {
        JsonObject data = new JsonObject();
        JsonArray shows = new JsonArray();
        JsonArray links = new JsonArray();
        for (Show show : SHOWS_BY_ID.values()) {
            shows.add(show.imdb);
            for (String link : show.links) {
                JsonObject obj = new JsonObject();
                obj.addProperty("imdb", show.imdb);
                obj.addProperty("link", link);
                links.add(obj);
            }
        }
        data.add("shows", shows);
        data.add("links", links);
        return writeJson(data);
    }

    public static JsonObject readJson() {
        String fname = "shows.json";
        File f = new File(fname);
        File bk = new File(fname + ".bak");
        JsonObject data = readJson(f);
        if (data == null)
            data = readJson(bk);
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
            if (f.exists())
                f.renameTo(bk);
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

        public final String imdb;
        public final Set<String> links;

        private String display, day;

        public Show(String imdb, String... links) {
            this.imdb = imdb;
            this.links = new TreeSet<>(Arrays.asList(links));
        }

        public Show(String imdb, Collection<String> links) {
            this.imdb = imdb;
            this.links = new TreeSet<>(links);
        }

        public void addLink(String link) {
            links.add(link.toLowerCase());
        }

        public String getDisplay() {
            if (display != null)
                return display;
            TitleResult res = SuperBotController.OMDB.titleById(imdb);
            return this.display = res != null ? res.title : null;
        }

        public String getDay() {
            if (day != null)
                return day;
            JavaOMDB omdb = SuperBotController.OMDB;
            int n = 0;
            SeasonResult season = null;
            try {
                while (true) {
                    season = omdb.seasonById(imdb, String.valueOf(n + 1));
                    n++;
                }
            } catch (JOMDBException ignored) {}
            if (n > 0) {
                Calendar now = Calendar.getInstance();
                List<SeasonEpisodeResult> eps = Arrays.asList(season.episodes);
                SeasonEpisodeResult ser = null;
                for (Iterator<SeasonEpisodeResult> it = eps.iterator(); it.hasNext();) {
                    Calendar cal = (ser = it.next()).getReleaseDate();
                    if (cal != null && !cal.after(now)) {
                        return this.day = days.getOrDefault(ser.getReleaseDate().get(Calendar.DAY_OF_WEEK), "N/A");
                    }
                }
            }
            return this.day = "N/A";
        }

        @Override
        public Show clone() {
            return new Show(imdb, links);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || o == this || o.getClass() != getClass())
                return o == this;
            Show s = (Show) o;
            return imdb == s.imdb || imdb != null && imdb.equals(s.imdb);
         }

    }

}
