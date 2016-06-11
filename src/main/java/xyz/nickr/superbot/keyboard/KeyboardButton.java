package xyz.nickr.superbot.keyboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor(staticName = "of")
public class KeyboardButton {

    @NonNull
    private final String text;

    @NonNull
    private final Runnable onClick;

}
