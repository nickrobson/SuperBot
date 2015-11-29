package me.nickrobson.skype.superchat;

public class MessageBuilder {
    
    public static String join(String sep, Object[] strings) {
        String s = "";
        for (Object z : strings) {
            if (!s.isEmpty())
                s += sep;
            s += z.toString();
        }
        return s;
    }
    
    public static String join(String sep, Iterable<?> strings) {
        String s = "";
        for (Object z : strings) {
            if (!s.isEmpty())
                s += sep;
            s += z.toString();
        }
        return s;
    }

    // ' is replaced with &apos;
    // " is replaced with &quot;
    // & is replaced with &amp;
    // < is replaced with &lt;
    // > is replaced with &gt;
    public static String html_escape(String text) {
        text = text.replace("&", "&amp;");
        text = text.replace("'", "&apos;");
        text = text.replace("\"", "&quot;");
        text = text.replace("<", "&lt;");
        text = text.replace(">", "&gt;");
        return text;
    }

    private final StringBuilder msg;
    private boolean link = false, bold = false, italic = false, underline = false, code = false, blink = false,
            size = false, strikethrough = false;

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