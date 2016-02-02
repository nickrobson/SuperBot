package xyz.nickr.superbot.sys;

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

}
