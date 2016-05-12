package xyz.nickr.superbot.sys;

import java.util.function.Consumer;

public interface MessageBuilder<T extends MessageBuilder<T>> {

    int length();

    String build();

    T newLine();

    T name(User user);

    T escaped(String text, Object... params);

    T raw(String text, Object... params);

    T link(String url, String text);

    default T link(String url) {
        return this.link(url, url);
    }

    T bold(boolean on);

    T italic(boolean on);

    T code(boolean on);

    T underline(boolean on);

    T strikethrough(boolean on);

    T blink(boolean on);

    T size(int s);

    default T bold(Consumer<T> consumer) {
        consumer.accept(this.bold(true));
        return this.bold(false);
    }

    default T italic(Consumer<T> consumer) {
        consumer.accept(this.italic(true));
        return this.italic(false);
    }

    default T code(Consumer<T> consumer) {
        consumer.accept(this.code(true));
        return this.code(false);
    }

    default T underline(Consumer<T> consumer) {
        consumer.accept(this.underline(true));
        return this.underline(false);
    }

    default T strikethrough(Consumer<T> consumer) {
        consumer.accept(this.strikethrough(true));
        return this.strikethrough(false);
    }

    default T blink(Consumer<T> consumer) {
        consumer.accept(this.blink(true));
        return this.blink(false);
    }

    default T size(int size, Consumer<T> consumer) {
        consumer.accept(this.size(size));
        return this.size(0);
    }

}
