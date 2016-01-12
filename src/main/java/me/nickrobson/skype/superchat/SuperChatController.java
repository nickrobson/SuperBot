package me.nickrobson.skype.superchat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
import java.util.Scanner;
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

import in.kyle.ezskypeezlife.EzSkype;
import in.kyle.ezskypeezlife.api.SkypeConversationType;
import in.kyle.ezskypeezlife.api.captcha.SkypeCaptcha;
import in.kyle.ezskypeezlife.api.captcha.SkypeErrorHandler;
import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import me.nickrobson.skype.superchat.SuperChatShows.Show;
import me.nickrobson.skype.superchat.cmd.Command;
import me.nickrobson.skype.superchat.cmd.HelpCommand;
import me.nickrobson.skype.superchat.cmd.ReloadCommand;
import me.nickrobson.skype.superchat.cmd.StopCommand;
import me.nickrobson.skype.superchat.cmd.cfg.EditConfigCommand;
import me.nickrobson.skype.superchat.cmd.cfg.ShowConfigCommand;
import me.nickrobson.skype.superchat.cmd.fun.DefineCommand;
import me.nickrobson.skype.superchat.cmd.fun.HangmanCommand;
import me.nickrobson.skype.superchat.cmd.fun.NumberwangCommand;
import me.nickrobson.skype.superchat.cmd.fun.TypeOutCommand;
import me.nickrobson.skype.superchat.cmd.perm.AddPermCommand;
import me.nickrobson.skype.superchat.cmd.perm.DelPermCommand;
import me.nickrobson.skype.superchat.cmd.perm.ListPermsCommand;
import me.nickrobson.skype.superchat.cmd.shows.AddShowCommand;
import me.nickrobson.skype.superchat.cmd.shows.ProgressCommand;
import me.nickrobson.skype.superchat.cmd.shows.RemoveShowCommand;
import me.nickrobson.skype.superchat.cmd.shows.SetProgressCommand;
import me.nickrobson.skype.superchat.cmd.shows.ShowsCommand;
import me.nickrobson.skype.superchat.cmd.shows.TimetableCommand;
import me.nickrobson.skype.superchat.cmd.shows.ViewingOrderCommand;
import me.nickrobson.skype.superchat.cmd.shows.WhoCommand;
import me.nickrobson.skype.superchat.cmd.shows.WipeCommand;
import me.nickrobson.skype.superchat.cmd.util.ConvertCommand;
import me.nickrobson.skype.superchat.cmd.util.CurrencyCommand;
import me.nickrobson.skype.superchat.cmd.util.GidCommand;
import me.nickrobson.skype.superchat.cmd.util.GitCommand;
import me.nickrobson.skype.superchat.cmd.util.JenkinsCommand;
import me.nickrobson.skype.superchat.cmd.util.VersionCommand;

/**
 * @author Nick Robson
 */
public class SuperChatController implements SkypeErrorHandler {

    private static final Map<String, GroupConfiguration> GCONFIGS               = new HashMap<>();

    public static final Map<String, Command>             COMMANDS               = new HashMap<>();
    public static final Map<String, Map<String, String>> PROGRESS               = new TreeMap<>();

    public static final List<String>                     HANGMAN_PHRASES        = new LinkedList<>();

    public static final String                           COMMAND_PREFIX         = "+";

    public static final String                           WELCOME_MESSAGE        = "Welcome to %s";
    public static final String                           WELCOME_MESSAGE_JOIN   = "Welcome, %s, to %s";

    public static final Gson                             GSON                   = new GsonBuilder().setPrettyPrinting().create();

    public static boolean                                HELP_IGNORE_WHITESPACE = false;
    public static boolean                                HELP_WELCOME_CENTRED   = true;

    public static String                                 VERSION                = "Unknown";
    public static int                                    BUILD_NUMBER           = 0;
    public static String[]                               GIT_COMMIT_IDS         = new String[] { "Unknown" };
    public static String[]                               GIT_COMMIT_MESSAGES    = new String[] { "Unknown" };
    public static String[]                               GIT_COMMIT_AUTHORS     = new String[] { "Unknown" };

    public static EzSkype skype;

    public static void main(String[] args) {
        try {
            File config = new File("config.cfg");

            Map<String, String> properties = new HashMap<>();

            try {
                BufferedReader reader = Files.newBufferedReader(config.toPath());
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("=")) {
                        String[] data = line.split("=", 2);
                        properties.put(data[0], data[1]);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            SuperChatShows.setup();

            load(null);

            HELP_IGNORE_WHITESPACE = properties.getOrDefault("help.whitespace", "false").equalsIgnoreCase("true");
            HELP_WELCOME_CENTRED = properties.getOrDefault("help.welcome.centred", "false").equalsIgnoreCase("true");

            skype = new EzSkype(properties.get("username"), properties.get("password"));
            skype.setErrorHandler(new SuperChatController());
            skype.login();
            skype.getEventManager().registerEvents(new SuperChatListener());

            new Thread(() -> {
                try {
                    Thread.sleep(1000 * 60 * 60 * 2); // 2 hours
                } catch (Exception ex) {}
                saveProgress();
                System.exit(0);
            }, "SuperChat Sleepy Thread").start();

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
            }, "SuperChat FileWatch Thread").start();;

            while (true) {}
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void load(Consumer<String> callback) {
        if (callback == null)
            callback = s -> {};
        callback.accept("0/5");
        loadProgress();
        callback.accept("1/5");
        loadGroups();
        callback.accept("2/5");
        loadPermissions();
        callback.accept("3/5");
        loadHangmanWords();
        callback.accept("4/5");
        loadCommands();
        callback.accept("5/5");
    }

    public static void loadPermissions() {
        SuperChatPermissions.clear();
        File permsFolder = new File("permissions");
        if (!permsFolder.exists())
            permsFolder.mkdirs();
        else {
            for (File f : permsFolder.listFiles(f -> f.getName().toLowerCase().equals(f.getName()))) {
                try {
                    String username = f.getName();
                    Files.readAllLines(f.toPath()).forEach(s -> SuperChatPermissions.set(username, s, true, false));
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
        for (Entry<String, Set<String>> entry : SuperChatPermissions.permissions.entrySet()) {
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

    public static void register(Command cmd) {
        for (String name : cmd.names())
            COMMANDS.put(name, cmd);
        cmd.init();
    }

    public static void loadCommands() {
        COMMANDS.clear();

        register(new HelpCommand());
        register(new ReloadCommand());
        register(new StopCommand());

        register(new EditConfigCommand());
        register(new ShowConfigCommand());

        register(new AddPermCommand());
        register(new DelPermCommand());
        register(new ListPermsCommand());

        register(new AddShowCommand());
        register(new ProgressCommand());
        register(new RemoveShowCommand());
        register(new SetProgressCommand());
        register(new ShowsCommand());
        register(new TimetableCommand());
        register(new ViewingOrderCommand());
        register(new WhoCommand());
        register(new WipeCommand());

        register(new HangmanCommand());
        register(new NumberwangCommand());
        register(new TypeOutCommand());
        register(new DefineCommand());

        register(new ConvertCommand());
        register(new CurrencyCommand());
        register(new GidCommand());
        register(new GitCommand());
        register(new JenkinsCommand());
        register(new VersionCommand());
    }

    public static void loadProgress() {
        File dir = new File("superchat_data");
        if (!dir.exists())
            dir.mkdir();
        for (File file : dir.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".mrv")) {
                try {
                    Map<String, String> map = new HashMap<>();
                    BufferedReader reader = Files.newBufferedReader(file.toPath());
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] data = line.split("=", 2);
                        map.put(data[0], data[1]);
                    }
                    PROGRESS.put(file.getName().substring(0, file.getName().length() - 4), map);
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
                File file = new File(dir, entry.getKey() + ".mrv");
                BufferedWriter writer = Files.newBufferedWriter(file.toPath());
                for (Entry<String, String> e : entry.getValue().entrySet()) {
                    writer.write(e.getKey() + "=" + e.getValue());
                    writer.newLine();
                }
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
        GCONFIGS.clear();
        for (File f : dir.listFiles()) {
            GroupConfiguration cfg = new GroupConfiguration(f);
            if (cfg.getGroupId() != null) {
                GCONFIGS.put(cfg.getGroupId(), cfg);
            }
        }
    }

    public static GroupConfiguration getGroupConfiguration(SkypeConversation group) {
        return getGroupConfiguration(group, true);
    }

    public static GroupConfiguration getGroupConfiguration(SkypeConversation group, boolean create) {
        if (group.getConversationType() == SkypeConversationType.USER)
            return null;
        String longId = group.getLongId();
        GroupConfiguration cfg = GCONFIGS.get(longId);
        if (cfg == null) {
            cfg = newGroupConfiguration();
            cfg.set(GroupConfiguration.KEY_GROUP_ID, longId);
            cfg.save();
            GCONFIGS.put(longId, cfg);
        }
        return cfg;
    }

    public static GroupConfiguration newGroupConfiguration() {
        File file = null, dir = new File("groups");
        int n = 0;
        while (file == null || file.exists())
            file = new File(dir, n + ".cfg");
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
        if (!SuperChatShows.EPISODE_PATTERN.matcher(ep).matches())
            return null;

        List<String> users = getProgress(show).entrySet().stream().filter(e -> e.getValue().equals(ep)).map(e -> e.getKey()).collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        for (String s : users) {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(s);
        }
        return sb.toString();
    }

    public static Map<String, String> getProgress(Show show) {
        return getProgress(show == null ? null : show.getMainName());
    }

    public static Map<String, String> getProgress(String show) {
        if (show == null)
            return null;
        return PROGRESS.computeIfAbsent(show, s -> new HashMap<>());
    }

    public static Map<Show, String> getUserProgress(String username) {
        Map<Show, String> prg = new HashMap<>();
        PROGRESS.forEach((s, m) -> {
            if (m.containsKey(username))
                prg.put(SuperChatShows.getShow(s), m.get(username));
        });
        return prg;
    }

    @Override
    public String setNewPassword() {
        System.out.println("You need to set a new password!!");
        return null;
    }

    @Override
    public String solve(SkypeCaptcha captcha) {
        System.out.println("Enter captcha ( " + captcha.getUrl() + " )");
        try (Scanner sc = new Scanner(System.in)) {
            return sc.nextLine();
        } catch (Exception ex) {
            return null;
        }
    }

    static {
        try {
            InputStream is = SuperChatController.class.getResourceAsStream("/META-INF/MANIFEST.MF");
            Manifest mf = new Manifest(is);
            VERSION = mf.getMainAttributes().getValue("MavenVersion");
            BUILD_NUMBER = Integer.parseInt(mf.getMainAttributes().getValue("JenkinsBuild"));
            URL changesUrl = new URL("http://ci.nickr.xyz/job/SuperChat/" + BUILD_NUMBER + "/api/json?pretty=true&tree=changeSet[items[id,msg,author[id]]]");
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
