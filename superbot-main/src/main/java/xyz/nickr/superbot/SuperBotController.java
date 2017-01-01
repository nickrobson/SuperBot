package xyz.nickr.superbot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.jar.Manifest;
import xyz.nickr.jomdb.JavaOMDB;
import xyz.nickr.superbot.cmd.RegisterCommands;
import xyz.nickr.superbot.cmd.util.PasteFetchCommand;
import xyz.nickr.superbot.sys.GroupConfiguration;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.discord.DiscordSys;
import xyz.nickr.superbot.sys.gitter.GitterSys;
import xyz.nickr.superbot.sys.skype.SkypeSys;
import xyz.nickr.superbot.sys.telegram.TelegramSys;

/**
 * @author Nick Robson
 */
public class SuperBotController {

    public static void main(String[] args) {
        try {
            File config = new File("config.cfg");

            Properties properties = new Properties();
            properties.load(new FileInputStream(config));

            SuperBotResource.registerProvider(new SkypeSys(properties.getProperty("skype.username"), properties.getProperty("skype.password")));
            SuperBotResource.registerProvider(new TelegramSys(properties.getProperty("telegram.api")));
            SuperBotResource.registerProvider(new GitterSys(properties.getProperty("gitter.api")));
            SuperBotResource.registerProvider(new DiscordSys(properties.getProperty("discord.api")));

            PasteFetchCommand.VILSOL_PASTE_TOKEN = properties.getProperty("vilsol.paste.token");
            Imgur.IMGUR_CLIENT_ID = properties.getProperty("imgur.clientid");

            SuperBotShows.setup();

            new Thread(() -> SuperBotShows.getShows().forEach(show -> {
                System.out.println("Fetched day for: " + show.getDisplay() + " (" + show.getDay() + ")");
            })).start();

            SuperBotResource.load(null);
            RegisterCommands.register();

            SuperBotResource.PROVIDERS.forEach((s, sys) -> sys.onLoaded());

            new Thread(() -> {
                try {
                    Thread.sleep(1000 * 60 * 60 * 2); // 2 hours
                } catch (Exception ex) {}
                SuperBotResource.saveProgress();
                System.exit(0);
            }, "SuperBot Sleepy Thread").start();

            new Thread(() -> {
                while (true) {
                    File file = new File(".jenkins-built");
                    if (file.exists()) {
                        file.delete();
                        SuperBotResource.saveProgress();
                        System.exit(0);
                    }
                    try {
                        Thread.sleep(10_000); // 10 seconds
                    } catch (Exception ex) {}
                }
            }, "SuperBot FileWatch Thread").start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    SuperBotResource.HTTP.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "SuperBot Shutdown Thread"));

            while (true) {
                Thread.sleep(100_000);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static {
        try {
            InputStream is = SuperBotController.class.getResourceAsStream("/META-INF/MANIFEST.MF");
            Manifest mf = new Manifest(is);
            SuperBotResource.VERSION = mf.getMainAttributes().getValue("MavenVersion");
            SuperBotResource.BUILD_NUMBER = Integer.parseInt(mf.getMainAttributes().getValue("JenkinsBuild"));
            URL changesUrl = new URL("http://ci.nickr.xyz/job/SuperBot/" + SuperBotResource.BUILD_NUMBER + "/api/json?pretty=true&tree=changeSet[items[id,msg,author[id]]]");
            BufferedReader changesReader = new BufferedReader(new InputStreamReader(changesUrl.openStream()));
            JsonObject obj = SuperBotResource.GSON.fromJson(changesReader, JsonObject.class);
            JsonArray details = obj.getAsJsonObject("changeSet").getAsJsonArray("items");
            int detailsLen = details.size();
            SuperBotResource.GIT_COMMIT_IDS = new String[detailsLen];
            SuperBotResource.GIT_COMMIT_MESSAGES = new String[detailsLen];
            SuperBotResource.GIT_COMMIT_AUTHORS = new String[detailsLen];
            for (int i = 0; i < detailsLen; i++) {
                SuperBotResource.GIT_COMMIT_IDS[i] = SuperBotResource.GIT_COMMIT_MESSAGES[i] = SuperBotResource.GIT_COMMIT_AUTHORS[i] = "Unknown";
                try {
                    SuperBotResource.GIT_COMMIT_IDS[i] = details.get(i).getAsJsonObject().get("id").getAsString().trim();
                } catch (Exception ex) {}
                try {
                    SuperBotResource.GIT_COMMIT_MESSAGES[i] = details.get(i).getAsJsonObject().get("msg").getAsString().trim();
                } catch (Exception ex) {}
                try {
                    SuperBotResource.GIT_COMMIT_AUTHORS[i] = details.get(i).getAsJsonObject().getAsJsonObject("author").get("id").getAsString().trim();
                } catch (Exception ex) {}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
