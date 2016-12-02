package xyz.nickr.superbot.sys;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class Keyboard implements Iterable<KeyboardRow> {

    @Getter
    private List<KeyboardRow> rows = new LinkedList<>();

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private boolean locked;

    public Keyboard lock() {
        this.locked = true;
        this.rows.forEach(KeyboardRow::lock);
        return this;
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
