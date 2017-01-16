package xyz.nickr.superbot.sys.telegram;

import lombok.Setter;
import pro.zackpollard.telegrambot.api.chat.inline.InlineReplyMarkup;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import xyz.nickr.superbot.ConsecutiveId;
import xyz.nickr.superbot.sys.Conversable;
import xyz.nickr.superbot.sys.Keyboard;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

/**
 * Created by bo0tzz
 */
public class TelegramMessage implements Message {

    private final TelegramSys sys;
    private String strmsg;
    private pro.zackpollard.telegrambot.api.chat.message.Message message;
    @Setter private InlineReplyMarkup replyMarkup;
    private String kbPrefix;

    public TelegramMessage(TelegramSys sys, String strmsg, pro.zackpollard.telegrambot.api.chat.message.Message message) {
        this.sys = sys;
        this.message = message;
        this.strmsg = strmsg;
    }

    @Override
    public Sys getProvider() {
        return this.sys;
    }

    @Override
    public String getUniqueId() {
        return String.valueOf(this.message.getMessageId());
    }

    @Override
    public Conversable getConversation() {
        return this.sys.wrap(this.message.getChat());
    }

    @Override
    public User getSender() {
        return this.sys.wrap(this.message.getSender());
    }

    @Override
    public String getMessage() {
        return this.strmsg;
    }

    @Override
    public void edit(MessageBuilder message) {
        String newText = TelegramMessageBuilder.build(message);
        Keyboard kb = message.getKeyboard();
        if (kb != null) {
            kb.lock();
            kbPrefix = ConsecutiveId.next(TelegramInlineSys.KEYBOARD_ID_NAMESPACE);
            replyMarkup = TelegramInlineSys.toTGKeyboard(kbPrefix, kb);
            sys.listener.addInlineKeyboard(kbPrefix, kb);
        }
        pro.zackpollard.telegrambot.api.chat.message.Message n = this.sys.getBot().editMessageText(this.message, newText, ParseMode.MARKDOWN, !message.isPreview(), replyMarkup);
        if (n != null) {
            this.strmsg = newText;
            this.message = n;
        }
    }

    @Override
    public boolean isEdited() {
        return false;
    }
}
