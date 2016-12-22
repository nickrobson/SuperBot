package xyz.nickr.superbot.sys.skype;

import xyz.nickr.superbot.sys.MessageBuilder;

import static xyz.nickr.superbot.sys.MessageBuilder.*;

public class HtmlMessageBuilder {

    public static String html_escape(String text) {
        text = text.replace("&", "&amp;"); // & is replaced with &amp;
        text = text.replace("'", "&apos;"); // ' is replaced with &apos;
        text = text.replace("\"", "&quot;"); // " is replaced with &quot;
        text = text.replace("<", "&lt;"); // < is replaced with &lt;
        text = text.replace(">", "&gt;"); // > is replaced with &gt;
        return text;
    }

    public static String build(MessageBuilder m) {
        m.italic(false).bold(false).code(false).codeblock(false);

        String message = "";
        for (MessageBuilder.Token token : m.getTokens()) {
            switch (token.getType()) {
                case TOKEN_NEWLINE: {
                    message += "\n";
                    break;
                }
                case TOKEN_ESCAPED_STRING: {
                    EscapedStringToken est = (EscapedStringToken) token;
                    message += html_escape(est.getString());
                    break;
                }
                case TOKEN_RAW_STRING: {
                    RawStringToken rst = (RawStringToken) token;
                    message += rst.getString();
                    break;
                }
                case TOKEN_LINK: {
                    LinkToken link = (LinkToken) token;
                    message += "<a href=\"" + link.getUrl() + "\">" + link.getText() + "</a>";
                    break;
                }
                case TOKEN_BOLD: {
                    FormatToken ft = (FormatToken) token;
                    message += ft.isState() ? "<b>" : "</b>";
                    break;
                }
                case TOKEN_ITALIC: {
                    FormatToken ft = (FormatToken) token;
                    message += ft.isState() ? "<i>" : "</i>";
                    break;
                }
                case TOKEN_CODEBLOCK:
                case TOKEN_CODE: {
                    FormatToken ft = (FormatToken) token;
                    message += ft.isState() ? "<pre>" : "</pre>";
                    break;
                }
            }
        }
        return message;
    }

}
