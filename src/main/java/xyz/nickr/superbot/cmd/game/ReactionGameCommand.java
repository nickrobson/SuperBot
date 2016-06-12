package xyz.nickr.superbot.cmd.game;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.keyboard.Keyboard;
import xyz.nickr.superbot.keyboard.KeyboardButton;
import xyz.nickr.superbot.keyboard.KeyboardRow;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class ReactionGameCommand implements Command {

    private final Random random = new Random();

    @Override
    public String[] names() {
        return new String[] {"reaction"};
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[] {"", "start a new game of reaction"};
    }

    @Override
    public boolean useEverythingOn() {
        return false;
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        MessageBuilder mb = sys.message().escaped("Click the button below when it says GO");
        Keyboard kb = new Keyboard().add(new KeyboardRow().add(new KeyboardButton("Click me when I say 'GO'", () -> {})));
        mb.setKeyboard(kb);
        Message m = group.sendMessage(mb);
        try {
            Thread.sleep(4000L + this.random.nextInt(1000));
        } catch (InterruptedException e) {}
        AtomicBoolean won = new AtomicBoolean(false);
        MessageBuilder onWin = sys.message().setKeyboard(new Keyboard());
        kb = new Keyboard().add(new KeyboardRow().add(new KeyboardButton("GO", u -> {
            if (!won.get()) {
                won.set(true);
                m.edit(onWin.escaped("Winner: " + u.getProvider().getUserFriendlyName(u.getUniqueId())));
            }
        })));
        mb = sys.message().setKeyboard(kb);
        m.edit(mb);
    }

}
