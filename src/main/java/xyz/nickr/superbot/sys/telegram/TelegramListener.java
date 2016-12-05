package xyz.nickr.superbot.sys.telegram;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.inline.InlineCallbackQuery;
import pro.zackpollard.telegrambot.api.chat.inline.InlineReplyMarkup;
import pro.zackpollard.telegrambot.api.chat.inline.send.InlineQueryResponse;
import pro.zackpollard.telegrambot.api.chat.inline.send.content.InputMessageContent;
import pro.zackpollard.telegrambot.api.chat.inline.send.content.InputTextMessageContent;
import pro.zackpollard.telegrambot.api.chat.inline.send.results.InlineQueryResult;
import pro.zackpollard.telegrambot.api.chat.inline.send.results.InlineQueryResultArticle;
import pro.zackpollard.telegrambot.api.chat.message.content.TextContent;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.ParticipantJoinGroupChatEvent;
import pro.zackpollard.telegrambot.api.event.chat.inline.InlineCallbackQueryReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.inline.InlineQueryReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.inline.InlineResultChosenEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.MessageEditReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.MessageEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.MessageReceivedEvent;
import xyz.nickr.superbot.Joiner;
import xyz.nickr.superbot.SuperBotCommands;
import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.cmd.LinkCommand;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupConfiguration;
import xyz.nickr.superbot.sys.Keyboard;
import xyz.nickr.superbot.sys.KeyboardButton;
import xyz.nickr.superbot.sys.KeyboardButtonResponse;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.User;
import xyz.nickr.superbot.sys.telegram.TelegramDummy.DummyGroup;
import xyz.nickr.superbot.sys.telegram.TelegramDummy.DummyMessage;
import xyz.nickr.superbot.sys.telegram.TelegramDummy.DummyUser;

/**
 * Created by bo0tzz
 */
public class TelegramListener implements Listener {

    private final TelegramBot bot;
    private final TelegramSys sys;
    final TelegramInlineSys inlineSys;
    private final Map<String, Keyboard> keyboards;
    private final Map<String, DummyMessage> dummies;

    public TelegramListener(TelegramBot bot, TelegramSys sys) {
        this.bot = bot;
        this.sys = sys;
        this.inlineSys = new TelegramInlineSys(sys);
        this.keyboards = new HashMap<>();
        this.dummies = new HashMap<>();
    }

    public TelegramBot getBot() {
        return this.sys.getBot();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        cmd(event);
    }

    @Override
    public void onMessageEditReceived(MessageEditReceivedEvent event) {
        cmd(event);
    }

    private synchronized void cmd(MessageEvent event) {
        if (!(event.getMessage().getContent() instanceof TextContent))
            return;
        Group g = this.sys.wrap(event.getChat());
        User u = this.sys.wrap(event.getMessage().getSender());
        String content = ((TextContent) event.getMessage().getContent()).getContent();
        LinkCommand.propagate(this.sys, g, u, this.sys.wrap(this.sys.message().escaped(content), event.getMessage()));

        String command = content.substring(1).split(" ")[0].split("@")[0];

        int argsStart = content.indexOf(" ");
        String args = "";

        if (argsStart != -1) {
            args = content.substring(argsStart).trim();
        }

        String msg = this.sys.prefix() + command.trim() + " " + args.trim();
        SuperBotCommands.exec(this.sys, g, u, this.sys.wrap(this.sys.message().escaped(msg), event.getMessage()));
    }

    @Override
    public void onParticipantJoinGroupChat(ParticipantJoinGroupChatEvent event) {
        User user = this.sys.wrap(event.getParticipant());
        Group convo = this.sys.wrap(event.getChat());
        GroupConfiguration cfg = SuperBotController.getGroupConfiguration(convo);
        if (cfg != null && cfg.isShowJoinMessage()) {
            String welcome = String.format(SuperBotController.WELCOME_MESSAGE_JOIN, user.getUsername(), convo.getDisplayName());
            String help = "You can access my help menu by typing `" + this.sys.prefix() + "help`";
            MessageBuilder message = this.sys.message().bold(true).escaped(welcome).newLine().escaped(help).bold(false);
            convo.sendMessage(message);
        }
    }

    @Override
    public void onInlineCallbackQueryReceivedEvent(InlineCallbackQueryReceivedEvent event) {
        InlineCallbackQuery q = event.getCallbackQuery();
        String callback = q.getData();
        boolean answer = false;
        if (callback.contains("-")) {
            String[] spl = callback.split("-");
            Keyboard kb = this.keyboards.get(spl[0]);
            if (kb != null) {
                KeyboardButton btn = kb.getRows().get(Integer.parseInt(spl[1])).getButtons().get(Integer.parseInt(spl[2]));
                if (btn != null) {
                    KeyboardButtonResponse res = btn.onClick(this.sys.wrap(q.getFrom()));
                    if (res != null) {
                        q.answer(res.getText(), res.isShowAlert());
                        answer = true;
                    }
                }
            }
        }
        if (!answer) {
            q.answer("", false);
        }
    }

    public void addKeyboard(String messageId, Keyboard kb) {
        this.keyboards.put(messageId, kb);
    }

    static InlineQueryResultArticle res(String title, String desc, String text, boolean web) {
        return res(title, desc, text, web, null);
    }

    static InlineQueryResultArticle res(String title, String desc, String text, boolean web, InlineReplyMarkup mkup) {
        InputMessageContent imc = InputTextMessageContent.builder().parseMode(ParseMode.MARKDOWN).disableWebPagePreview(!web).messageText(text).build();
        return InlineQueryResultArticle.builder().title(title).description(desc).inputMessageContent(imc).replyMarkup(mkup).build();
    }

    static InlineQueryResultArticle res(String id, String title, String desc, String text, boolean web, InlineReplyMarkup mkup) {
        InputMessageContent imc = InputTextMessageContent.builder().parseMode(ParseMode.MARKDOWN).disableWebPagePreview(!web).messageText(text).build();
        return InlineQueryResultArticle.builder().id(id).title(title).description(desc).inputMessageContent(imc).replyMarkup(mkup).build();
    }

    @Override
    public void onInlineQueryReceived(InlineQueryReceivedEvent event) {
        String q = event.getQuery().getQuery().trim();
        String[] words = q.split("\\s+");
        List<InlineQueryResult> results = new LinkedList<>();
        boolean is_personal = false;
        int cache_time = 0;
        if (words.length >= 1) {
            String cmd = "/" + Joiner.join(" ", words);
            List<DummyMessage> msgs = new LinkedList<>();
            DummyUser user = new DummyUser(this, event, results, msgs);
            SuperBotCommands.exec(this.inlineSys, new DummyGroup(this, user), user, new DummyMessage(this, this.sys.wrap(event.getQuery().getSender()), cmd, null, null));
            for (DummyMessage dm : msgs) {
                this.dummies.put(dm.inlineId, dm);
            }
        }
        if (results.isEmpty()) {
            String un = this.bot.getBotUsername();
            for (Command cmd : SuperBotCommands.CMDS) {
                String name = cmd.names()[0];
                User u = this.sys.wrap(event.getQuery().getSender());
                if (cmd.perm() == Command.DEFAULT_PERMISSION) {
                    String[] usage = cmd.help(u, false);
                    String txt = "@" + un + " " + name + " " + usage[0];
                    MessageBuilder mb = this.sys.message().escaped(txt);
                    results.add(res(name + " - " + usage[1], txt, TelegramMessageBuilder.build(mb), false));
                }
            }
        }
        InlineQueryResponse res = InlineQueryResponse.builder().isPersonal(is_personal).results(results).cacheTime(cache_time).build();
        event.getQuery().answer(this.bot, res);
    }

    @Override
    public void onInlineResultChosen(InlineResultChosenEvent event) {
        DummyMessage dm = this.dummies.get(event.getChosenResult().getResultId());
        if (dm != null) {
            dm.messageId = event.getChosenResult().getInlineMessageId();
        }
    }

}
