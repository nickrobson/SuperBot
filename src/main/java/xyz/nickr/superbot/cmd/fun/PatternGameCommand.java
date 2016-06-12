package xyz.nickr.superbot.cmd.fun;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import xyz.nickr.superbot.ConsecutiveId;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.keyboard.ButtonResponse;
import xyz.nickr.superbot.keyboard.Keyboard;
import xyz.nickr.superbot.keyboard.KeyboardButton;
import xyz.nickr.superbot.keyboard.KeyboardRow;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class PatternGameCommand implements Command {

    private final Map<String, Map<String, String>> progress = new HashMap<>();
    private final String alphabet = "abcdefghijklmnopqrstuvwxyz";
    private final Random random = new Random();

    @Override
    public String[] names() {
        return new String[] {"pattern"};
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[] {"[easy/medium/hard]", "make a new pattern game"};
    }

    @Override
    public boolean useEverythingOn() {
        return false;
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length == 0) {
            this.sendUsage(sys, user, group);
            return;
        }
        List<String> modes = Arrays.asList("easy", "medium", "hard");
        int size = 3;
        int count = 4 + modes.indexOf(args[0].toLowerCase());
        if (count < 4) {
            group.sendMessage(sys.message().escaped("Gamemode must be easy, medium, or hard."));
            return;
        }
        String game = ConsecutiveId.next("SuperBot::PatternGame");
        this.progress.put(game, new HashMap<>());
        final StringBuilder pattern = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            char c;
            do {
                c = this.alphabet.charAt(this.random.nextInt(size * size));
            } while (pattern.length() > 0 && c == pattern.charAt(pattern.length() - 1));
            pattern.append(c);
        }
        AtomicBoolean hasShown = new AtomicBoolean();
        AtomicReference<Message> amsg = new AtomicReference<>();
        AtomicReference<Function<Integer, MessageBuilder>> ar = new AtomicReference<>();
        Function<Integer, Function<User, ButtonResponse>> btnAction = i -> {
            Function<User, ButtonResponse> f = u -> {
                if (!hasShown.get()) {
                    return null;
                }
                Map<String, String> prg = this.progress.get(game);
                String key = u.getProvider().getName() + "-" + u.getUniqueId();
                String out = prg.containsKey(key) ? prg.get(key) : "";
                if (out == null) {
                    return new ButtonResponse("You have already lost!", true);
                }
                if (out.length() < pattern.length()) {
                    char clicked = this.alphabet.charAt(i);
                    if (clicked == pattern.charAt(out.length())) {
                        prg.put(key, out += clicked);
                        this.progress.put(game, prg);
                    } else {
                        prg.put(key, null);
                        this.progress.put(game, prg);
                        return new ButtonResponse("Wrong button! You lose!", true);
                    }
                }
                if (out.length() == pattern.length()) {
                    return new ButtonResponse("Congratulations, you won!", true);
                } else {
                    return new ButtonResponse("Just enter the remaining " + (pattern.length() - out.length()) + " steps!", false);
                }
            };
            return f.andThen(br -> {
                if (hasShown.get()) {
                    amsg.get().edit(ar.get().apply(-1));
                }
                return br;
            });
        };
        Function<Integer, Keyboard> getKeyboard = i -> {
            Keyboard kb = new Keyboard();
            for (int y = 0; y < size; y++) {
                KeyboardRow row = new KeyboardRow();
                for (int x = 0; x < size; x++) {
                    final int idx = y * size + x;
                    KeyboardButton btn = new KeyboardButton(idx == i ? "THIS ONE" : String.valueOf(idx + 1), btnAction.apply(idx));
                    row.add(btn);
                }
                kb.add(row);
            }
            return kb;
        };
        Function<Integer, MessageBuilder> msg = i -> {
            MessageBuilder mb = sys.message().bold(z -> z.escaped("Difficulty: ")).escaped(args[0].toUpperCase());
            this.progress.get(game).entrySet().stream().filter(e -> e.getValue() != null && e.getValue().length() == pattern.length()).forEach(e -> {
                mb.newLine().escaped("- " + sys.getUserFriendlyName(e.getKey().split("-", 2)[1]));
            });
            mb.setKeyboard(getKeyboard.apply(i));
            return mb;
        };
        Message m = group.sendMessage(msg.apply(-1));
        ar.set(msg);
        amsg.set(m);
        new Thread(() -> {
            int c = 0;
            try {
                while (c <= pattern.length()) {
                    Thread.sleep(2000L);
                    if (c == pattern.length()) {
                        m.edit(msg.apply(-1));
                        hasShown.set(true);
                    } else {
                        m.edit(msg.apply(this.alphabet.indexOf(pattern.charAt(c))));
                    }
                    c++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
