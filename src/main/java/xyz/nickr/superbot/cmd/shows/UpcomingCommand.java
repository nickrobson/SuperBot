package xyz.nickr.superbot.cmd.shows;

import java.util.Calendar;
import java.util.Map;
import java.util.Map.Entry;
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
        Calendar today = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        now.clear();
        now.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE));
        now.add(Calendar.DATE, -2);
        Calendar week = Calendar.getInstance();
        week.clear();
        week.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE));
        week.add(Calendar.DATE, 8);

        Map<Calendar, String> days = new TreeMap<>();
        for (Show show : SuperBotShows.getShows()) {
            Calendar date = show.getDate();
            if (date != null && !date.before(now) && !date.after(week)) {
                days.merge(date, show.display, (a, b) -> a + "\n" + b);
            }
        }

        MessageBuilder mb = sys.message().bold(true).escaped("Upcoming episodes of shows:").bold(false);
        for (Entry<Calendar, String> entry : days.entrySet()) {
            mb.newLine().bold(true).escaped(Show.getDateString(entry.getKey())).bold(false);
            for (String show : entry.getValue().split("\\n")) {
                mb.newLine().escaped("   - " + show);
            }
        }
        group.sendMessage(mb);
    }

}
