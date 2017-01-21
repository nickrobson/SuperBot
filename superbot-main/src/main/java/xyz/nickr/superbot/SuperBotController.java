package xyz.nickr.superbot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
import java.util.jar.Manifest;
import xyz.nickr.superbot.cmd.RegisterCommands;
import xyz.nickr.superbot.cmd.util.PasteFetchCommand;
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

}
