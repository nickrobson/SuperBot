package me.nickrobson.skype.superchat.cmd;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.Joiner;
import me.nickrobson.skype.superchat.MessageBuilder;
import me.nickrobson.skype.superchat.SuperChatShows;
import me.nickrobson.skype.superchat.SuperChatShows.Show;

public class TimetableCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "timetable", "days" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[] { "", "see what days each show is on" };
    }

    void append(String day, Set<Show> set, MessageBuilder builder) {
        if (builder.length() > 0)
            builder.newLine();
        List<String> names = set.stream().map(s -> s.display).collect(Collectors.toList());
        names.sort(String.CASE_INSENSITIVE_ORDER);
        builder.bold(true).text(day + ": ").bold(false).text(Joiner.join(", ", names));
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        Map<String, Set<Show>> days = new HashMap<>();
        SuperChatShows.TRACKED_SHOWS.forEach(s -> {
            Set<Show> set = days.get(s.day == null ? "not airing" : s.day.toLowerCase());
            if (set == null)
                set = new HashSet<>();
            set.add(s);
            days.put(s.day == null ? "not airing" : s.day.toLowerCase(), set);
        });
        MessageBuilder builder = new MessageBuilder();
        // days.forEach((day, set) -> {
        for (String day : Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Netflix", "Not airing")) {
            Set<Show> set = days.get(day.toLowerCase());
            if (set != null)
                append(day, set, builder);
        }
        if (builder.length() > 0) {
            group.sendMessage(builder.build());
        } else {
            group.sendMessage(bold(encode("Something went wrong!")));
        }
    }

}
