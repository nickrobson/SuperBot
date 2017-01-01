package xyz.nickr.superbot.sys;

import java.util.function.Consumer;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
public class KeyboardButton implements Cloneable {

    @NonNull
    @Getter
    private final String text;

    @Setter
    private Function<User, KeyboardButtonResponse> onClick;

    public KeyboardButton(String text, Consumer<User> onClick) {
        this(text, u -> {
            onClick.accept(u);
            return null;
        });
    }

    public KeyboardButton(String text, Runnable onClick) {
        this(text, u -> {
            onClick.run();
            return null;
        });
    }

    public KeyboardButtonResponse onClick(User user) {
        return this.onClick != null ? this.onClick.apply(user) : null;
    }

    @Override
    public KeyboardButton clone() {
        return new KeyboardButton(text, onClick);
    }

}
