package xyz.nickr.superbot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import xyz.nickr.jomdb.JavaOMDB;
import xyz.nickr.superbot.SuperBotShows.Show;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupConfiguration;
import xyz.nickr.superbot.sys.GroupType;
import xyz.nickr.superbot.sys.Profile;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.gitter.GitterSys;
import xyz.nickr.superbot.sys.skype.SkypeSys;
import xyz.nickr.superbot.sys.telegram.TelegramSys;
import xyz.nickr.superbot.web.SuperBotServer;

/**
 * @author Nick Robson
 */
public class SuperBotController {

    public static final Map<String, Sys> PROVIDERS = new HashMap<>();

    public static final Map<String, Map<String, String>> PROGRESS = new TreeMap<>();

    public static final List<String> HANGMAN_PHRASES = new LinkedList<>();
    public static final String WELCOME_MESSAGE = "Welcome to %s";
    public static final String WELCOME_MESSAGE_JOIN = "Welcome, %s, to %s";

    public static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();

    public static String VERSION = "Unknown";
    public static int BUILD_NUMBER = 0;
    public static String[] GIT_COMMIT_IDS = new String[] { "Unknown" };
    public static String[] GIT_COMMIT_MESSAGES = new String[] { "Unknown" };
    public static String[] GIT_COMMIT_AUTHORS = new String[] { "Unknown" };

    public static final JavaOMDB OMDB = new JavaOMDB(false);

    public static void main(String[] args) {
        try {
            File config = new File("config.cfg");

            Properties properties = new Properties();
            properties.load(new FileInputStream(config));

            register(new SkypeSys(properties.getProperty("skype.username"), properties.getProperty("skype.password")));
            register(new TelegramSys(properties.getProperty("telegram.api")));
            register(new GitterSys(properties.getProperty("gitter.api")));

            try {
                // HTTP Server
                new SuperBotServer();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            SuperBotShows.setup();

            new Thread(() -> SuperBotShows.getShows().forEach(show -> {
                System.out.println("Fetched day for: " + show.display + " (" + show.getDay() + ")");
            })).start();

            load(null);

            PROVIDERS.forEach((s, sys) -> sys.onLoaded());

            new Thread(() -> {
                try {
                    Thread.sleep(1000 * 60 * 60 * 2); // 2 hours
                } catch (Exception ex) {}
                saveProgress();
                System.exit(0);
            }, "SuperBot Sleepy Thread").start();

            new Thread(() -> {
                while (true) {
                    File file = new File(".jenkins-built");
                    if (file.exists()) {
                        file.delete();
                        saveProgress();
                        System.exit(0);
                    }
                    try {
                        Thread.sleep(10_000); // 10 seconds
                    } catch (Exception ex) {}
                }
            }, "SuperBot FileWatch Thread").start();

            while (true) {}
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void register(Sys sys) {
        PROVIDERS.put(sys.getName(), sys);
    }

    public static void load(Consumer<String> callback) {
        if (callback == null)
            callback = s -> {};
        callback.accept("0/6");
        loadProgress();
        callback.accept("1/6");
        loadGroups();
        callback.accept("2/6");
        loadPermissions();
        callback.accept("3/6");
        loadHangmanWords();
        callback.accept("4/6");
        SuperBotCommands.loadCommands();
        callback.accept("5/6");
        SuperBotProfiles.loadProfiles();
        callback.accept("6/6");
    }

    public static void loadPermissions() {
        SuperBotPermissions.clear();
        File permsFolder = new File("permissions");
        if (!permsFolder.exists())
            permsFolder.mkdirs();
        else {
            for (File f : permsFolder.listFiles(f -> f.getName().toLowerCase().equals(f.getName()))) {
                try {
                    String username = f.getName();
                    Files.readAllLines(f.toPath()).forEach(s -> SuperBotPermissions.set(username, s, true, false));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void savePermissions() {
        File permsFolder = new File("permissions");
        if (!permsFolder.exists())
            permsFolder.mkdirs();
        for (Entry<String, Set<String>> entry : SuperBotPermissions.permissions.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                File f = new File(permsFolder, entry.getKey().toLowerCase());
                if (f.exists())
                    f.delete();
                try {
                    Files.write(f.toPath(), entry.getValue(), StandardOpenOption.CREATE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void loadHangmanWords() {
        HANGMAN_PHRASES.clear();
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

    public static void loadProgress() {
        File dir = new File("superchat_data");
        if (!dir.exists())
            dir.mkdir();
        for (File file : dir.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                try {
                    BufferedReader reader = Files.newBufferedReader(file.toPath());
                    Map<String, String> map = new HashMap<>();
                    GSON.fromJson(reader, JsonObject.class).entrySet().forEach(e -> map.put(e.getKey(), e.getValue().getAsString()));
                    PROGRESS.put(file.getName().substring(0, file.getName().length() - 5), map);
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
                File file = new File(dir, entry.getKey() + ".json");
                JsonObject obj = new JsonObject();
                entry.getValue().forEach((k, v) -> obj.addProperty(k, v));
                BufferedWriter writer = Files.newBufferedWriter(file.toPath());
                GSON.toJson(obj, writer);
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
            if (cfg.getProvider() != null && cfg.getUniqueId() != null) {
                Sys provider = PROVIDERS.get(cfg.getProvider());
                if (provider != null)
                    provider.addGroupConfiguration(cfg);
            }
        }
    }

    public static GroupConfiguration getGroupConfiguration(Group group) {
        return getGroupConfiguration(group, true);
    }

    public static GroupConfiguration getGroupConfiguration(Group group, boolean create) {
        if (group.getType() != GroupType.GROUP) {
            return null;
        }
        String uniqueId = group.getUniqueId();
        Sys provider = group.getProvider();
        GroupConfiguration cfg = provider.getGroupConfiguration(uniqueId);
        if (cfg == null) {
            cfg = newGroupConfiguration();
            cfg.set(GroupConfiguration.KEY_PROVIDER, provider.getName());
            cfg.set(GroupConfiguration.KEY_UNIQUE_ID, uniqueId);
            cfg.set(GroupConfiguration.KEY_GROUP_NAME, group.getDisplayName());
            cfg.save();
            provider.addGroupConfiguration(cfg);
        } else {
            cfg.set(GroupConfiguration.KEY_GROUP_NAME, group.getDisplayName());
            cfg.save();
        }
        return cfg;
    }

    public static GroupConfiguration newGroupConfiguration() {
        File file = null, dir = new File("groups");
        int n = 0;
        do file = new File(dir, n++ + ".cfg");
        while (file.exists());
        return new GroupConfiguration(file);
    }

    public static String whichEarlier(String a, String b) {
        String[] ad = a.substring(1).split("E");
        String[] bd = b.substring(1).split("E");
        int as = Integer.parseInt(ad[0]);
        int ae = Integer.parseInt(ad[1]);
        int bs = Integer.parseInt(bd[0]);
        int be = Integer.parseInt(bd[1]);
        if (as < bs)
            return a;
        if (bs < as)
            return b;
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
        if (!SuperBotShows.EPISODE_PATTERN.matcher(ep).matches())
            return null;

        List<String> users = getProgress(show).entrySet().stream().filter(e -> e.getValue().equals(ep)).map(e -> e.getKey()).collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        for (String s : users) {
            if (sb.length() > 0)
                sb.append(", ");
            Optional<Profile> p = Profile.getProfile(s);
            sb.append(p.isPresent() ? p.get().getName() : s);
        }
        return sb.toString();
    }

    public static Map<String, String> getProgress(Show show) {
        return getProgress(show == null ? null : show.imdb);
    }

    public static Map<String, String> getProgress(String show) {
        if (show == null)
            return null;
        return PROGRESS.computeIfAbsent(show, s -> new HashMap<>());
    }

    public static Map<Show, String> getUserProgress(String username) {
        Map<Show, String> prg = new HashMap<>();
        PROGRESS.forEach((s, m) -> {
            if (m.containsKey(username.toLowerCase()))
                prg.put(SuperBotShows.getShow(s), m.get(username.toLowerCase()));
        });
        return prg;
    }

    static {
        try {
            InputStream is = SuperBotController.class.getResourceAsStream("/META-INF/MANIFEST.MF");
            Manifest mf = new Manifest(is);
            VERSION = mf.getMainAttributes().getValue("MavenVersion");
            BUILD_NUMBER = Integer.parseInt(mf.getMainAttributes().getValue("JenkinsBuild"));
            URL changesUrl = new URL("http://ci.nickr.xyz/job/SuperBot/" + BUILD_NUMBER + "/api/json?pretty=true&tree=changeSet[items[id,msg,author[id]]]");
            BufferedReader changesReader = new BufferedReader(new InputStreamReader(changesUrl.openStream()));
            JsonObject obj = GSON.fromJson(changesReader, JsonObject.class);
            JsonArray details = obj.getAsJsonObject("changeSet").getAsJsonArray("items");
            int detailsLen = details.size();
            GIT_COMMIT_IDS = new String[detailsLen];
            GIT_COMMIT_MESSAGES = new String[detailsLen];
            GIT_COMMIT_AUTHORS = new String[detailsLen];
            for (int i = 0; i < detailsLen; i++) {
                GIT_COMMIT_IDS[i] = GIT_COMMIT_MESSAGES[i] = GIT_COMMIT_AUTHORS[i] = "Unknown";
                try {
                    GIT_COMMIT_IDS[i] = details.get(i).getAsJsonObject().get("id").getAsString().trim();
                } catch (Exception ex) {}
                try {
                    GIT_COMMIT_MESSAGES[i] = details.get(i).getAsJsonObject().get("msg").getAsString().trim();
                } catch (Exception ex) {}
                try {
                    GIT_COMMIT_AUTHORS[i] = details.get(i).getAsJsonObject().getAsJsonObject("author").get("id").getAsString().trim();
                } catch (Exception ex) {}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
