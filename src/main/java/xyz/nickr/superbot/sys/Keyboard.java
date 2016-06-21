package xyz.nickr.superbot.sys;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class Keyboard implements Iterable<KeyboardRow> {

    private final Map<String, KeyboardButton> buttons = new HashMap<>();

    @Getter
    private List<KeyboardRow> rows = new LinkedList<>();

    @Setter(AccessLevel.PACKAGE)
    private boolean locked;

    public Keyboard lock() {
        this.locked = true;
        this.rows.forEach(KeyboardRow::lock);
        if (this.buttons.isEmpty()) {
            this.rows.forEach(r -> r.forEach(b -> this.buttons.put(b.getText(), b)));
        }
        return this;
    }

    public KeyboardButton getButton(String data) {
        return this.buttons.get(data);
    }

    public Keyboard add(KeyboardRow row) {
        if (!this.locked) {
            this.rows.add(row);
        }
        return this;
    }

    @Override
    public Iterator<KeyboardRow> iterator() {
        return this.rows.iterator();
    }

    @Override
    public Spliterator<KeyboardRow> spliterator() {
        return this.rows.spliterator();
    }

}
