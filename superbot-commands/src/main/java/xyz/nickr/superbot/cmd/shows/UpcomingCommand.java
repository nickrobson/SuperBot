package xyz.nickr.superbot.cmd.shows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.TreeMap;

import xyz.nickr.superbot.SuperBotShows;
import xyz.nickr.superbot.SuperBotShows.Show;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class UpcomingCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"upcoming"};
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[] {"", "gets upcoming episodes of shows"};
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        LocalDateTime now = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));
        now = now.minus(1, ChronoUnit.DAYS);
        LocalDateTime week = now.plus(8, ChronoUnit.DAYS);

        Map<LocalDateTime, String> days = new TreeMap<>();
        for (Show show : SuperBotShows.getShows()) {
            LocalDateTime date = show.getDate();
            if (date != null && !date.isBefore(now) && !date.isAfter(week)) {
                days.merge(date, show.getDisplay(), (a, b) -> a + "\n" + b);
            }
        }

        MessageBuilder mb = sys.message().bold(true).escaped("Upcoming episodes of shows:").bold(false);
        for (Map.Entry<LocalDateTime, String> entry : days.entrySet()) {
            mb.newLine().bold(true).escaped(Show.getDateString(entry.getKey())).bold(false);
            for (String show : entry.getValue().split("\\n")) {
                mb.newLine().escaped("   - " + show);
            }
        }
        group.sendMessage(mb);
    }

}
