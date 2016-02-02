package xyz.nickr.superbot.sys.gitter;

import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.User;

/**
 * Created by bo0tzz
 */
public class MarkdownMessageBuilder implements MessageBuilder<MarkdownMessageBuilder> {

    public static String markdown_escape(String text, boolean code) {
        if (!code) {
            text = text.replace("*", "\\*"); // * is replaced with \*
            text = text.replace("_", "\\_"); // _ is replaced with \_
            text = text.replace("[", "\\["); // [ is replaced with \[
        }
        text = text.replace(" ", "&nbsp;");
        return text;
    }

    private final StringBuilder msg;

    private boolean bold = false;
    private boolean italic = false;
    private boolean code = false;

    public MarkdownMessageBuilder() {
        this("");
    }

    public MarkdownMessageBuilder(String initial) {
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
    public MarkdownMessageBuilder newLine() {
        boolean wasCode = code;
        if (wasCode)
            code(false);
        msg.append("\n");
        if (wasCode)
            code(true);
        return this;
    }

    @Override
    public MarkdownMessageBuilder name(User user) {
        return escaped(user.name());
    }

    @Override
    public MarkdownMessageBuilder escaped(String text) {
        msg.append(markdown_escape(text, code));
        return this;
    }

    @Override
    public MarkdownMessageBuilder raw(String text) {
        msg.append(text);
        return this;
    }

    @Override
    public MarkdownMessageBuilder link(String url, String text) {
        msg.append("[" + text + "](" + url + ")");
        return this;
    }

    @Override
    public MarkdownMessageBuilder bold(boolean on) {
        if (bold != on) {
            bold = on;
            msg.append(on ? "**" : "**");
        }
        return this;
    }

    @Override
    public MarkdownMessageBuilder italic(boolean on) {
        if (italic != on) {
            italic = on;
            msg.append(on ? "_" : "_");
        }
        return this;
    }

    @Override
    public MarkdownMessageBuilder code(boolean on) {
        if (code != on) {
            code = on;
            msg.append(on ? "`" : "`");
        }
        return this;
    }

    @Override
    public MarkdownMessageBuilder underline(boolean on) {
        return this;
    }

    @Override
    public MarkdownMessageBuilder strikethrough(boolean on) {
        return this;
    }

    @Override
    public MarkdownMessageBuilder blink(boolean on) {
        return this;
    }

    @Override
    public MarkdownMessageBuilder size(int s) {
        return this;
    }
}
