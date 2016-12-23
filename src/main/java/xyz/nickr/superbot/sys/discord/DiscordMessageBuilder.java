package xyz.nickr.superbot.sys.discord;

import xyz.nickr.superbot.sys.MessageBuilder;

import static xyz.nickr.superbot.sys.MessageBuilder.*;

/**
 * @author Nick Robson
 */
public class DiscordMessageBuilder {

    public static String markdown_escape(String text, boolean code) {
        if (!code) {
            text = text.replace("*", "\\*");
            text = text.replace("_", "\\_");
            text = text.replace("[", "\\[");
        }
        return text;
    }

    public static String build(MessageBuilder mb) {
        mb.italic(false).bold(false).code(false).codeblock(false);

        String message = "";
        boolean isInCode = false;
        for (MessageBuilder.Token token : mb.getTokens()) {
            switch (token.getType()) {
                case TOKEN_NEWLINE: {
                    message += "\n";
                    break;
                }
                case TOKEN_ESCAPED_STRING: {
                    EscapedStringToken est = (EscapedStringToken) token;
                    message += markdown_escape(est.getString(), isInCode);
                    break;
                }
                case TOKEN_RAW_STRING: {
                    RawStringToken rst = (RawStringToken) token;
                    message += rst.getString();
                    break;
                }
                case TOKEN_LINK: {
                    LinkToken link = (LinkToken) token;
                    message += link.getUrl();
                    break;
                }
                case TOKEN_BOLD: {
                    message += "**";
                    break;
                }
                case TOKEN_ITALIC: {
                    message += "_";
                    break;
                }
                case TOKEN_CODEBLOCK:
                    message += "```";
                    isInCode = !isInCode;
                    break;
                case TOKEN_CODE: {
                    message += "`";
                    isInCode = !isInCode;
                    break;
                }
            }
        }
        return message;
    }

}
