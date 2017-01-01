package xyz.nickr.superbot.sys;

public class KeyboardBuilder {

    private Keyboard kb;

    public KeyboardBuilder() {
        this.kb = new Keyboard();
    }

    public KeyboardRowBuilder row() {
        return new KeyboardRowBuilder();
    }

    public Keyboard build() {
        return this.kb;
    }

    public class KeyboardRowBuilder {

        private KeyboardRow row;

        public KeyboardRowBuilder() {
            this.row = new KeyboardRow();
        }

        public KeyboardRowBuilder add(KeyboardButton button) {
            this.row.add(button);
            return this;
        }

        public KeyboardBuilder end() {
            KeyboardBuilder.this.kb.add(this.row);
            return KeyboardBuilder.this;
        }

    }

}
