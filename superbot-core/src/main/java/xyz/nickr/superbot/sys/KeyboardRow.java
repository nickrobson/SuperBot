package xyz.nickr.superbot.sys;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class KeyboardRow implements Iterable<KeyboardButton>, Cloneable {

    @Getter
    private List<KeyboardButton> buttons = new LinkedList<>();

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private boolean locked;

    void lock() {
        this.locked = true;
    }

    public KeyboardRow add(KeyboardButton button) {
        if (!this.locked) {
            this.buttons.add(button);
        }
        return this;
    }

    @Override
    public KeyboardRow clone() {
        KeyboardRow newRow = new KeyboardRow();
        buttons.forEach(b -> newRow.add(b.clone()));
        return newRow;
    }

    @Override
    public Iterator<KeyboardButton> iterator() {
        return this.buttons.iterator();
    }

    @Override
    public Spliterator<KeyboardButton> spliterator() {
        return this.buttons.spliterator();
    }

}
