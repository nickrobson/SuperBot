package xyz.nickr.superbot.sys.gitter;

import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.User;
import xyz.nickr.superbot.sys.telegram.TelegramMessageBuilder;

/**
 * Created by bo0tzz
 */
public class GitterMessageBuilder extends MessageBuilder {

    public static String markdown_escape(String text, boolean code) {
        if (!code) {
            text = text.replace("*", "\\*");
            text = text.replace("_", "\\_");
            text = text.replace("[", "\\[");
        }
        return text;
    }

    @Override
    public String toString() {
        return this.build();
    }

    @Override
    public String build() {
        this.italic(false).bold(false).code(false);

        String message = "";
        boolean isInCode = false;
        for (Token token : getTokens()) {
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
                    message += "[" + link.getText() + "](" + link.getUrl() + ")";
                    break;
                }
                case TOKEN_BOLD: {
                    message += "*";
                    break;
                }
                case TOKEN_ITALIC: {
                    message += "_";
                    break;
                }
                case TOKEN_CODE: {
                    message += "`";
                    isInCode = true;
                    break;
                }
            }
        }
        return message;
    }

}
