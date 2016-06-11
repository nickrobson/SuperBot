package xyz.nickr.superbot.keyboard;

import java.util.function.Consumer;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import xyz.nickr.superbot.sys.User;

@AllArgsConstructor
public class KeyboardButton {

    @NonNull
    @Getter
    private final String text;

    @Setter
    private Function<User, ButtonResponse> onClick;

    public KeyboardButton(String text, Consumer<User> onClick) {
        this(text, u -> {
            onClick.accept(u);
            return null;
        });
    }

    public KeyboardButton(String text, Runnable onClick) {
        this(text, u -> onClick.run());
    }

    public ButtonResponse onClick(User wrap) {
        return this.onClick != null ? this.onClick.apply(wrap) : null;
    }

}
