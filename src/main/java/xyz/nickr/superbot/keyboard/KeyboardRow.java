package xyz.nickr.superbot.keyboard;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class KeyboardRow implements Iterable<KeyboardButton> {

    @Getter
    private List<KeyboardButton> buttons = new LinkedList<>();

    @Setter(AccessLevel.PACKAGE)
    private boolean locked;

    public KeyboardRow addButton(KeyboardButton button) {
        if (!this.locked) {
            this.buttons.add(button);
        }
        return this;
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
