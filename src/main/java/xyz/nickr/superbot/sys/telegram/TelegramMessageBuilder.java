package xyz.nickr.superbot.sys.telegram;

import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.User;

/**
 * Created by bo0tzz
 */
public class TelegramMessageBuilder implements MessageBuilder<TelegramMessageBuilder> {

    public static String html_escape(String text) {
        text = text.replace("&", "&amp;"); // & is replaced with &amp;
        text = text.replace("'", "&apos;"); // ' is replaced with &apos;
        text = text.replace("\"", "&quot;"); // " is replaced with &quot;
        text = text.replace("<", "&lt;"); // < is replaced with &lt;
        text = text.replace(">", "&gt;"); // > is replaced with &gt;
        return text;
    }

    private final StringBuilder msg;

    private boolean             link          = false;
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
        link(null).strikethrough(false).italic(false).blink(false).underline(false).code(false).size(0).bold(false);
        return msg.toString();
    }

    @Override
    public TelegramMessageBuilder newLine() {
        msg.append("\n");
        return this;
    }

    @Override
    public TelegramMessageBuilder name(User user) {
        return text(user.name());
    }

    @Override
    public TelegramMessageBuilder text(String text) {
        msg.append(html_escape(text));
        return this;
    }

    @Override
    public TelegramMessageBuilder html(String text) {
        msg.append(text);
        return this;
    }

    @Override
    public TelegramMessageBuilder link(String url) {
        boolean on = url != null;
        if (link != on) {
            link = on;
            msg.append(on ? "<a href=\"" + url + "\">" : "</a>");
        }
        return this;
    }

    @Override
    public TelegramMessageBuilder bold(boolean on) {
        if (bold != on) {
            bold = on;
            msg.append(on ? "<b>" : "</b>");
        }
        return this;
    }

    @Override
    public TelegramMessageBuilder italic(boolean on) {
        if (italic != on) {
            italic = on;
            msg.append(on ? "<i>" : "</i>");
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
            msg.append(on ? "<pre>" : "</pre>");
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
