package me.nickrobson.skype.superchat.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.SuperChatController;
import me.nickrobson.skype.superchat.SuperChatShows.Show;

public class WhoCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "who", "whois" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[] { "(username)", "gets (username)'s progress on all shows" };
    }

    String pad(String s, int len) {
        StringBuilder builder = new StringBuilder(s);
        while (builder.length() < len)
            builder.insert(builder.indexOf("("), ' ');
        return builder.toString();
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        String username = args.length > 0 ? args[0].toLowerCase() : user.getUsername();
        List<String> shows = new ArrayList<>();
        Map<Show, String> progress = SuperChatController.getUserProgress(username);
        progress.forEach((show, ep) -> {
            if (show != null)
                shows.add(show.getDisplay() + "    (" + ep + ")");
        });
        int rows = (shows.size() / 2) + (shows.size() % 2);
        shows.sort(String.CASE_INSENSITIVE_ORDER);
        int maxLen1 = shows.subList(0, rows).stream().max((s1, s2) -> s1.length() - s2.length()).orElse("").length();
        int maxLen2 = shows.subList(rows, shows.size()).stream().max((s1, s2) -> s1.length() - s2.length()).orElse("").length();
        String s = "";
        for (int i = 0; i < rows; i++) {
            if (shows.size() > i) {
                String t = pad(shows.get(i), maxLen1);
                if (shows.size() > rows + i)
                    t += "    |    " + pad(shows.get(rows + i), maxLen2);
                s += encode(t);
                if (i != rows - 1)
                    s += "\n   ";
            }
        }
        if (shows.size() > 0)
            group.sendMessage(bold(encode("Shows " + username + " is watching:")) + "\n" + code("   " + s));
        else
            group.sendMessage(bold(encode("Error: ")) + encode("It doesn't look like " + username + " uses me. :("));
    }

}
