package xyz.nickr.superbot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
public class SuperBotShows {

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
            arr.add(show.toJson());
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

    public static boolean editShow(String name, Show show) {
        if (name == null || show == null)
            return false;
        JsonArray arr = readJson();
        for (int i = 0; i < arr.size(); i++) {
            JsonArray names = arr.get(i).getAsJsonObject().getAsJsonArray("aliases");
            int j;
            for (j = 0; j < names.size(); j++) {
                if (name.equals(names.get(j).getAsString())) {
                    arr.set(i, show.toJson());
                    break;
                }
            }
            if (j == names.size())
                return false;
        }
        return writeJson(arr);
    }

    public static JsonArray readJson() {
        String fname = "shows.json";
        File f = new File(fname);
        File bk = new File(fname + ".bak");
        JsonArray arr = readJson(f);
        if (arr == null)
            arr = readJson(bk);
        return arr;
    }

    private static JsonArray readJson(File f) {
        try {
            if (f.exists()) {
                return SuperBotController.GSON.fromJson(new FileReader(f), JsonArray.class);
            } else {
                throw new FileNotFoundException(f.getName() + " is missing");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static boolean writeJson(JsonArray arr) {
        String fname = "shows.json";
        File f = new File(fname);
        File bk = new File(fname + ".bak");
        try {
            if (f.exists())
                f.renameTo(bk);
            BufferedWriter writer = Files.newBufferedWriter(f.toPath(), StandardOpenOption.CREATE);
            SuperBotController.GSON.toJson(arr, writer);
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

        public String   display;
        public String[] names;
        public String   day;

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

        @Override
        public Show clone() {
            return new Show(display, day, names);
        }

        public JsonObject toJson() {
            JsonObject showDetails = new JsonObject();
            JsonArray aliases = new JsonArray();
            Arrays.asList(names).forEach(aliases::add);
            showDetails.addProperty("showname", display);
            showDetails.addProperty("day", day);
            showDetails.add("aliases", aliases);
            return showDetails;
        }

        public static Show fromJson(JsonObject obj) {
            String disp = obj.get("showname").getAsString();
            String day  = obj.get("day").getAsString();
            JsonArray arr = obj.getAsJsonArray("aliases");
            String[] names = new String[arr.size()];
            for (int i = 0; i < arr.size(); i++)
                names[i] = arr.get(i).getAsString();
            return new Show(disp, day, names);
        }

    }

}
