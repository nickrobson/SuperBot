package xyz.nickr.superbot.sys;

import java.util.function.Consumer;

public interface MessageBuilder<T extends MessageBuilder<T>> {

    int length();

    String build();

    T newLine();

    T name(User user);

    T escaped(String text);

    T raw(String text);

    T link(String url, String text);

    default T link(String url) {
        return link(url, url);
    }

    T bold(boolean on);

    T italic(boolean on);

    T code(boolean on);

    T underline(boolean on);

    T strikethrough(boolean on);

    T blink(boolean on);

    T size(int s);

    default T bold(Consumer<T> consumer) {
        consumer.accept(bold(true));
        return bold(false);
    }

    default T italic(Consumer<T> consumer) {
        consumer.accept(italic(true));
        return italic(false);
    }

    default T code(Consumer<T> consumer) {
        consumer.accept(code(true));
        return code(false);
    }

    default T underline(Consumer<T> consumer) {
        consumer.accept(underline(true));
        return underline(false);
    }

    default T strikethrough(Consumer<T> consumer) {
        consumer.accept(strikethrough(true));
        return strikethrough(false);
    }

    default T blink(Consumer<T> consumer) {
        consumer.accept(blink(true));
        return blink(false);
    }

    default T size(int size, Consumer<T> consumer) {
        consumer.accept(size(size));
        return size(0);
    }

}
