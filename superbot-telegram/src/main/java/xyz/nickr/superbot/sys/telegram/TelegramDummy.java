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

    public static class DummyGroup implements Group {

        private TelegramListener listener;
        private DummyUser user;

        public DummyGroup(TelegramListener listener, DummyUser user) {
            this.listener = listener;
            this.user = user;
        }

        @Override
        public Sys getProvider() {
            return this.listener.inlineSys;
        }

        @Override
        public String getUniqueId() {
            return this.user.getUniqueId();
        }

        @Override
        public Message sendMessage(MessageBuilder message, boolean event) {
            return this.user.sendMessage(message);
        }

        @Override
        public boolean supportsMultiplePhotos() {
            return true;
        }

        @Override
        public void sendPhoto(URL url, boolean event) {
            this.user.sendPhoto(url, event);
        }

        @Override
        public String getDisplayName() {
            return this.user.getDisplayName().orElse(this.user.getUsername());
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
        public void sendPhoto(URL url, boolean event) {
            this.results.add(InlineQueryResultPhoto.builder().thumbUrl(url).photoUrl(url).build());
        }

        @Override
        public Message sendMessage(MessageBuilder message, boolean event) {
            String msg = TelegramMessageBuilder.build(message);
            Keyboard kb = message.getKeyboard();
            InlineReplyMarkup ikm = null;
            if (kb != null) {
                kb.lock();
                String prefix = ConsecutiveId.next(TelegramInlineSys.KEYBOARD_ID_NAMESPACE);
                ikm = TelegramInlineSys.toTGKeyboard(prefix, kb);
                this.listener.addInlineKeyboard(prefix, kb);
            }
            String id = ConsecutiveId.next(TelegramInlineSys.RESULT_ID_NAMESPACE);
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
                String prefix = ConsecutiveId.next(TelegramInlineSys.KEYBOARD_ID_NAMESPACE);
                this.ikm = TelegramInlineSys.toTGKeyboard(prefix, kb);
                this.listener.addInlineKeyboard(prefix, kb);
            }
            this.listener.getBot().editInlineMessageText(this.messageId, TelegramMessageBuilder.build(message), ParseMode.MARKDOWN, true, this.ikm);
        }

        @Override
        public boolean isEdited() {
            return false;
        }

    }

}
