package xyz.nickr.superbot.sys.telegram;

import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.User;

/**
 * Created by bo0tzz
 */
public class TelegramMessageBuilder implements MessageBuilder<TelegramMessageBuilder> {

    public static String markdown_escape(String text, boolean code) {
        if (!code) {
            text = text.replace("*", "\\*"); // * is replaced with \*
            text = text.replace("[", "\\["); // [ is replaced with \[
        }
        return text;
    }

    private final StringBuilder msg;

    private boolean             bold          = false;
    private boolean             italic        = false;
    private boolean             code          = false;

    public TelegramMessageBuilder() {
        this("");
    }

    public TelegramMessageBuilder(String initial) {
        msg = new StringBuilder(initial);
    }

    @Override
    public int length() {
        return msg.length();
    }

    @Override
    public String toString() {
        return build();
    }

    @Override
    public String build() {
        italic(false).bold(false).code(false);
        return msg.toString();
    }

    @Override
    public TelegramMessageBuilder newLine() {
        msg.append("\n");
        return this;
    }

    @Override
    public TelegramMessageBuilder name(User user) {
        return escaped(user.name());
    }

    @Override
    public TelegramMessageBuilder escaped(String text) {
        msg.append(markdown_escape(text, code));
        return this;
    }

    @Override
    public TelegramMessageBuilder raw(String text) {
        msg.append(text);
        return this;
    }

    @Override
    public TelegramMessageBuilder link(String url, String text) {
        msg.append("[" + text + "](" + url + ")");
        return this;
    }

    @Override
    public TelegramMessageBuilder bold(boolean on) {
        if (bold != on) {
            bold = on;
            msg.append(on ? "*" : "*");
        }
        return this;
    }

    @Override
    public TelegramMessageBuilder italic(boolean on) {
        if (italic != on) {
            italic = on;
            msg.append(on ? "_" : "_");
        }
        return this;
    }

    @Override
    public TelegramMessageBuilder underline(boolean on) {
        return this;
    }

    @Override
    public TelegramMessageBuilder strikethrough(boolean on) {
        return this;
    }

    @Override
    public TelegramMessageBuilder code(boolean on) {
        if (code != on) {
            code = on;
            msg.append(on ? "`" : "`");
        }
        return this;
    }

    @Override
    public TelegramMessageBuilder blink(boolean on) {
        return this;
    }

    @Override
    public TelegramMessageBuilder size(int s) {
        return this;
    }
}
