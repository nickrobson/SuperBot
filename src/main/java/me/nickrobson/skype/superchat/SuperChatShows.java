package me.nickrobson.skype.superchat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Contains all logic pertaining to the different shows the bot tracks.
 *
 * @author Nick Robson
 * @author Horrgs
 */
public class SuperChatShows {

    public static final List<Show>        TRACKED_SHOWS   = new ArrayList<>();
    public static final Map<String, Show> SHOWS_BY_NAME   = new HashMap<>();
    public static final Pattern           EPISODE_PATTERN = Pattern.compile("S[1-9][0-9]?E[1-9][0-9]?");

    public static void setup() {
        if (TRACKED_SHOWS.size() > 0)
            return;
        loadShows();
    }

    public static void loadShows() {
        JsonArray jsonArray = readJson();
        for (int x = 0, arrsize = jsonArray.size(); x < arrsize; x++) {
            JsonElement el = jsonArray.get(x);
            if (el != null && el.isJsonObject()) {
                JsonObject obj = el.getAsJsonObject();
                if (obj.has("aliases")) {
                    String display = obj.get("showname").getAsString();
                    String day = obj.get("day").getAsString();
                    JsonArray aliases = obj.getAsJsonArray("aliases");
                    int size = aliases.size();
                    String[] names = new String[size];
                    for (int i = 0; i < size; i++) {
                        names[i] = aliases.get(i).getAsString();
                    }
                    register(new Show(display, day, names));
                }
            }
        }
    }

    public static boolean addShow(Show show) {
        if (show == null)
            return false;
        JsonArray arr = readJson();
        if (arr != null) {
            register(show);
            JsonObject showDetails = new JsonObject();
            JsonArray aliases = new JsonArray();
            Arrays.asList(show.names).forEach(aliases::add);
            showDetails.addProperty("showname", show.display);
            showDetails.addProperty("day", show.day);
            showDetails.add("aliases", aliases);
            arr.add(showDetails);
            return writeJson(arr);
        }
        return false;
    }

    public static boolean removeShow(String showName) {
        if (showName == null)
            return false;
        Show show = getShow(showName);
        if (show == null)
            return false;
        unregister(show);
        JsonArray arr = readJson();
        for (Iterator<JsonElement> it = arr.iterator(); it.hasNext();) {
            JsonElement el = it.next();
            if (!el.isJsonObject()) {
                it.remove();
                continue;
            }
            JsonObject obj = el.getAsJsonObject();
            if (!obj.has("showname"))
                it.remove();
            else if (obj.get("showname").getAsString().equals(show.display)) {
                it.remove();
            }
        }
        return writeJson(arr);
    }

    public static JsonArray readJson() {
        String fname = "shows.json";
        try {
            File f = new File(fname);
            if (f.exists()) {
                return SuperChatController.GSON.fromJson(new FileReader(f), JsonArray.class);
            } else {
                throw new FileNotFoundException("shows.json is missing");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static boolean writeJson(JsonArray arr) {
        String fname = "shows.json";
        try {
            File f = new File(fname);
            if (f.exists()) {
                SuperChatController.GSON.toJson(arr, Files.newBufferedWriter(f.toPath(), StandardOpenOption.CREATE));
                return true;
            } else {
                throw new FileNotFoundException("shows.json is missing");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static void register(Show show) {
        TRACKED_SHOWS.add(show);
        for (String s : show.names)
            SHOWS_BY_NAME.put(s.toLowerCase(), show);
    }

    public static void unregister(Show show) {
        TRACKED_SHOWS.remove(show);
        for (String s : show.names)
            SHOWS_BY_NAME.remove(s.toLowerCase(), show);
    }

    public static Show getShow(String name) {
        return SHOWS_BY_NAME.get(name.toLowerCase());
    }

    public static final class Show {

        public final String   display;
        public final String[] names;
        public final String   day;

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
