package xyz.nickr.superbot.cmd.link;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.cmd.Permission;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Profile;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

/**
 * @author Nick Robson
 */
public class LinkCommand implements Command {

    private static final Map<String, Set<Map.Entry<String, String>>> linkedChats = new HashMap<>();

    public static Set<String> getLinks(Group g) {
        return linkedChats.entrySet().stream().filter(e -> e.getValue().contains(new AbstractMap.SimpleEntry<>(g.getProvider().getName(), g.getUniqueId()))).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    public static Set<Map.Entry<String, String>> getLinkedGroups(Group g) {
        Set<Map.Entry<String, String>> set =  getLinks(g).stream().map(linkedChats::get).flatMap(Set::stream).collect(Collectors.toSet());
        set.remove(new AbstractMap.SimpleEntry<>(g.getProvider().getName(), g.getUniqueId()));
        return set;
    }

    public static void propagate(Sys sys, Group group, User user, Message message) {
        try {
            Set<Map.Entry<String, String>> linkedGroups = getLinkedGroups(group);
            Optional<Profile> profile = user.getProfile();
            String msg = String.format("Message from %s user %s%s:", sys.getName(), profile.map(Profile::getName).orElse(user.getUsername()), profile.isPresent() ? " (profile)" : "");
            for (Map.Entry<String, String> linkedGroup : linkedGroups) {
                try {
                    Sys s = SuperBotController.PROVIDERS.get(linkedGroup.getKey());
                    if (s != null) {
                        Group o = s.getGroup(linkedGroup.getValue());
                        if (o != null) {
                            MessageBuilder m = s.message();
                            m.italic(x -> x.escaped(msg));
                            m.newLine().escaped(message.getMessage());
                            o.sendMessage(m);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void init() {
        try {
            File linkedChatsJson = new File("linked_chats.json");
            InputStreamReader fis = new InputStreamReader(new FileInputStream(linkedChatsJson));
            JsonObject json = SuperBotController.GSON.fromJson(fis, JsonObject.class);

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

            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
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

        try {
            File linkedChatsJson = new File("linked_chats.json");
            FileWriter writer = new FileWriter(linkedChatsJson);
            SuperBotController.GSON.toJson(json, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] names() {
        return new String[] { "link", "ln" };
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[] { "[list/create/delete/join/leave/show]", "links chats together" };
    }

    @Override
    public Permission perm() {
        return string("links.manage");
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendUsage(sys, user, group);
            return;
        }
        MessageBuilder mb = sys.message();
        if (args[0].equalsIgnoreCase("list")) {
            if (linkedChats.isEmpty()) {
                group.sendMessage(mb.escaped("No link networks"));
            } else {
                mb.escaped("Link networks:");
                Map.Entry<String, String> me = new AbstractMap.SimpleEntry<>(sys.getName(), group.getUniqueId());
                linkedChats.forEach((linkName, set) -> {
                    mb.newLine().escaped("- %s (%d members)", linkName, set.size()).escaped(set.contains(me) ? " *" : "");
                });
                group.sendMessage(mb);
            }
        } else if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 2) {
                sendUsage(sys, user, group);
                return;
            }
            String linkName = args[1].toLowerCase();
            if (linkedChats.containsKey(linkName)) {
                group.sendMessage(mb.escaped("There is already a link network called '%s'", linkName));
            } else {
                linkedChats.put(linkName, new HashSet<>());
                save();
                group.sendMessage(mb.escaped("Success! New link network created: '%s'", linkName));
            }
        } else if (args[0].equalsIgnoreCase("delete")) {
            if (args.length < 2) {
                sendUsage(sys, user, group);
                return;
            }
            String linkName = args[1].toLowerCase();
            if (!linkedChats.containsKey(linkName)) {
                group.sendMessage(mb.escaped("There is no link network called '%s'", linkName));
            } else {
                linkedChats.remove(linkName);
                save();
                group.sendMessage(mb.escaped("Success! Link network deleted: '%s'", linkName));
            }
        } else if (args[0].equalsIgnoreCase("join")) {
            if (args.length < 2) {
                sendUsage(sys, user, group);
                return;
            }
            String linkName = args[1].toLowerCase();
            if (!linkedChats.containsKey(linkName)) {
                group.sendMessage(mb.escaped("There is no link network called '%s'", linkName));
            } else {
                Map.Entry<String, String> me = new AbstractMap.SimpleEntry<>(sys.getName(), group.getUniqueId());
                Set<Map.Entry<String, String>> set = linkedChats.get(linkName);
                if (set.add(me)) {
                    group.sendMessage(mb.escaped("Success! Added this chat to the link network '%s'!", linkName));
                } else {
                    group.sendMessage(mb.escaped("This chat is already in the link network '%s'!", linkName));
                }
                linkedChats.put(linkName, set);
                save();
            }
        } else if (args[0].equalsIgnoreCase("leave")) {
            if (args.length < 2) {
                sendUsage(sys, user, group);
                return;
            }
            String linkName = args[1].toLowerCase();
            if (!linkedChats.containsKey(linkName)) {
                group.sendMessage(mb.escaped("There is no link network called '%s'", linkName));
            } else {
                Map.Entry<String, String> me = new AbstractMap.SimpleEntry<>(sys.getName(), group.getUniqueId());
                Set<Map.Entry<String, String>> set = linkedChats.get(linkName);
                if (set.remove(me)) {
                    group.sendMessage(mb.escaped("Success! Removed this chat from the link network '%s'!", linkName));
                } else {
                    group.sendMessage(mb.escaped("This chat isn't in the link network '%s'!", linkName));
                }
                linkedChats.put(linkName, set);
                save();
            }
        } else if (args[0].equalsIgnoreCase("show")) {
            if (args.length < 2) {
                sendUsage(sys, user, group);
                return;
            }
            String linkName = args[1].toLowerCase();
            if (!linkedChats.containsKey(linkName)) {
                group.sendMessage(mb.escaped("There is no link network called '%s'", linkName));
            } else {
                Set<Map.Entry<String, String>> set = linkedChats.get(linkName);
                mb.escaped("Showing link network data for '%s':", linkName);
                Map<String, List<Map.Entry<String, String>>> providers = set.stream().collect(Collectors.groupingBy(Map.Entry::getKey));
                for (Map.Entry<String, List<Map.Entry<String, String>>> entry : providers.entrySet()) {
                    Sys s = SuperBotController.PROVIDERS.get(entry.getKey());
                    if (s != null) {
                        mb.newLine().escaped("  Chats from %s:", entry.getKey());
                        for (Map.Entry<String, String> e : entry.getValue()) {
                            mb.newLine().escaped("    " + s.getUserFriendlyName(e.getValue()));
                        }
                    }
                }
            }
        } else {
            sendUsage(sys, user, group);
        }
    }

}
