package xyz.nickr.superbot.sys.telegram;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.inline.InlineCallbackQuery;
import pro.zackpollard.telegrambot.api.chat.inline.InlineReplyMarkup;
import pro.zackpollard.telegrambot.api.chat.inline.send.InlineQueryResponse;
import pro.zackpollard.telegrambot.api.chat.inline.send.content.InputMessageContent;
import pro.zackpollard.telegrambot.api.chat.inline.send.content.InputTextMessageContent;
import pro.zackpollard.telegrambot.api.chat.inline.send.results.InlineQueryResult;
import pro.zackpollard.telegrambot.api.chat.inline.send.results.InlineQueryResultArticle;
import pro.zackpollard.telegrambot.api.chat.inline.send.results.InlineQueryResultPhoto;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.ParticipantJoinGroupChatEvent;
import pro.zackpollard.telegrambot.api.event.chat.inline.InlineCallbackQueryReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.inline.InlineQueryReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.inline.InlineResultChosenEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.keyboards.InlineKeyboardButton;
import pro.zackpollard.telegrambot.api.keyboards.InlineKeyboardMarkup;
import pro.zackpollard.telegrambot.api.keyboards.InlineKeyboardMarkup.InlineKeyboardMarkupBuilder;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardHide;
import xyz.nickr.superbot.ConsecutiveId;
import xyz.nickr.superbot.Joiner;
import xyz.nickr.superbot.SuperBotCommands;
import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.keyboard.ButtonResponse;
import xyz.nickr.superbot.keyboard.Keyboard;
import xyz.nickr.superbot.keyboard.KeyboardButton;
import xyz.nickr.superbot.keyboard.KeyboardRow;
import xyz.nickr.superbot.sys.Conversable;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupConfiguration;
import xyz.nickr.superbot.sys.GroupType;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;
import xyz.nickr.superbot.web.StandardEndpoints;

/**
 * Created by bo0tzz
 */
public class TelegramListener implements Listener {

    public static final Pattern PATTERN_HEXCOLOUR_FREE = Pattern.compile("(?:[A-F0-9]{2,3})|(?:[A-F0-9]{5,6})");

    private static final String KEYBOARD_ID_NAMESPACE = "SuperBot::InlineKeyboard";
    private static final String RESULT_ID_NAMESPACE = "SuperBot::InlineResult";

    private final TelegramBot bot;
    private final TelegramSys sys;
    private final TelegramInlineSys inlineSys;
    private final Map<String, Keyboard> keyboards;
    private final Map<String, DummyMessage> dummies;

    public TelegramListener(TelegramBot bot, TelegramSys sys) {
        this.bot = bot;
        this.sys = sys;
        this.inlineSys = new TelegramInlineSys(sys);
        this.keyboards = new HashMap<>();
        this.dummies = new HashMap<>();
    }

    @Override
    public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
        if (event.getCommand().equals("fixkb")) {
            event.getChat().sendMessage(SendableTextMessage.builder().message("hi").replyMarkup(ReplyKeyboardHide.builder().build()).build());
        }
        Group g = this.sys.wrap(event.getChat());
        User u = this.sys.wrap(event.getMessage().getSender());
        String msg = this.sys.prefix() + event.getCommand().trim() + " " + event.getArgsString().trim();
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
            MessageBuilder message = this.sys.message().bold(true).escaped(TelegramMessageBuilder.markdown_escape(welcome, false) + "\n" + TelegramMessageBuilder.markdown_escape(help, false)).bold(false);
            convo.sendMessage(message);
        }
    }

    @Override
    public void onInlineCallbackQueryReceivedEvent(InlineCallbackQueryReceivedEvent event) {
        InlineCallbackQuery q = event.getCallbackQuery();
        String callback = q.getData();
        boolean answer = false;
        if (callback.contains("-")) {
            String[] spl = callback.split("-", 2);
            Keyboard kb = this.keyboards.get(spl[0]);
            if (kb != null) {
                KeyboardButton btn = kb.getButton(spl[1]);
                if (btn != null) {
                    ButtonResponse res = btn.onClick(this.sys.wrap(q.getFrom()));
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

    private static InlineQueryResultArticle res(String title, String desc, String text, boolean web) {
        return res(title, desc, text, web, null);
    }

    private static InlineQueryResultArticle res(String title, String desc, String text, boolean web, InlineReplyMarkup mkup) {
        InputMessageContent imc = InputTextMessageContent.builder().parseMode(ParseMode.MARKDOWN).disableWebPagePreview(!web).messageText(text).build();
        return InlineQueryResultArticle.builder().title(title).description(desc).inputMessageContent(imc).replyMarkup(mkup).build();
    }

    private static InlineQueryResultArticle res(String id, String title, String desc, String text, boolean web, InlineReplyMarkup mkup) {
        InputMessageContent imc = InputTextMessageContent.builder().parseMode(ParseMode.MARKDOWN).disableWebPagePreview(!web).messageText(text).build();
        return InlineQueryResultArticle.builder().id(id).title(title).description(desc).inputMessageContent(imc).replyMarkup(mkup).build();
    }

    @Override
    public void onInlineQueryReceived(InlineQueryReceivedEvent event) {
        String q = event.getQuery().getQuery().trim();
        // System.out.println("INLINE QUERY RECEIVED: " + q);
        // System.out.println("FROM: " + (event.getQuery().getSender().getUsername() != null ?
        // event.getQuery().getSender().getUsername() :
        // String.valueOf(event.getQuery().getSender().getId())));
        String[] words = q.split("\\s+");
        List<InlineQueryResult> results = new LinkedList<>();
        boolean is_personal = false;
        int cache_time = 0;
        if (words.length >= 1) {
            if (words[0].startsWith("#")) {
                String colour = words[0].substring(1).toUpperCase();
                List<String> colours = new LinkedList<>();
                if (PATTERN_HEXCOLOUR_FREE.matcher(colour).matches()) {
                    String hex = "0123456789ABCDEF";
                    if (colour.length() == 2) {
                        for (int i = 0; i < hex.length(); i++) {
                            String co = colour + hex.charAt(i);
                            String c = "";
                            for (int x = 0; x < 6; x++) {
                                c += co.charAt(x / 2);
                            }
                            colours.add(c);
                        }
                    } else if (colour.length() == 5) {
                        for (int i = 0; i < hex.length(); i++) {
                            colours.add(colour + hex.charAt(i));
                        }
                    } else if (colour.length() == 3 || colour.length() == 6) {
                        String c = colour.length() == 3 ? "" : colour;
                        if (colour.length() == 3) {
                            for (int i = 0; i < 6; i++) {
                                c += colour.charAt(i / 2);
                            }
                        }
                        colours.add(c);
                    }
                }
                for (String co : colours) {
                    URL url;
                    try {
                        url = new URL("http://nickr.xyz/photo/" + co + ".jpg");
                        results.add(InlineQueryResultPhoto.builder().title("#" + co).caption("").thumbUrl(url).photoUrl(url).photoWidth(StandardEndpoints.PHOTO_WIDTH).photoHeight(StandardEndpoints.PHOTO_HEIGHT).build());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                cache_time = 30;
            } else if (words[0].equalsIgnoreCase("distance")) {
                if (words.length >= 3) {
                    String from = words[1], to = words[2];
                    try {
                        JSONObject res = Unirest.get(String.format("http://www.distance24.org/route.json?stops=%s", URLEncoder.encode(from + "|" + to, "UTF-8"))).asJson().getBody().getObject();
                        JSONArray arr = res.getJSONArray("stops");
                        boolean fromValid = true, toValid = true;
                        if (arr.getJSONObject(0).getString("type").equalsIgnoreCase("Invalid")) {
                            fromValid = false;
                        } else {
                            from = arr.getJSONObject(0).getString("city") + (arr.getJSONObject(0).has("region") ? ", " + arr.getJSONObject(0).getString("region") : "");
                        }
                        if (arr.getJSONObject(1).getString("type").equalsIgnoreCase("Invalid")) {
                            toValid = false;
                        } else {
                            to = arr.getJSONObject(1).getString("city") + ", " + (arr.getJSONObject(1).has("region") ? ", " + arr.getJSONObject(1).getString("region") : "");
                        }
                        if (fromValid && toValid) {
                            double dist = res.getDouble("distance");
                            String text = "\\[Distance] " + " " + from + " -> " + to + " = " + dist + "km";
                            results.add(res(from + " => " + to, "Distance: " + dist + "km", text, false));
                        } else {
                            String disp = "";
                            if (!fromValid) {
                                disp += from;
                            }
                            if (!toValid) {
                                disp += (disp.isEmpty() ? "" : ", ") + to;
                            }
                            results.add(res("Invalid city name(s): " + disp, "Try another query", "\\[Distance] Invalid city name(s): " + disp, false));
                        }
                    } catch (IOException | UnirestException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                String cmd = "/" + Joiner.join(" ", words);
                List<DummyMessage> msgs = new LinkedList<>();
                DummyUser user = new DummyUser(event, results, msgs);
                SuperBotCommands.exec(this.inlineSys, new DummyGroup(user), user, new DummyMessage(this.sys.wrap(event.getQuery().getSender()), cmd, null, null));
                for (DummyMessage dm : msgs) {
                    this.dummies.put(dm.inlineId, dm);
                }
            }
        }
        if (results.isEmpty()) {
            String un = this.bot.getBotUsername();
            results.add(res("Flip", "@" + un + " flip [text]", "@" + un.replace("_", "\\_") + " flip \\[text]", false));
            results.add(res("Colour", "@" + un + " #[colour]", "@" + un.replace("_", "\\_") + " #\\[colour]", false));
            results.add(res("Distance", "@" + un + " distance [from] [to]", "@" + un.replace("_", "\\_") + " distance \\[from] \\[to]", false));
            results.add(res("Maths", "@" + un + " maths [query]", "/math@" + un.replace("_", "\\_"), false));
            results.add(res("Convert", "@" + un + " convert [from] [to] [quantity]", "/convert@" + un.replace("_", "\\_"), false));
            results.add(res("Currency", "@" + un + " currency [from] [to] [quantity]", "/currency@" + un.replace("_", "\\_"), false));
        }
        InlineQueryResponse res = InlineQueryResponse.builder().is_personal(is_personal).results(results).cache_time(cache_time).build();
        event.getQuery().answer(this.bot, res);
    }

    @Override
    public void onInlineResultChosen(InlineResultChosenEvent event) {
        DummyMessage dm = this.dummies.get(event.getChosenResult().getResultId());
        if (dm != null) {
            dm.messageId = event.getChosenResult().getInlineMessageId();
        }
    }

    public class DummyGroup implements Group {

        private DummyUser dummy;

        public DummyGroup(DummyUser dummy) {
            this.dummy = dummy;
        }

        @Override
        public Sys getProvider() {
            return TelegramListener.this.inlineSys;
        }

        @Override
        public String getUniqueId() {
            return this.dummy.getUniqueId();
        }

        @Override
        public Message sendMessage(MessageBuilder message) {
            return this.dummy.sendMessage(message);
        }

        @Override
        public String getDisplayName() {
            return this.dummy.getDisplayName().orElse(this.dummy.getUsername());
        }

        @Override
        public GroupType getType() {
            return GroupType.GROUP;
        }

        @Override
        public boolean isAdmin(User u) {
            return false;
        }

        @Override
        public Set<User> getUsers() {
            return new HashSet<>(Arrays.asList(this.dummy));
        }

    }

    public class DummyUser implements User {

        private List<InlineQueryResult> results;
        private pro.zackpollard.telegrambot.api.user.User user;
        private List<DummyMessage> messages;

        public DummyUser(InlineQueryReceivedEvent event, List<InlineQueryResult> results, List<DummyMessage> messages) {
            this.results = results;
            this.user = event.getQuery().getSender();
            this.messages = messages;
        }

        @Override
        public Sys getProvider() {
            return TelegramListener.this.inlineSys;
        }

        @Override
        public String getUniqueId() {
            return String.valueOf(this.user.getId());
        }

        @Override
        public Message sendMessage(MessageBuilder message) {
            String msg = message.build();
            Keyboard kb = message.getKeyboard();
            InlineReplyMarkup ikm = null;
            if (kb != null) {
                kb.lock();
                String prefix = ConsecutiveId.next(KEYBOARD_ID_NAMESPACE);
                InlineKeyboardMarkupBuilder reply = InlineKeyboardMarkup.builder();
                for (KeyboardRow kbr : kb) {
                    List<InlineKeyboardButton> btns = new LinkedList<>();
                    kbr.forEach(b -> {
                        btns.add(InlineKeyboardButton.builder().callbackData(prefix + "-" + b.getText()).text(b.getText()).build());
                    });
                    reply.addRow(btns);
                }
                ikm = reply.build();
                TelegramListener.this.addKeyboard(prefix, kb);
            }
            String id = ConsecutiveId.next(RESULT_ID_NAMESPACE);
            this.results.add(res(id, "Result:", msg, msg, false, ikm));
            DummyMessage m = new DummyMessage(this, msg, id, ikm);
            this.messages.add(m);
            return m;
        }

        @Override
        public String getUsername() {
            return this.user.getUsername();
        }

        @Override
        public Optional<String> getDisplayName() {
            return Optional.ofNullable(this.user.getFullName());
        }

    }

    public class DummyMessage implements Message {

        private Conversable convo;
        private String message;
        String messageId, inlineId;
        InlineReplyMarkup ikm;

        public DummyMessage(Conversable convo, String message, String inlineId, InlineReplyMarkup ikm) {
            this.convo = convo;
            this.message = message;
            this.inlineId = inlineId;
            this.ikm = ikm;
        }

        @Override
        public Sys getProvider() {
            return TelegramListener.this.sys;
        }

        @Override
        public String getUniqueId() {
            return "";
        }

        @Override
        public Conversable getConversation() {
            return this.convo;
        }

        @Override
        public User getSender() {
            return null;
        }

        @Override
        public String getMessage() {
            return this.message;
        }

        @Override
        public void edit(MessageBuilder message) {
            TelegramListener.this.sys.getBot().editInlineMessageText(this.messageId, message.build(), ParseMode.MARKDOWN, true, this.ikm);
        }

        @Override
        public boolean isEdited() {
            return false;
        }

    }

}
