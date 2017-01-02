package xyz.nickr.superbot.janus;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import xyz.nickr.superbot.SuperBotResource;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.cmd.Permission;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

/**
 * @author Nick Robson
 */
public class LinkCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "cmd", "ln" };
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
    public boolean alwaysEnabled() {
        return true;
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendUsage(sys, user, group);
            return;
        }
        Map<String, Set<Map.Entry<String, String>>> linkedChats = Janus.linkedChats;
        MessageBuilder mb = sys.message();
        if (args[0].equalsIgnoreCase("list")) {
            if (linkedChats.isEmpty()) {
                group.sendMessage(mb.escaped("No janus networks"));
            } else {
                mb.escaped("Janus networks:");
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
                group.sendMessage(mb.escaped("There is already a janus network called '%s'", linkName));
            } else {
                linkedChats.put(linkName, new HashSet<>());
                Janus.save();
                group.sendMessage(mb.escaped("Success! New janus network created: '%s'", linkName));
            }
        } else if (args[0].equalsIgnoreCase("delete")) {
            if (args.length < 2) {
                sendUsage(sys, user, group);
                return;
            }
            String linkName = args[1].toLowerCase();
            if (!linkedChats.containsKey(linkName)) {
                group.sendMessage(mb.escaped("There is no janus network called '%s'", linkName));
            } else {
                linkedChats.remove(linkName);
                Janus.save();
                group.sendMessage(mb.escaped("Success! Janus network deleted: '%s'", linkName));
            }
        } else if (args[0].equalsIgnoreCase("join")) {
            if (args.length < 2) {
                sendUsage(sys, user, group);
                return;
            }
            String linkName = args[1].toLowerCase();
            if (!linkedChats.containsKey(linkName)) {
                group.sendMessage(mb.escaped("There is no janus network called '%s'", linkName));
            } else {
                Map.Entry<String, String> me = new AbstractMap.SimpleEntry<>(sys.getName(), group.getUniqueId());
                Set<Map.Entry<String, String>> set = linkedChats.get(linkName);
                if (set.add(me)) {
                    group.sendMessage(mb.escaped("Success! Added this chat to the janus network '%s'!", linkName));
                } else {
                    group.sendMessage(mb.escaped("This chat is already in the janus network '%s'!", linkName));
                }
                linkedChats.put(linkName, set);
                Janus.save();
            }
        } else if (args[0].equalsIgnoreCase("leave")) {
            if (args.length < 2) {
                sendUsage(sys, user, group);
                return;
            }
            String linkName = args[1].toLowerCase();
            if (!linkedChats.containsKey(linkName)) {
                group.sendMessage(mb.escaped("There is no janus network called '%s'", linkName));
            } else {
                Map.Entry<String, String> me = new AbstractMap.SimpleEntry<>(sys.getName(), group.getUniqueId());
                Set<Map.Entry<String, String>> set = linkedChats.get(linkName);
                if (set.remove(me)) {
                    group.sendMessage(mb.escaped("Success! Removed this chat from the janus network '%s'!", linkName));
                } else {
                    group.sendMessage(mb.escaped("This chat isn't in the janus network '%s'!", linkName));
                }
                linkedChats.put(linkName, set);
                Janus.save();
            }
        } else if (args[0].equalsIgnoreCase("show")) {
            if (args.length < 2) {
                sendUsage(sys, user, group);
                return;
            }
            String linkName = args[1].toLowerCase();
            if (!linkedChats.containsKey(linkName)) {
                group.sendMessage(mb.escaped("There is no janus network called '%s'", linkName));
            } else {
                Set<Map.Entry<String, String>> set = linkedChats.get(linkName);
                mb.escaped("Showing janus network data for '%s':", linkName);
                Map<String, List<Map.Entry<String, String>>> providers = set.stream().collect(Collectors.groupingBy(Map.Entry::getKey));
                for (Map.Entry<String, List<Map.Entry<String, String>>> entry : providers.entrySet()) {
                    Sys s = SuperBotResource.PROVIDERS.get(entry.getKey());
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
