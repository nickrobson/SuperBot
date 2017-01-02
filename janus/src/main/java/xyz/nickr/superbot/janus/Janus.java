package xyz.nickr.superbot.janus;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import xyz.nickr.superbot.SuperBotCommands;
import xyz.nickr.superbot.SuperBotResource;
import xyz.nickr.superbot.event.EventManager;
import xyz.nickr.superbot.event.Listener;
import xyz.nickr.superbot.sys.Conversable;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Profile;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;
import xyz.nickr.superbot.sys.discord.DiscordSys;
import xyz.nickr.superbot.sys.gitter.GitterSys;
import xyz.nickr.superbot.sys.skype.SkypeSys;
import xyz.nickr.superbot.sys.telegram.TelegramSys;

/**
 * @author Nick Robson
 */
public class Janus {

    public static void main(String[] args) {
        try {
            File config = new File("config.cfg");

            Properties properties = new Properties();
            properties.load(new FileInputStream(config));

            SuperBotResource.registerProvider(new SkypeSys(properties.getProperty("skype.username"), properties.getProperty("skype.password")));
            SuperBotResource.registerProvider(new TelegramSys(properties.getProperty("telegram.api")));
            SuperBotResource.registerProvider(new GitterSys(properties.getProperty("gitter.api")));
            SuperBotResource.registerProvider(new DiscordSys(properties.getProperty("discord.api")));

            init();

            SuperBotCommands.register(new LinkCommand());

            EventManager.register(new Listener() {

                @Override
                public void onMessage(Sys sys, Group group, User user, Message message) {
                    propagate(sys, group, user, message);
                }

                @Override
                public void onSend(Conversable conversable, MessageBuilder message) {
                    if (conversable instanceof Group) {
                        share((Group) conversable, message);
                    }
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static final Map<String, Set<Map.Entry<String, String>>> linkedChats = new HashMap<>();

    public static Set<String> getLinks(Group g) {
        return linkedChats.entrySet().stream().filter(e -> e.getValue().contains(new AbstractMap.SimpleEntry<>(g.getProvider().getName(), g.getUniqueId()))).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    public static Set<Map.Entry<String, String>> getLinkedGroups(Group g) {
        Set<Map.Entry<String, String>> set = getLinks(g).stream().map(linkedChats::get).flatMap(Set::stream).collect(Collectors.toSet());
        set.remove(new AbstractMap.SimpleEntry<>(g.getProvider().getName(), g.getUniqueId()));
        return set;
    }

    public static void share(Group g, MessageBuilder m) {
        new Thread(() -> {
            try {
                Set<Map.Entry<String, String>> linkedGroups = getLinkedGroups(g);
                for (Map.Entry<String, String> linkedGroup : linkedGroups) {
                    try {
                        Sys sys = SuperBotResource.PROVIDERS.get(linkedGroup.getKey());
                        if (sys != null) {
                            Group o = sys.getGroup(linkedGroup.getValue());
                            if (o != null) {
                                o.sendMessage(sys.message().raw(m), false);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, "Sharing is Caring Thread").start();
    }

    public static void propagate(Sys sys, Group group, User user, Message message) {
        try {
            Optional<Profile> profile = user.getProfile();
            MessageBuilder m = sys.message();
            m.bold(x -> x.escaped(profile.map(Profile::getName).orElse(user.getUsername())));
            m.italic(x -> x.escaped(": (%s%s)", sys.getName(), profile.isPresent() ? " profile" : ""));
            m.newLine().escaped(message.getMessage());
            share(group, m);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void init() {
        try {
            try (FileReader fis = new FileReader("linked_chats.json")) {
                JsonObject json = SuperBotResource.GSON.fromJson(fis, JsonObject.class);

                JsonObject links = json.getAsJsonObject("links");

                links.entrySet().forEach(e -> {
                    String linkName = e.getKey();
                    JsonObject linkData = e.getValue().getAsJsonObject();
                    JsonObject chats = linkData.getAsJsonObject("chats");

                    Set<Map.Entry<String, String>> set = new HashSet<>();

                    chats.entrySet().forEach(entry -> {
                        String provider = entry.getKey();
                        JsonArray chatIds = entry.getValue().getAsJsonArray();
                        chatIds.forEach(x -> set.add(new AbstractMap.SimpleEntry<String, String>(provider, x.getAsString())));
                    });

                    linkedChats.put(linkName, set);
                });

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        JsonObject json = new JsonObject();
        JsonObject links = new JsonObject();

        linkedChats.forEach((linkName, set) -> {
            JsonObject linkData = new JsonObject();
            JsonObject chats = new JsonObject();

            Map<String, JsonArray> arrs = new HashMap<>();
            set.forEach(e -> {
                JsonArray arr = arrs.getOrDefault(e.getKey(), new JsonArray());
                arr.add(e.getValue());
                arrs.put(e.getKey(), arr);
            });

            arrs.forEach((s, a) -> chats.add(s, a));

            linkData.add("chats", chats);
            links.add(linkName, linkData);
        });

        json.add("links", links);

        File file = new File("linked_chats.json.tmp");
        file.delete();
        try (FileWriter writer = new FileWriter(file)) {
            SuperBotResource.GSON.toJson(json, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File newFile = new File("linked_chats.json");
        newFile.delete();
        file.renameTo(newFile);
    }

}
