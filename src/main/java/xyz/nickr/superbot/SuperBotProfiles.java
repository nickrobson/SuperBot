package xyz.nickr.superbot;

import java.io.File;

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
        Profile.ALL.forEach((s, p) -> {
            p.save();
        });
    }

}
