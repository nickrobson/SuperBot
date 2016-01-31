package xyz.nickr.superbot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import xyz.nickr.superbot.sys.Profile;

public class SuperBotProfiles {

    public static void loadProfiles() {
        File dir = new File("profiles");
        if (!dir.exists())
            dir.mkdirs();
        else if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                new Profile(file).register();
            }
        }
    }

    public static void saveProfiles() {
        Profile.ALL.forEach((s, p) -> p.save());
    }

    public static File fileOf(String name) {
        File dir = new File("profiles");
        if (!dir.exists())
            dir.mkdirs();
        File f = new File(dir, name);
        if (!f.exists()) {
            try {
                Files.write(f.toPath(), Arrays.asList("name=" + name), StandardOpenOption.CREATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return f;
    }

}
