package xyz.nickr.superbot.sys;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import lombok.Getter;

public class PaginatedData {

    @Getter
    private List<MessageBuilder> pages;

    public PaginatedData(List<MessageBuilder> lines, int pageHeight, boolean code) {
        this.pages = new LinkedList<>();
        int page = 0;
        while (page * pageHeight < lines.size()) {
            MessageBuilder m = new MessageBuilder();
            for (int i = page * pageHeight, j = Math.min((page + 1) * pageHeight, lines.size()); i < j; i++) {
                m.raw(lines.get(i));
                if (i != j - 1) {
                    m.newLine();
                }
            }
            this.pages.add(m);
            page++;
        }
        for (int i = 0; i < this.pages.size(); i++) {
            MessageBuilder z = new MessageBuilder().codeblock(code).raw(this.pages.get(i)).codeblock(false);
            z.newLine().bold(true).escaped("Page %d of %d", i + 1, this.pages.size()).bold(false);
            this.pages.set(i, z);
        }
    }

    public int getNumberOfPages() {
        return this.pages.size();
    }

    public void send(Sys sys, Group group, int page) {
        if (page < 0 || page >= pages.size()) {
            System.err.format("requested page %d, only have [0, %d)", page, pages.size());
            return;
        }
        MessageBuilder builder = sys.message().raw(this.pages.get(page));
        if (sys.hasKeyboards()) {
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
            group.sendMessage(builder);
        }
    }

}
