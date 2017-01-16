package xyz.nickr.superbot.sys.telegram;

import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import pro.zackpollard.telegrambot.api.keyboards.InlineKeyboardButton;
import pro.zackpollard.telegrambot.api.keyboards.InlineKeyboardMarkup;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupConfiguration;
import xyz.nickr.superbot.sys.Keyboard;
import xyz.nickr.superbot.sys.KeyboardButton;
import xyz.nickr.superbot.sys.KeyboardRow;
import xyz.nickr.superbot.sys.Sys;

@AllArgsConstructor
public class TelegramInlineSys extends Sys {

    private TelegramSys sys;

    @Override
    public String getName() {
        return this.sys.getName();
    }

    @Override
    public String prefix() {
        return this.sys.prefix();
    }

    @Override
    public boolean isUIDCaseSensitive() {
        return this.sys.isUIDCaseSensitive();
    }

    @Override
    public boolean hasKeyboards() {
        return true;
    }

    @Override
    public String getUserFriendlyName(String uniqueId) {
        return this.sys.getUserFriendlyName(uniqueId);
    }

    @Override
    public Group getGroup(String uniqueId) {
        return sys.getGroup(uniqueId);
    }

    public static final String KEYBOARD_ID_NAMESPACE = "SuperBot::InlineKeyboard";
    public static final String RESULT_ID_NAMESPACE = "SuperBot::InlineResult";

    public static InlineKeyboardMarkup toTGKeyboard(String prefix, Keyboard kb) {
        if (prefix == null || kb == null)
            return null;
        InlineKeyboardMarkup.InlineKeyboardMarkupBuilder reply = InlineKeyboardMarkup.builder();
        int row = 0;
        for (KeyboardRow kbr : kb) {
            List<InlineKeyboardButton> btns = new LinkedList<>();
            int rowb = 0;
            for (KeyboardButton b : kbr) {
                btns.add(InlineKeyboardButton.builder().callbackData(prefix + "-" + row + "-" + rowb).text(b.getText()).build());
                rowb++;
            }
            reply.addRow(btns);
            row++;
        }
        return reply.build();
    }
}
