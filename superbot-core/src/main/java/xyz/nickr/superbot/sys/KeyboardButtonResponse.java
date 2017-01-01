package xyz.nickr.superbot.sys;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KeyboardButtonResponse {

    private final String text;
    private final boolean showAlert;

}
