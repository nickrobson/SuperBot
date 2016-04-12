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
        List<String> names = set.stream().map(s -> s.getDisplay()).collect(Collectors.toList());
        names.sort(String.CASE_INSENSITIVE_ORDER);
        return mb.bold(true).escaped(day + ": ").bold(false).escaped(Joiner.join(", ", names)).build();
    }

    <T> Set<T> merge(Set<T> a, Set<T> b) {
        Set<T> m = new HashSet<>(a);
        m.addAll(b);
        return m;
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        Map<String, Set<Show>> days = new HashMap<>();
        SuperBotShows.TRACKED_SHOWS.forEach(s -> {
            String day = s.getDay();
            if (day == null || day.isEmpty() || day.equals("N/A"))
                day = "Not airing";
            days.merge(day, new HashSet<>(Arrays.asList(s)), this::merge);
        });
        List<String> alldays = new LinkedList<>(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday", "Netflix", "Not airing"));
        days.keySet().stream().filter(s -> !alldays.contains(s)).forEach(alldays::add);
        List<String> lines = new LinkedList<>();
        for (String day : alldays) {
            Set<Show> set = days.get(day);
            if (set != null) {
                lines.add(get(day, set, sys));
            }
        }
        if (args.length > 0)
            lines.removeIf(s -> !s.toLowerCase().contains(Joiner.join(" ", args).toLowerCase()));
        MessageBuilder<?> builder = sys.message();
        builder.bold(true).escaped(lines.isEmpty() ? "No matching shows or days." : "Shows by day:").bold(false);
        lines.forEach(l -> builder.newLine().raw(l));
        group.sendMessage(builder);
    }

}
