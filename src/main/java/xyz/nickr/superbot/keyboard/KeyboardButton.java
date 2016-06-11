package xyz.nickr.superbot.keyboard;

import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import xyz.nickr.superbot.sys.User;

@AllArgsConstructor(staticName = "of")
@RequiredArgsConstructor(staticName = "of")
public class KeyboardButton {

    @NonNull
    @Getter
    private final String text;

    private Consumer<User> onClick;

    public void onClick(User wrap) {
        if (this.onClick != null) {
            this.onClick.accept(wrap);
        }
    }

}
