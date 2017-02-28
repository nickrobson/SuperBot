package xyz.nickr.superbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import xyz.nickr.jomdb.JavaOMDB;
import xyz.nickr.superbot.sys.GroupConfiguration;
import xyz.nickr.superbot.sys.Profile;
import xyz.nickr.superbot.sys.Sys;

/**
 * @author Nick Robson
 */
public class SuperBotResource {

    public static final Map<String, Sys> PROVIDERS = new HashMap<>();

    public static final Map<String, Map<String, String>> PROGRESS = new TreeMap<>();

    public static final List<String> HANGMAN_PHRASES = new LinkedList<>();
    public static final String WELCOME_MESSAGE = "Welcome to %s";
    public static final String WELCOME_MESSAGE_JOIN = "Welcome, %s, to %s";

    public static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
    public static final CloseableHttpClient HTTP = HttpClients.createDefault();
    public static final JavaOMDB OMDB = new JavaOMDB();

    public static String whichEarlier(String a, String b) {
        String[] ad = a.substring(1).split("E");
        String[] bd = b.substring(1).split("E");
        int as = Integer.parseInt(ad[0]);
        int ae = Integer.parseInt(ad[1]);
        int bs = Integer.parseInt(bd[0]);
        int be = Integer.parseInt(bd[1]);
        if (as < bs) {
            return a;
        }
        if (bs < as) {
            return b;
        }
        return ae < be ? a : b;
    }

    public static AtomicBoolean wipe(String toRemove) {
        AtomicBoolean wiped = new AtomicBoolean(false);
        Map<String, Map<String, String>> originalProgress = new HashMap<>(PROGRESS);
        originalProgress.forEach((s, prg) -> {
            if (prg.containsKey(toRemove)) {
                prg.remove(toRemove);
                wiped.set(true);
                PROGRESS.put(s, prg);
            }
        });
        return wiped;
    }

    public static String getUsersOn(String show, String ep) {
        if (!SuperBotShows.EPISODE_PATTERN.matcher(ep).matches()) {
            return null;
        }

        List<String> users = getProgress(show).entrySet().stream().filter(e -> e.getValue().equals(ep)).map(Map.Entry::getKey).collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        for (String s : users) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            Optional<Profile> p = Profile.getProfile(s);
            sb.append(p.isPresent() ? p.get().getName() : s);
        }
        return sb.toString();
    }

    public static Map<String, String> getProgress(SuperBotShows.Show show) {
        return getProgress(show == null ? null : show.getIMDB());
    }

    public static Map<String, String> getProgress(String show) {
        if (show == null) {
            return null;
        }
        return PROGRESS.computeIfAbsent(show, s -> new HashMap<>());
    }

    public static Map<SuperBotShows.Show, String> getUserProgress(String username) {
        Map<SuperBotShows.Show, String> prg = new HashMap<>();
        PROGRESS.forEach((s, m) -> {
            if (m.containsKey(username.toLowerCase())) {
                prg.put(SuperBotShows.getShow(s), m.get(username.toLowerCase()));
            }
        });
        return prg;
    }


    public static void registerProvider(Sys sys) {
        SuperBotResource.PROVIDERS.put(sys.getName(), sys);
    }

    public static void load(Consumer<String> callback) {
        if (callback == null) {
            callback = s -> {};
        }
        callback.accept("0/5");
        loadProgress();
        callback.accept("1/5");
        loadGroups();
        callback.accept("2/5");
        loadPermissions();
        callback.accept("3/5");
        loadHangmanWords();
        callback.accept("4/5");
        SuperBotProfiles.loadProfiles();
        callback.accept("5/5");
    }

    public static void loadPermissions() {
        SuperBotPermissions.clear();
        File permsFolder = new File("permissions");
        if (!permsFolder.exists()) {
            permsFolder.mkdirs();
        } else {
            for (File f : permsFolder.listFiles()) {
                try {
                    String username = f.getName().toLowerCase();
                    Files.readAllLines(f.toPath()).forEach(s -> SuperBotPermissions.set(username, s, true, false));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void savePermissions() {
        File permsFolder = new File("permissions");
        if (!permsFolder.exists()) {
            permsFolder.mkdirs();
        }
        for (Map.Entry<String, Set<String>> entry : SuperBotPermissions.permissions.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                File f = new File(permsFolder, entry.getKey().toLowerCase());
                if (f.exists()) {
                    f.delete();
                }
                try {
                    Files.write(f.toPath(), entry.getValue(), StandardOpenOption.CREATE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void loadHangmanWords() {
        SuperBotResource.HANGMAN_PHRASES.clear();
        File f = new File("hangman.txt");
        if (f.exists()) {
            try (BufferedReader reader = Files.newBufferedReader(f.toPath())) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith("Chapter")) {
                        SuperBotResource.HANGMAN_PHRASES.add(line.toUpperCase());
                    }
                }
                Collections.shuffle(SuperBotResource.HANGMAN_PHRASES);
            } catch (IOException ex) {}
        }
    }

    public static void loadProgress() {
        File dir = new File("superchat_data");
        if (!dir.exists()) {
            dir.mkdir();
        }
        for (File file : dir.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                try {
                    String showID = file.getName().substring(0, file.getName().length() - 5);
                    if (JavaOMDB.IMDB_ID_PATTERN.matcher(showID).matches()) {
                        BufferedReader reader = Files.newBufferedReader(file.toPath());
                        Map<String, String> map = new HashMap<>();
                        SuperBotResource.GSON.fromJson(reader, JsonObject.class).entrySet().forEach(e -> map.put(e.getKey(), e.getValue().getAsString()));
                        SuperBotResource.PROGRESS.put(showID, map);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void saveProgress() {
        Map<String, Map<String, String>> map = SuperBotResource.PROGRESS;
        File dir = new File("superchat_data");
        if (!dir.exists()) {
            dir.mkdir();
        }
        for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
            try {
                File file = new File(dir, entry.getKey() + ".json");
                JsonObject obj = new JsonObject();
                entry.getValue().forEach((k, v) -> obj.addProperty(k, v));
                BufferedWriter writer = Files.newBufferedWriter(file.toPath());
                SuperBotResource.GSON.toJson(obj, writer);
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void loadGroups() {
        File dir = new File("groups");
        if (!dir.exists()) {
            dir.mkdir();
        }
        for (File f : dir.listFiles()) {
            GroupConfiguration cfg = new GroupConfiguration(f);
            if (cfg.getProvider() != null && cfg.getUniqueId() != null) {
                Sys provider = SuperBotResource.PROVIDERS.get(cfg.getProvider());
                if (provider != null) {
                    GroupConfiguration.put(provider, cfg.getUniqueId(), cfg);
                }
            }
        }
    }

}
