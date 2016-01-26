package xyz.nickr.superchat;

import in.kyle.ezskypeezlife.api.obj.SkypeUser;

public class MessageBuilder {

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
    private boolean             underline     = false;
    private boolean             code          = false;
    private boolean             blink         = false;
    private boolean             size          = false;
    private boolean             strikethrough = false;

    public MessageBuilder() {
        this("");
    }

    public MessageBuilder(String initial) {
        msg = new StringBuilder(initial);
    }

    public int length() {
        return msg.length();
    }

    @Override
    public String toString() {
        return build();
    }

    public String build() {
        link(null).strikethrough(false).italic(false).blink(false).underline(false).code(false).size(0).bold(false);
        return msg.toString();
    }

    public MessageBuilder newLine() {
        msg.append("\n");
        return this;
    }

    public MessageBuilder name(SkypeUser user) {
        return text(user.getDisplayName().orElse(user.getUsername()));
    }

    public MessageBuilder text(String text) {
        msg.append(html_escape(text));
        return this;
    }

    public MessageBuilder html(String text) {
        msg.append(text);
        return this;
    }

    public MessageBuilder link(String url) {
        boolean on = url != null;
        if (link != on) {
            link = on;
            msg.append(on ? "<a href=\"" + url + "\">" : "</a>");
        }
        return this;
    }

    public MessageBuilder bold(boolean on) {
        if (bold != on) {
            bold = on;
            msg.append(on ? "<b>" : "</b>");
        }
        return this;
    }

    public MessageBuilder italic(boolean on) {
        if (italic != on) {
            italic = on;
            msg.append(on ? "<i>" : "</i>");
        }
        return this;
    }

    public MessageBuilder underline(boolean on) {
        if (underline != on) {
            underline = on;
            msg.append(on ? "<u>" : "</u>");
        }
        return this;
    }

    public MessageBuilder strikethrough(boolean on) {
        if (strikethrough != on) {
            strikethrough = on;
            msg.append(on ? "<s>" : "</s>");
        }
        return this;
    }

    public MessageBuilder code(boolean on) {
        if (code != on) {
            code = on;
            msg.append(on ? "<pre>" : "</pre>");
        }
        return this;
    }

    public MessageBuilder blink(boolean on) {
        if (blink != on) {
            blink = on;
            msg.append(on ? "<blink>" : "</blink>");
        }
        return this;
    }

    public MessageBuilder size(int s) {
        boolean on = s > 0;
        if (size != on) {
            size = on;
            msg.append(on ? "<font size=\"" + s + "\">" : "</font>");
        }
        return this;
    }
}
