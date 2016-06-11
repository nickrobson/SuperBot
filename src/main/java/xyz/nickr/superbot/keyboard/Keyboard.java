package xyz.nickr.superbot.keyboard;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;

import lombok.Getter;

public class Keyboard implements Iterable<KeyboardRow> {

    @Getter
    private List<KeyboardRow> rows = new LinkedList<>();

    public Keyboard addButton(KeyboardRow button) {
        this.rows.add(button);
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
