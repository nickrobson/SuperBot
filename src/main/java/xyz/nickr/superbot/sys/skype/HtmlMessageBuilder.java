package xyz.nickr.superbot.sys.skype;

import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.User;

public class HtmlMessageBuilder implements MessageBuilder<HtmlMessageBuilder> {

    public static String html_escape(String text) {
        text = text.replace("&", "&amp;"); // & is replaced with &amp;
        text = text.replace("'", "&apos;"); // ' is replaced with &apos;
        text = text.replace("\"", "&quot;"); // " is replaced with &quot;
        text = text.replace("<", "&lt;"); // < is replaced with &lt;
        text = text.replace(">", "&gt;"); // > is replaced with &gt;
        return text;
    }

    private final StringBuilder msg;

    private boolean             bold          = false;
    private boolean             italic        = false;
    private boolean             underline     = false;
    private boolean             code          = false;
    private boolean             blink         = false;
    private boolean             size          = false;
    private boolean             strikethrough = false;

    public HtmlMessageBuilder() {
        this("");
    }

    public HtmlMessageBuilder(String initial) {
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
        strikethrough(false).italic(false).blink(false).underline(false).code(false).size(0).bold(false);
        return msg.toString();
    }

    @Override
    public HtmlMessageBuilder newLine() {
        msg.append("\n");
        return this;
    }

    @Override
    public HtmlMessageBuilder name(User user) {
        return escaped(user.name());
    }

    @Override
    public HtmlMessageBuilder escaped(String text) {
        msg.append(html_escape(text));
        return this;
    }

    @Override
    public HtmlMessageBuilder raw(String text) {
        msg.append(text);
        return this;
    }

    @Override
    public HtmlMessageBuilder link(String url, String text) {
        msg.append("<a href=\"" + url + "\">" + text + "</a>");
        return this;
    }

    @Override
    public HtmlMessageBuilder bold(boolean on) {
        if (bold != on) {
            bold = on;
            msg.append(on ? "<b>" : "</b>");
        }
        return this;
    }

    @Override
    public HtmlMessageBuilder italic(boolean on) {
        if (italic != on) {
            italic = on;
            msg.append(on ? "<i>" : "</i>");
        }
        return this;
    }

    @Override
    public HtmlMessageBuilder underline(boolean on) {
        if (underline != on) {
            underline = on;
            msg.append(on ? "<u>" : "</u>");
        }
        return this;
    }

    @Override
    public HtmlMessageBuilder strikethrough(boolean on) {
        if (strikethrough != on) {
            strikethrough = on;
            msg.append(on ? "<s>" : "</s>");
        }
        return this;
    }

    @Override
    public HtmlMessageBuilder code(boolean on) {
        if (code != on) {
            code = on;
            msg.append(on ? "<pre>" : "</pre>");
        }
        return this;
    }

    @Override
    public HtmlMessageBuilder blink(boolean on) {
        if (blink != on) {
            blink = on;
            msg.append(on ? "<blink>" : "</blink>");
        }
        return this;
    }

    @Override
    public HtmlMessageBuilder size(int s) {
        boolean on = s > 0;
        if (size != on) {
            size = on;
            msg.append(on ? "<font size=\"" + s + "\">" : "</font>");
        }
        return this;
    }
}
