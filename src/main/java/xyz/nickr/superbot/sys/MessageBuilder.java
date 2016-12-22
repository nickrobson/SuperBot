package xyz.nickr.superbot.sys;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public final class MessageBuilder implements Cloneable {

    public static final String TOKEN_NEWLINE = "newline";
    public static final String TOKEN_ESCAPED_STRING = "string_escaped";
    public static final String TOKEN_RAW_STRING = "string_raw";
    public static final String TOKEN_LINK = "format_link";
    public static final String TOKEN_BOLD = "format_bold";
    public static final String TOKEN_ITALIC = "format_italic";
    public static final String TOKEN_CODE = "format_code";
    public static final String TOKEN_CODEBLOCK = "format_codeblock";

    private final List<Token> tokens = new LinkedList<>();

    @Getter @Setter
    private Keyboard keyboard;

    @Getter @Setter
    private boolean preview = true;

    public MessageBuilder() {}

    public MessageBuilder(MessageBuilder copyFrom) {
        this.tokens.addAll(copyFrom.tokens);
        this.keyboard = copyFrom.keyboard != null ? copyFrom.keyboard.clone() : null;
    }

    @Override
    public MessageBuilder clone() {
        return new MessageBuilder(this);
    }

    public List<Token> getTokens() {
        return Collections.unmodifiableList(tokens);
    }

    public boolean isEmpty() {
        return tokens.isEmpty();
    }

    public MessageBuilder newLine() {
        tokens.add(new NewlineToken());
        return this;
    }

    public MessageBuilder name(User user) {
        tokens.add(new UserToken(user));
        return this;
    }

    public MessageBuilder escaped(String text, Object... params) {
        tokens.add(new EscapedStringToken(params.length == 0 ? text : String.format(text, params)));
        return this;
    }

    public MessageBuilder raw(String text, Object... params) {
        tokens.add(new RawStringToken(params.length == 0 ? text : String.format(text, params)));
        return this;
    }

    public MessageBuilder raw(MessageBuilder mb) {
        for (Token token : mb.tokens) {
            if (token instanceof FormatToken) {
                FormatToken ft = (FormatToken) token;
                format(ft.getType(), ft.state);
            } else {
                tokens.add(token);
            }
        }
        return this;
    }

    public MessageBuilder link(String url, String text) {
        tokens.add(new LinkToken(url, text));
        return this;
    }

    private MessageBuilder format(String tokenType, boolean newState) {
        FormatToken on = new FormatToken(tokenType, true);
        FormatToken off = new FormatToken(tokenType, false);

        int lastOn = tokens.lastIndexOf(on);
        int lastOff = tokens.lastIndexOf(off);

        boolean previouslyOn = lastOn > lastOff;

        if (previouslyOn != newState) {
            tokens.add(newState ? on : off);
        }
        return this;
    }

    public MessageBuilder bold(boolean on) {
        return format(TOKEN_BOLD, on);
    }

    public MessageBuilder italic(boolean on) {
        return format(TOKEN_ITALIC, on);
    }

    public MessageBuilder code(boolean on) {
        return format(TOKEN_CODE, on);
    }

    public MessageBuilder codeblock(boolean on) {
        return format(TOKEN_CODEBLOCK, on);
    }

    public MessageBuilder link(String url) {
        return this.link(url, url);
    }

    public MessageBuilder bold(Consumer<MessageBuilder> consumer) {
        consumer.accept(this.bold(true));
        return this.bold(false);
    }

    public MessageBuilder italic(Consumer<MessageBuilder> consumer) {
        consumer.accept(this.italic(true));
        return this.italic(false);
    }

    public MessageBuilder code(Consumer<MessageBuilder> consumer) {
        consumer.accept(this.code(true));
        return this.code(false);
    }

    public MessageBuilder codeblock(Consumer<MessageBuilder> consumer) {
        consumer.accept(this.codeblock(true));
        return this.codeblock(false);
    }

    @Data
    @AllArgsConstructor
    public static class Token {

        private final String type;

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class NewlineToken extends Token {

        public NewlineToken() {
            super(TOKEN_NEWLINE);
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class UserToken extends EscapedStringToken {

        public UserToken(User user) {
            super(user.name());
        }

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class EscapedStringToken extends Token {

        private final String string;

        public EscapedStringToken(String string) {
            super(TOKEN_ESCAPED_STRING);
            this.string = string;
        }

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class RawStringToken extends Token {

        private final String string;

        public RawStringToken(String string) {
            super(TOKEN_RAW_STRING);
            this.string = string;
        }

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class LinkToken extends Token {

        private final String url, text;

        public LinkToken(String url, String text) {
            super(TOKEN_LINK);
            this.url = url;
            this.text = text;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class FormatToken extends Token {

        private final boolean state;

        public FormatToken(String type, boolean state) {
            super(type);
            this.state = state;
        }

    }

}
