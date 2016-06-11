package xyz.nickr.superbot.sys.gitter;

import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.User;

/**
 * Created by bo0tzz
 */
public class MarkdownMessageBuilder extends MessageBuilder {

    public static String markdown_escape(String text, boolean code) {
        if (!code) {
            text = text.replace("*", "\\*"); // * is replaced with \*
            text = text.replace("_", "\\_"); // _ is replaced with \_
        }
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
    public MarkdownMessageBuilder newLine() {
        this.msg.append("\n");
        return this;
    }

    @Override
    public MarkdownMessageBuilder name(User user) {
        return this.escaped(user.name());
    }

    @Override
    public MarkdownMessageBuilder escaped(String text, Object... params) {
        this.msg.append(MarkdownMessageBuilder.markdown_escape(String.format(text, params), this.code));
        return this;
    }

    @Override
    public MarkdownMessageBuilder raw(String text, Object... params) {
        this.msg.append(String.format(text, params));
        return this;
    }

    @Override
    public MarkdownMessageBuilder link(String url, String text) {
        this.msg.append("[" + text + "](" + url + ")");
        return this;
    }

    @Override
    public MarkdownMessageBuilder bold(boolean on) {
        if (this.bold != on) {
            this.bold = on;
            this.msg.append(on ? "**" : "**");
        }
        return this;
    }

    @Override
    public MarkdownMessageBuilder italic(boolean on) {
        if (this.italic != on) {
            this.italic = on;
            this.msg.append(on ? "_" : "_");
        }
        return this;
    }

    @Override
    public MarkdownMessageBuilder code(boolean on) {
        if (this.code != on) {
            this.code = on;
            this.msg.append(on ? "\n```text\n" : "\n```\n");
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
