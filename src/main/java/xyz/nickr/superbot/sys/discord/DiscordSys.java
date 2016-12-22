package xyz.nickr.superbot.sys.discord;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.requests.RestAction;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Sys;

/**
 * @author Nick Robson
 */
public class DiscordSys extends Sys {

    JDA jda;

    public DiscordSys(String botToken) {
        new Thread(() -> {
            long now = System.currentTimeMillis();
            System.out.println("Loading SuperBot: Discord");
            try {
                try {
                    this.jda = new JDABuilder(AccountType.BOT)
                            .setToken(botToken)
                            .setEnableShutdownHook(true)
                            .setGame(Game.of("SuperBot"))
                            .setAudioEnabled(false)
                            .setAutoReconnect(true)
                            .addListener(new DiscordListener(this))
                            .buildBlocking();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Done SuperBot: Discord (" + (System.currentTimeMillis() - now) + "ms)");
        }).start();
    }

    @Override
    public String getName() {
        return "Discord";
    }

    @Override
    public String prefix() {
        return "+";
    }

    @Override
    public boolean isUIDCaseSensitive() {
        return true;
    }

    @Override
    public String getUserFriendlyName(String uniqueId) {
        return jda.getUserById(uniqueId).getName();
    }

    @Override
    public Group getGroup(String uniqueId) {
        return wrap(jda.getTextChannelById(uniqueId));
    }

    public DiscordGroup wrap(MessageChannel channel) {
        return channel != null ? new DiscordGroup(this, channel) : null;
    }

    public xyz.nickr.superbot.sys.User wrap(User user) {
        return user != null ? new DiscordUser(this, user) : null;
    }

    public xyz.nickr.superbot.sys.Message wrap(Message message) {
        return new DiscordMessage(this, message);
    }

    public xyz.nickr.superbot.sys.Message wrap(RestAction<Message> message) {
        try {
            return wrap(message.block());
        } catch (RateLimitedException e) {
            e.printStackTrace();
            return null;
        }
    }

}
