package xyz.nickr.superbot.cmd.shows;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import xyz.nickr.superbot.SuperBotShows;
import xyz.nickr.superbot.SuperBotShows.Show;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.keyboard.Keyboard;
import xyz.nickr.superbot.keyboard.KeyboardButton;
import xyz.nickr.superbot.keyboard.KeyboardRow;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class ShowsCommand implements Command {

    public static final int SHOWS_PER_PAGE = 30;

    @Override
    public String[] names() {
        return new String[] {"shows"};
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] {"", "see which shows are being tracked"};
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        List<String> snd = new LinkedList<>();
        for (Show show : SuperBotShows.getShows()) {
            StringBuilder sb = new StringBuilder();
            for (String s : show.links) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(s);
            }
            if (sb.length() > 0) {
                snd.add("[" + show.getDisplay() + "] " + sb.toString());
            }
        }
        snd.sort(String.CASE_INSENSITIVE_ORDER);
        int rows = snd.size();
        final int maxpages = rows / ShowsCommand.SHOWS_PER_PAGE + (rows % ShowsCommand.SHOWS_PER_PAGE == 0 ? 0 : 1);
        Function<Integer, MessageBuilder> getPage = p -> {
            MessageBuilder b = sys.message();
            final List<String> send = snd.subList(p * ShowsCommand.SHOWS_PER_PAGE, Math.min((p + 1) * ShowsCommand.SHOWS_PER_PAGE, snd.size()));
            int maxLen = send.stream().max((s1, s2) -> s1.length() - s2.length()).orElse("").length();
            b.bold(m -> m.escaped("Page %d of %d", p + 1, maxpages)).newLine();
            for (int i = 0, j = send.size(); i < j; i++) {
                final int x = i;
                String spc = "";
                for (int k = send.get(i).length(); k < maxLen; k++) {
                    spc += ' ';
                }
                final String spaces = spc;
                b.code(m -> m.escaped(send.get(x) + spaces));
                if (i != j - 1) {
                    b.newLine();
                }
            }
            return b;
        };
        MessageBuilder builder = sys.message();
        if (sys.hasKeyboards()) {
            Map<Integer, MessageBuilder> pages = new HashMap<>();
            for (int i = 0; i < maxpages; i++) {
                pages.put(i, getPage.apply(i));
            }
            builder.raw(pages.get(0).build());
            AtomicInteger currentPage = new AtomicInteger(0);
            AtomicReference<Message> msg = new AtomicReference<>();

            if (pages.size() > 1) {
                Keyboard kb = new Keyboard().add(new KeyboardRow().add(new KeyboardButton("«", () -> {
                    int cPage = currentPage.get();
                    int prevPage = (cPage == 0 ? maxpages : cPage) - 1;
                    msg.get().edit(pages.get(prevPage));
                })).add(new KeyboardButton("»", () -> {
                    int cPage = currentPage.get() + 1;
                    int nextPage = cPage == maxpages ? 0 : cPage;
                    msg.get().edit(pages.get(nextPage));
                })));
                builder.setKeyboard(kb);
            }

            msg.set(group.sendMessage(builder));
        } else {
            int page = 0;
            if (args.length > 0) {
                try {
                    page = Integer.parseInt(args[0]) - 1;
                    if (page < 0 || page >= maxpages) {
                        final int x = page + 1, y = maxpages + 1;
                        group.sendMessage(sys.message().bold(m -> m.escaped("Invalid page: %d, not in [0, %d)", x, y)));
                    } else {
                        group.sendMessage(getPage.apply(page));
                    }
                } catch (Exception ex) {
                    group.sendMessage(sys.message().bold(m -> m.escaped("Not a number: %s", args[0])));
                }
            } else {
                group.sendMessage(getPage.apply(0));
            }
        }
    }

}
