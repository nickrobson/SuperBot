package xyz.nickr.superbot.sys;

import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public abstract class MessageBuilder {

    @Getter @Setter
    private Keyboard keyboard;

    @Getter @Setter
    private boolean preview;

    public abstract int length();

    public abstract String build();

    public abstract MessageBuilder newLine();

    public abstract MessageBuilder name(User user);

    public abstract MessageBuilder escaped(String text, Object... params);

    public abstract MessageBuilder raw(String text, Object... params);

    public abstract MessageBuilder link(String url, String text);

    public abstract MessageBuilder bold(boolean on);

    public abstract MessageBuilder italic(boolean on);

    public abstract MessageBuilder code(boolean on);

    public abstract MessageBuilder underline(boolean on);

    public abstract MessageBuilder strikethrough(boolean on);

    public abstract MessageBuilder blink(boolean on);

    public abstract MessageBuilder size(int s);

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

    public MessageBuilder underline(Consumer<MessageBuilder> consumer) {
        consumer.accept(this.underline(true));
        return this.underline(false);
    }

    public MessageBuilder strikethrough(Consumer<MessageBuilder> consumer) {
        consumer.accept(this.strikethrough(true));
        return this.strikethrough(false);
    }

    public MessageBuilder blink(Consumer<MessageBuilder> consumer) {
        consumer.accept(this.blink(true));
        return this.blink(false);
    }

    public MessageBuilder size(int size, Consumer<MessageBuilder> consumer) {
        consumer.accept(this.size(size));
        return this.size(0);
    }

}
