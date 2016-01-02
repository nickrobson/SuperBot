package me.nickrobson.skype.superchat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SuperChatShows {

    public static final List<Show>        TRACKED_SHOWS   = new ArrayList<>();
    public static final Map<String, Show> SHOWS_BY_NAME   = new HashMap<>();
    public static final Pattern           EPISODE_PATTERN = Pattern.compile("S[1-9][0-9]?E[1-9][0-9]?");

    public static void setup() {
        if (TRACKED_SHOWS.size() > 0)
            return;
        // Format:
        // register(new Show("Display Name", "main", "other_name",
        // "any_other_name", "blah"));
        // Note:
        // You only need the first two strings ("Display Name" and "main"), the
        // others are optional!
        // Please keep the "main" string to only contain lowercase english
        // letters (as it is used for file names)!
        loadShows();
    }

    public static void loadShows() {
        JsonArray jsonArray = (JsonArray) parseJson("shows.json");
        String[] showAliases = null;
        for(int x = 0; x < jsonArray.size(); x++) {
            JsonObject showSection = jsonArray.get(x).getAsJsonObject();
            if(showSection != null) {
                if(showSection.get("aliases") != null) {
                    int length = showSection.get("aliases").getAsJsonArray().size();
                    if(length > 1) {
                        showAliases = new String[length];
                        for(int i = 0; i < showSection.get("aliases").getAsJsonArray().size(); i++) {
                            showAliases[x] = showSection.get("aliases").getAsJsonArray().get(x).getAsString();
                        }
                    } else {
                        showAliases = new String[1];
                        showAliases[0] = showSection.get("aliases").getAsJsonArray().get(0).getAsString();
                    }
                }
                String showName = showSection.get("showname").getAsString();
                String showDay = showSection.get("day").getAsString();
                register(new Show(showName, showDay, showAliases));
            }
        }
    }

    public static void addShow(Show show) {
        JsonArray jsonArray = (JsonArray) parseJson("shows.json");
        try {
            JsonObject showDetails = new JsonObject();
            jsonArray.add(showDetails);
            showDetails.addProperty("showname", show.getDisplay());
            showDetails.addProperty("day", show.day);
            JsonArray aliases = new JsonArray();
            for(String str : show.getNames()) {
                aliases.add(str.replace(" ", ""));
            }
            showDetails.add("aliases", aliases);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("shows.json", true));
            bufferedWriter.write(jsonArray.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
            register(show);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void removeShow(String showName) {
        JsonArray jsonArray = (JsonArray) parseJson("shows.json");
        JsonArray newJsonArray  = new JsonArray();
        try {
            for(int x = 0; x < jsonArray.size(); x++) {
                if(!jsonArray.get(x).getAsJsonObject().get("showname").getAsString().equals(showName)) {
                    newJsonArray.add(jsonArray.get(x));
                }
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("shows.json", false));
            bufferedWriter.write(jsonArray.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
            unregister(getShow(showName));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static JsonElement parseJson(String fileName) {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = null;
        try {
            if(new File(fileName).exists()) {
                Object obj = jsonParser.parse(new FileReader(new File(fileName)));
                jsonElement  = (JsonElement) obj;
                System.out.println("Parsing " + fileName + ".json ....");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return jsonElement;
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
