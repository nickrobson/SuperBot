package xyz.nickr.superbot.cmd.game;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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
        AtomicBoolean started = new AtomicBoolean(false);
        AtomicReference<Message> m = new AtomicReference<>();
        Keyboard kb = new Keyboard().add(new KeyboardRow().add(new KeyboardButton("Begin", () -> {
            if (!started.getAndSet(true)) {
                MessageBuilder mb = sys.message().bold(z -> z.escaped("Reaction")).escaped(" Click the button below when it says GO");
                Keyboard k = new Keyboard().add(new KeyboardRow().add(new KeyboardButton("Click me when I say 'GO'", () -> {})));
                mb.setKeyboard(k);
                m.get().edit(mb);
                try {
                    Thread.sleep(4000 + this.random.nextInt(10000));
                } catch (InterruptedException e) {}
                AtomicBoolean won = new AtomicBoolean(false);
                MessageBuilder onWin = sys.message().setKeyboard(new Keyboard());
                k = new Keyboard().add(new KeyboardRow().add(new KeyboardButton("GO", u -> {
                    if (!won.getAndSet(true)) {
                        m.get().edit(onWin.bold(z -> z.escaped("Reaction")).escaped(" Winner: " + u.getProvider().getUserFriendlyName(u.getUniqueId())));
                    }
                })));
                mb = sys.message().escaped("Click GO").setKeyboard(k);
                m.get().edit(mb);
            }
        })));
        m.set(group.sendMessage(sys.message().escaped("Click to begin ").bold(z -> z.escaped("Reaction")).setKeyboard(kb)));
    }

}
