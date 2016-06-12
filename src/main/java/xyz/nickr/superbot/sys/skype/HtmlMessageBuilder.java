package xyz.nickr.superbot.sys.skype;

import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.User;

public class HtmlMessageBuilder extends MessageBuilder {

    public static String html_escape(String text) {
        text = text.replace("&", "&amp;"); // & is replaced with &amp;
        text = text.replace("'", "&apos;"); // ' is replaced with &apos;
        text = text.replace("\"", "&quot;"); // " is replaced with &quot;
        text = text.replace("<", "&lt;"); // < is replaced with &lt;
        text = text.replace(">", "&gt;"); // > is replaced with &gt;
        return text;
    }

    private final StringBuilder msg;

    private boolean bold = false;
    private boolean italic = false;
    private boolean underline = false;
    private boolean code = false;
    private boolean blink = false;
    private boolean size = false;
    private boolean strikethrough = false;

    public HtmlMessageBuilder() {
        this("");
    }

    public HtmlMessageBuilder(String initial) {
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
        this.strikethrough(false).italic(false).blink(false).underline(false).code(false).size(0).bold(false);
        return this.msg.toString();
    }

    @Override
    public HtmlMessageBuilder newLine() {
        this.msg.append("\n");
        return this;
    }

    @Override
    public HtmlMessageBuilder name(User user) {
        return this.escaped(user.name());
    }

    @Override
    public HtmlMessageBuilder escaped(String text, Object... params) {
        this.msg.append(HtmlMessageBuilder.html_escape(params.length > 0 ? String.format(text, params) : text));
        return this;
    }

    @Override
    public HtmlMessageBuilder raw(String text, Object... params) {
        this.msg.append(String.format(text, params));
        return this;
    }

    @Override
    public HtmlMessageBuilder link(String url, String text) {
        this.msg.append("<a href=\"" + url + "\">" + text + "</a>");
        return this;
    }

    @Override
    public HtmlMessageBuilder bold(boolean on) {
        if (this.bold != on) {
            this.bold = on;
            this.msg.append(on ? "<b>" : "</b>");
        }
        return this;
    }

    @Override
    public HtmlMessageBuilder italic(boolean on) {
        if (this.italic != on) {
            this.italic = on;
            this.msg.append(on ? "<i>" : "</i>");
        }
        return this;
    }

    @Override
    public HtmlMessageBuilder underline(boolean on) {
        if (this.underline != on) {
            this.underline = on;
            this.msg.append(on ? "<u>" : "</u>");
        }
        return this;
    }

    @Override
    public HtmlMessageBuilder strikethrough(boolean on) {
        if (this.strikethrough != on) {
            this.strikethrough = on;
            this.msg.append(on ? "<s>" : "</s>");
        }
        return this;
    }

    @Override
    public HtmlMessageBuilder code(boolean on) {
        if (this.code != on) {
            this.code = on;
            this.msg.append(on ? "<pre>" : "</pre>");
        }
        return this;
    }

    @Override
    public HtmlMessageBuilder blink(boolean on) {
        if (this.blink != on) {
            this.blink = on;
            this.msg.append(on ? "<blink>" : "</blink>");
        }
        return this;
    }

    @Override
    public HtmlMessageBuilder size(int s) {
        boolean on = s > 0;
        if (this.size != on) {
            this.size = on;
            this.msg.append(on ? "<font size=\"" + s + "\">" : "</font>");
        }
        return this;
    }
}
