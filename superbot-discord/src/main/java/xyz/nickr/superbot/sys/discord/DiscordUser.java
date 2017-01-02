package xyz.nickr.superbot.sys.discord;

import java.util.Optional;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.core.entities.User;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;

/**
 * @author Nick Robson
 */
@AllArgsConstructor
public class DiscordUser implements xyz.nickr.superbot.sys.User {

    private final DiscordSys sys;
    private final User user;

    @Override
    public String getUsername() {
        return user.getName() + "#" + user.getDiscriminator();
    }

    @Override
    public Optional<String> getDisplayName() {
        return Optional.ofNullable(user.getName());
    }

    @Override
    public Sys getProvider() {
        return sys;
    }

    @Override
    public String getUniqueId() {
        return user.getId();
    }

    @Override
    public Message sendMessage(MessageBuilder mb, boolean event) {
        if (user.hasPrivateChannel()) {
            return sys.wrap(user.getPrivateChannel()).sendMessage(mb, event);
        }
        return null;
    }
}
