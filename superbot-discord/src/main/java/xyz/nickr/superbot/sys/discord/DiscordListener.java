package xyz.nickr.superbot.sys.discord;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import xyz.nickr.superbot.SuperBotCommands;
import xyz.nickr.superbot.event.EventManager;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.User;

/**
 * @author Nick Robson
 */
@AllArgsConstructor
public class DiscordListener extends ListenerAdapter {

    private final DiscordSys sys;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        cmd(event.getMessage());
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        this.cmd(event.getMessage());
    }

    public synchronized void cmd(net.dv8tion.jda.core.entities.Message message) {
        net.dv8tion.jda.core.entities.User user = message.getAuthor();
        MessageChannel channel = message.getChannel();

        Group g = this.sys.wrap(channel);
        User u = this.sys.wrap(user);

        Message m = this.sys.wrap(message);

        if (!user.isBot()) {
            EventManager.onMessage(this.sys, g, u, m);
            SuperBotCommands.exec(this.sys, g, u, m);
        }
    }

}
