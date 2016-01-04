package me.nickrobson.skype.superchat.cmd.shows;

import java.util.LinkedList;
import java.util.List;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.MessageBuilder;
import me.nickrobson.skype.superchat.SuperChatShows;
import me.nickrobson.skype.superchat.SuperChatShows.Show;
import me.nickrobson.skype.superchat.cmd.Command;

public class ShowsCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "shows" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[] { "", "see which shows are being tracked" };
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        List<String> send = new LinkedList<>();
        for (Show show : SuperChatShows.TRACKED_SHOWS) {
            StringBuilder sb = new StringBuilder();
            for (String s : show.getNames()) {
                if (sb.length() > 0)
                    sb.append(", ");
                sb.append(s);
            }
            if (sb.length() > 0)
                send.add(code(encode("[[ " + show.getDisplay() + " ]]: ")) + code(encode(sb.toString())));
        }
        send.sort(String.CASE_INSENSITIVE_ORDER);
        int rows = (send.size() / 2) + (send.size() % 2);
        int maxLen1 = send.subList(0, rows).stream().max((s1, s2) -> s1.length() - s2.length()).orElse("").length();
        MessageBuilder builder = new MessageBuilder();
        for (int i = 0; i < rows; i++) {
            String spaces = "";
            for (int j = send.get(i).length(); j < maxLen1; j++)
                spaces += ' ';
            builder.html(send.get(i) + code(spaces));
            if (send.size() > rows + i) {
                builder.html(encode("    ") + send.get(rows + i));
            }
            if (i != rows - 1)
                builder.newLine();
        }
        group.sendMessage(builder.build());
    }

}
