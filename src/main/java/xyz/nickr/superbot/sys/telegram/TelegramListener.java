package xyz.nickr.superbot.sys.telegram;

import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.ParticipantJoinGroupChatEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.nickr.superbot.SuperBotCommands;
import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupConfiguration;
import xyz.nickr.superbot.sys.User;

/**
 * Created by bo0tzz
 */
public class TelegramListener implements Listener {
    private final TelegramBot bot;
    private final TelegramSys sys;

    public TelegramListener(TelegramBot bot, TelegramSys sys) {
        this.bot = bot;
        this.sys = sys;
    }

    @Override
    public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
        Group g = sys.wrap(event.getChat());
        User u = sys.wrap(event.getMessage().getSender());
        String msg = SuperBotCommands.COMMAND_PREFIX + event.getCommand().trim() + " " + event.getArgsString().trim();
        SuperBotCommands.exec(sys, g, u, sys.wrap(msg, event.getMessage()));
    }

    @Override
    public void onParticipantJoinGroupChat(ParticipantJoinGroupChatEvent event) {
        User user = sys.wrap(event.getParticipant());
        Group convo = sys.wrap(event.getChat());
        GroupConfiguration cfg = SuperBotController.getGroupConfiguration(convo);
        if (cfg != null && cfg.isShowJoinMessage()) {
            String welcome = String.format(SuperBotController.WELCOME_MESSAGE_JOIN, user.getUsername(), convo.getDisplayName());
            String help = "You can access my help menu by typing `" + SuperBotCommands.COMMAND_PREFIX + "help`";
            String message = sys.message()
                    .bold(true)
                    .text(TelegramMessageBuilder.markdown_escape(welcome) + "\n" + TelegramMessageBuilder.markdown_escape(help))
                    .bold(false)
                    .build();
            convo.sendMessage(message);
        }
    }
}
