package xyz.nickr.superbot.cmd.shows;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import xyz.nickr.superbot.Joiner;
import xyz.nickr.superbot.SuperBotShows;
import xyz.nickr.superbot.SuperBotShows.Show;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class TimetableCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "timetable", "days" };
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] { "(search)", "see what days each show is on" };
    }

    String get(String day, Set<Show> set, Sys sys) {
        MessageBuilder<?> mb = sys.message();
        if (mb.length() > 0)
            mb.newLine();
        List<String> names = set.stream().map(s -> s.display).collect(Collectors.toList());
        names.sort(String.CASE_INSENSITIVE_ORDER);
        return mb.bold(true).escaped(day + ": ").bold(false).escaped(Joiner.join(", ", names)).build();
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        Map<String, Set<Show>> days = new HashMap<>();
        SuperBotShows.TRACKED_SHOWS.forEach(s -> {
            Set<Show> set = days.get(s.day == null || s.day.isEmpty() || s.day.equals("N/A") ? "not airing" : s.day.toLowerCase());
            if (set == null)
                set = new HashSet<>();
            set.add(s);
            days.put(s.day == null || s.day.isEmpty() || s.day.equals("N/A") ? "not airing" : s.day.toLowerCase(), set);
        });
        List<String> lines = new LinkedList<>();
        for (String day : Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Netflix", "Not airing")) {
            Set<Show> set = days.get(day.toLowerCase());
            if (set != null) {
                lines.add(get(day, set, sys));
            }
        }
        MessageBuilder<?> builder = sys.message();
        if (args.length > 0)
            lines.removeIf(s -> !s.toLowerCase().contains(args[0].toLowerCase()));
        lines.forEach(builder::raw);
        if (builder.length() > 0) {
            group.sendMessage(builder.build());
        } else {
            group.sendMessage(builder.escaped("No matching shows."));
        }
    }

}
