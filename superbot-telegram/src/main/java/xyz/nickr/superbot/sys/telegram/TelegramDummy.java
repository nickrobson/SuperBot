package xyz.nickr.superbot.sys.telegram;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import pro.zackpollard.telegrambot.api.chat.inline.InlineReplyMarkup;
import pro.zackpollard.telegrambot.api.chat.inline.send.results.InlineQueryResult;
import pro.zackpollard.telegrambot.api.chat.inline.send.results.InlineQueryResultPhoto;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.event.chat.inline.InlineQueryReceivedEvent;
import pro.zackpollard.telegrambot.api.keyboards.InlineKeyboardButton;
import pro.zackpollard.telegrambot.api.keyboards.InlineKeyboardMarkup;
import pro.zackpollard.telegrambot.api.keyboards.InlineKeyboardMarkup.InlineKeyboardMarkupBuilder;
import xyz.nickr.superbot.ConsecutiveId;
import xyz.nickr.superbot.sys.*;

public class TelegramDummy {

    private static final String KEYBOARD_ID_NAMESPACE = "SuperBot::InlineKeyboard";
    private static final String RESULT_ID_NAMESPACE = "SuperBot::InlineResult";

    public static class DummyGroup implements Group {

        private TelegramListener listener;
        private DummyUser dummy;

        public DummyGroup(TelegramListener listener, DummyUser dummy) {
            this.listener = listener;
            this.dummy = dummy;
        }

        @Override
        public Sys getProvider() {
            return this.listener.inlineSys;
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
        public Message sendMessageNoShare(MessageBuilder message) {
            return this.dummy.sendMessage(message);
        }

        @Override
        public boolean supportsMultiplePhotos() {
            return true;
        }

        @Override
        public void sendPhoto(URL url) {
            this.dummy.sendPhoto(url);
        }

        @Override
        public String getDisplayName() {
            return this.dummy.getDisplayName().orElse(this.dummy.getUsername());
        }

        @Override
        public GroupType getType() {
            return GroupType.GROUP;
        }

    }

    public static class DummyUser implements User {

        private TelegramListener listener;
        private List<InlineQueryResult> results;
        private pro.zackpollard.telegrambot.api.user.User user;
        private List<DummyMessage> messages;

        public DummyUser(TelegramListener listener, InlineQueryReceivedEvent event, List<InlineQueryResult> results, List<DummyMessage> messages) {
            this.listener = listener;
            this.results = results;
            this.user = event.getQuery().getSender();
            this.messages = messages;
        }

        @Override
        public Sys getProvider() {
            return this.listener.inlineSys;
        }

        @Override
        public String getUniqueId() {
            return String.valueOf(this.user.getId());
        }

        @Override
        public boolean supportsMultiplePhotos() {
            return true;
        }

        @Override
        public void sendPhoto(URL url) {
            this.results.add(InlineQueryResultPhoto.builder().thumbUrl(url).photoUrl(url).build());
        }

        @Override
        public Message sendMessage(MessageBuilder message) {
            String msg = TelegramMessageBuilder.build(message);
            Keyboard kb = message.getKeyboard();
            InlineReplyMarkup ikm = null;
            if (kb != null) {
                kb.lock();
                String prefix = ConsecutiveId.next(KEYBOARD_ID_NAMESPACE);
                InlineKeyboardMarkupBuilder reply = InlineKeyboardMarkup.builder();
                int row = 0;
                for (KeyboardRow kbr : kb) {
                    List<InlineKeyboardButton> btns = new LinkedList<>();
                    int rowb = 0;
                    for (KeyboardButton b : kbr) {
                        btns.add(InlineKeyboardButton.builder().callbackData(prefix + "-" + row + "-" + rowb).text(b.getText()).build());
                        rowb++;
                    }
                    reply.addRow(btns);
                    row++;
                }
                ikm = reply.build();
                this.listener.addKeyboard(prefix, kb);
            }
            String id = ConsecutiveId.next(RESULT_ID_NAMESPACE);
            this.results.add(TelegramListener.res(id, "Result:", msg, msg, false, ikm));
            DummyMessage m = new DummyMessage(this.listener, this, msg, id, ikm);
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

    public static class DummyMessage implements Message {

        private TelegramListener listener;
        private Conversable convo;
        private String message;
        String messageId, inlineId;
        InlineReplyMarkup ikm;

        public DummyMessage(TelegramListener listener, Conversable convo, String message, String inlineId, InlineReplyMarkup ikm) {
            this.listener = listener;
            this.convo = convo;
            this.message = message;
            this.inlineId = inlineId;
            this.ikm = ikm;
        }

        @Override
        public Sys getProvider() {
            return this.listener.inlineSys;
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
            Keyboard kb = message.getKeyboard();
            if (kb != null) {
                kb.lock();
                String prefix = ConsecutiveId.next(KEYBOARD_ID_NAMESPACE);
                InlineKeyboardMarkupBuilder reply = InlineKeyboardMarkup.builder();
                int row = 0;
                for (KeyboardRow kbr : kb) {
                    List<InlineKeyboardButton> btns = new LinkedList<>();
                    int rowb = 0;
                    for (KeyboardButton b : kbr) {
                        btns.add(InlineKeyboardButton.builder().callbackData(prefix + "-" + row + "-" + rowb).text(b.getText()).build());
                        rowb++;
                    }
                    reply.addRow(btns);
                    row++;
                }
                this.ikm = reply.build();
                this.listener.addKeyboard(prefix, kb);
            }
            this.listener.getBot().editInlineMessageText(this.messageId, TelegramMessageBuilder.build(message), ParseMode.MARKDOWN, true, this.ikm);
        }

        @Override
        public boolean isEdited() {
            return false;
        }

    }

}
