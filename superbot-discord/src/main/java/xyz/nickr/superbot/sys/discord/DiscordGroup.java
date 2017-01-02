package xyz.nickr.superbot.sys.discord;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.core.entities.MessageChannel;
import xyz.nickr.superbot.event.EventManager;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupType;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;

/**
 * @author Nick Robson
 */
@AllArgsConstructor
public class DiscordGroup implements Group {

    private final DiscordSys sys;
    private final MessageChannel channel;

    @Override
    public Sys getProvider() {
        return sys;
    }

    @Override
    public String getUniqueId() {
        return channel.getId();
    }

    int countCodeBlocks(String s) {
        return s.split("```").length;
    }

    Message _send(String m) {
        return sys.wrap(channel.sendMessage(m));
    }

    @Override
    public Message sendMessage(MessageBuilder mb, boolean event) {
        String message = DiscordMessageBuilder.build(mb);
        String[] lines = message.split("\\r?\\n");
        String currentLine = "";
        Message m = null;
        int i = 0;
        while (i < lines.length) {
            String nextLine = currentLine + "\n" + lines[i];
            if (nextLine.length() >= 1995) {
                boolean needsCodeBlock = (countCodeBlocks(nextLine) & 1) == 0;
                m = _send(currentLine + (needsCodeBlock ? "```" : ""));
                currentLine = (needsCodeBlock ? "```" : "") + lines[i];
            } else {
                currentLine = nextLine;
            }
            i++;
        }
        if (!currentLine.isEmpty()) {
            m = _send(currentLine);
        }
        if (event) {
            EventManager.onSend(this, mb);
        }
        return m;
    }

    @Override
    public String getDisplayName() {
        switch (channel.getType()) {
            case TEXT:
                return "#" + channel.getName();
            default:
                return channel.getName();
        }
    }

    @Override
    public GroupType getType() {
        switch (channel.getType()) {
            case TEXT:
            case VOICE:
            case GROUP:
                return GroupType.GROUP;
            case PRIVATE:
                return GroupType.GROUP;
            default:
                return GroupType.USER;
        }
    }

}
