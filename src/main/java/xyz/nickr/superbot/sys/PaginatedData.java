package xyz.nickr.superbot.sys;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import lombok.Getter;

public class PaginatedData {

    @Getter
    private List<MessageBuilder> pages;

    public PaginatedData(Supplier<MessageBuilder> mb, List<String> lines, int pageHeight, boolean escaped, boolean code) {
        this.pages = new LinkedList<>();
        int page = 0;
        while (page * pageHeight < lines.size()) {
            MessageBuilder m = mb.get();
            for (int i = page * pageHeight, j = Math.min((page + 1) * pageHeight, lines.size()); i < j; i++) {
                final int x = i;
                if (code && escaped) {
                    m.code(z -> z.escaped(lines.get(x)));
                } else if (escaped) {
                    m.escaped(lines.get(x));
                } else if (code) {
                    m.code(z -> z.raw(lines.get(x)));
                } else {
                    m.raw(lines.get(x));
                }
                if (i != j - 1) {
                    m.newLine();
                }
            }
            this.pages.add(m);
            page++;
        }
        for (int i = 0; i < this.pages.size(); i++) {
            MessageBuilder m = this.pages.get(i);
            MessageBuilder z = mb.get();
            z.bold(true).escaped("Page %d of %d", i + 1, this.pages.size()).bold(false).newLine();
            z.raw(m.build());
            z.setKeyboard(m.getKeyboard());
            this.pages.set(i, z);
        }
    }

    public int getNumberOfPages() {
        return this.pages.size();
    }

    public void send(Sys sys, Group group, int page) {
        MessageBuilder builder = sys.message();
        if (sys.hasKeyboards()) {
            builder.raw(this.pages.get(page));
            AtomicInteger currentPage = new AtomicInteger(0);
            AtomicReference<Message> msg = new AtomicReference<>();
            if (this.pages.size() > 1) {
                Keyboard kb = new Keyboard().add(new KeyboardRow().add(new KeyboardButton("«", () -> {
                    int cPage = currentPage.get();
                    int prevPage = (cPage == 0 ? this.pages.size() : cPage) - 1;
                    currentPage.set(prevPage);
                    msg.get().edit(this.pages.get(prevPage));
                })).add(new KeyboardButton("»", () -> {
                    int cPage = currentPage.get() + 1;
                    int nextPage = cPage == this.pages.size() ? 0 : cPage;
                    currentPage.set(nextPage);
                    msg.get().edit(this.pages.get(nextPage));
                })));
                builder.setKeyboard(kb);
            }
            msg.set(group.sendMessage(builder));
        } else {
            group.sendMessage(this.pages.get(page));
        }
    }

}
