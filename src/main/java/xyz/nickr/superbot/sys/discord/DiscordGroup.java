package xyz.nickr.superbot.sys.discord;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.core.entities.MessageChannel;
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

    @Override
    public Message sendMessage(MessageBuilder mb) {
        Message m = sendMessageNoShare(mb);
        if (m != null)
            share(mb);
        return m;
    }

    @Override
    public Message sendMessageNoShare(MessageBuilder mb) {
        return sys.wrap(channel.sendMessage(DiscordMessageBuilder.build(mb)));
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
