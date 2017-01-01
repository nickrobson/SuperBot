package xyz.nickr.superbot.sys.discord;

import lombok.AllArgsConstructor;
import xyz.nickr.superbot.sys.Conversable;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

/**
 * @author Nick Robson
 */
@AllArgsConstructor
public class DiscordMessage implements Message {

    private final DiscordSys sys;
    private final net.dv8tion.jda.core.entities.Message handle;

    @Override
    public Sys getProvider() {
        return sys;
    }

    @Override
    public String getUniqueId() {
        return handle.getId();
    }

    @Override
    public Conversable getConversation() {
        return sys.wrap(handle.getChannel());
    }

    @Override
    public User getSender() {
        return sys.wrap(handle.getAuthor());
    }

    @Override
    public String getMessage() {
        return handle.getContent();
    }

    @Override
    public void edit(MessageBuilder message) {
        handle.editMessage(DiscordMessageBuilder.build(message)).queue();
    }

    @Override
    public boolean isEdited() {
        return handle.isEdited();
    }

}
