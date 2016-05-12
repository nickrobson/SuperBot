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
            text = text.replace("_", "\\_"); // _ is replaced with \_
            text = text.replace("[", "\\["); // [ is replaced with \[
        }
        return text;
    }

    private final StringBuilder msg;

    private boolean bold, italic, code;

    public TelegramMessageBuilder() {
        this("");
    }

    public TelegramMessageBuilder(String initial) {
        this.msg = new StringBuilder(initial);
    }

    @Override
    public int length() {
        return this.msg.length();
    }

    @Override
    public String toString() {
        return this.build();
    }

    @Override
    public String build() {
        this.italic(false).bold(false).code(false);
        return this.msg.toString();
    }

    @Override
    public TelegramMessageBuilder newLine() {
        this.msg.append("\n");
        return this;
    }

    @Override
    public TelegramMessageBuilder name(User user) {
        return this.escaped(user.name());
    }

    @Override
    public TelegramMessageBuilder escaped(String text, Object... params) {
        this.msg.append(TelegramMessageBuilder.markdown_escape(String.format(text, params), this.code));
        return this;
    }

    @Override
    public TelegramMessageBuilder raw(String text, Object... params) {
        this.msg.append(String.format(text, params));
        return this;
    }

    @Override
    public TelegramMessageBuilder link(String url, String text) {
        this.msg.append("[" + text + "](" + url + ")");
        return this;
    }

    @Override
    public TelegramMessageBuilder bold(boolean on) {
        if (this.bold != on) {
            this.bold = on;
            this.msg.append(on ? "*" : "*");
        }
        return this;
    }

    @Override
    public TelegramMessageBuilder italic(boolean on) {
        if (this.italic != on) {
            this.italic = on;
            this.msg.append(on ? "_" : "_");
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
        if (this.code != on) {
            this.code = on;
            this.msg.append(on ? "`" : "`");
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
