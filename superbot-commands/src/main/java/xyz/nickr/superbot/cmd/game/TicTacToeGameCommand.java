package xyz.nickr.superbot.cmd.game;

import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.*;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * @author Nick Robson
 */
public class TicTacToeGameCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"tictactoe"};
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[] {"", "start a new game of tic tac toe"};
    }

    @Override
    public boolean useEverythingOn() {
        return false;
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        AtomicBoolean started = new AtomicBoolean(false);
        AtomicReference<Message> m = new AtomicReference<>();
        AtomicReference<MessageBuilder> resultMessage = new AtomicReference<>();
        AtomicReference<Board> board = new AtomicReference<>(new Board(user.getUniqueId()));
        AtomicReference<Runnable> onClick = new AtomicReference<>();
        Keyboard kb = new Keyboard().add(new KeyboardRow().add(new KeyboardButton("Begin", clicker -> {
            if (board.get().playerOne.equals(clicker.getUniqueId()))
                return;
            if (!started.getAndSet(true)) {
                board.get().playerTwo = clicker.getUniqueId();
                resultMessage.set(sys.message().escaped("Tic Tac Toe").newLine().escaped(user.getUsername()).escaped(" vs ").escaped(clicker.getUsername()));
                Consumer<Boolean> onWin = playerOne -> m.get().edit(sys.message().escaped("Congratulations, " + (playerOne ? user.getUsername() : clicker.getUsername())).newLine().raw(board.get().toTextKeyboard(sys)).setKeyboard(new Keyboard()));
                Runnable onDraw = () -> m.get().edit(sys.message().escaped("It's a draw between " + user.getUsername() + " and " + clicker.getUsername()).newLine().raw(board.get().toTextKeyboard(sys)).setKeyboard(new Keyboard()));
                onClick.set(() -> m.get().edit(sys.message().raw(resultMessage.get()).newLine().escaped("It's " + (board.get().playerOneTurn ? user.getUsername() : clicker.getUsername()) + "'s turn!").setKeyboard(board.get().toKeyboard(onClick.get(), onDraw, onWin))));
                onClick.get().run();
            }
        })));
        m.set(group.sendMessage(sys.message().escaped("Click to begin ").bold(z -> z.escaped("Tic Tac Toe")).setKeyboard(kb)));
    }

    class Board {

        char[][] grid = new char[3][3]; {
            for (int i = 0; i < 3; i++) {
                Arrays.fill(grid[i], ' ');
            }
        }
        boolean playerOneTurn = true, won = false;
        String playerOne, playerTwo;

        public Board(String playerOne) {
            this.playerOne = playerOne;
        }

        public Consumer<User> handler(Runnable onClick, Runnable onDraw, Consumer<Boolean> onWin, int x, int y) {
            return clicker -> {
                if (won || playerOne == null || playerTwo == null)
                    return;
                if (clicker.getUniqueId() != null && clicker.getUniqueId().equals(playerOneTurn ? playerOne : playerTwo)) {
                    if (y >= 0 && y < 3 && x >= 0 && x < 3) {
                        if (grid[y][x] != ' ')
                            return;
                        grid[y][x] = playerOneTurn ? 'x' : 'o';
                        playerOneTurn = !playerOneTurn;
                        String winner = getWinner();
                        if (winner != null) {
                            if (winner.isEmpty())
                                onDraw.run();
                            else
                                onWin.accept(!playerOneTurn);
                        } else {
                            onClick.run();
                        }
                    }
                }
            };
        }

        public MessageBuilder toTextKeyboard(Sys sys) {
            MessageBuilder mb = sys.message();
            for (int y = 0; y < 3; y++) {
                mb.code(true);
                for (int x = 0; x < 3; x++) {
                    mb.escaped(" " + grid[y][x] + " ");
                    if (x != 2)
                        mb.escaped("|");
                }
                mb.code(false).newLine();
                if (y != 2)
                    mb.code(true).escaped("---+---+---").code(false).newLine();
            }
            return mb;
        }

        public Keyboard toKeyboard(Runnable onClick, Runnable onDraw, Consumer<Boolean> onWin) {
            Keyboard kb = new Keyboard();
            for (int y = 0; y < 3; y++) {
                KeyboardRow kbr = new KeyboardRow();
                for (int x = 0; x < 3; x++) {
                    kbr.add(new KeyboardButton(String.valueOf(grid[y][x]), handler(onClick, onDraw, onWin, x, y)));
                }
                kb.add(kbr);
            }
            return kb;
        }

        public String getWinner() {
            won = true;
            for (int i = 0; i < 3; i++) {
                char[] row = grid[i];
                if (' ' != row[0] && row[0] == row[1] && row[0] == row[2]) {
                    return row[0] == 'x' ? playerOne : playerTwo;
                }
            }
            for (int i = 0; i < 3; i++) {
                char[] col = new char[]{ grid[0][i], grid[1][i], grid[2][i] };
                if (' ' != col[0] && col[0] == col[1] && col[0] == col[2]) {
                    return col[0] == 'x' ? playerOne : playerTwo;
                }
            }
            char[][] diags = new char[2][];
            diags[0] = new char[]{ grid[0][0], grid[1][1], grid[2][2] };
            diags[1] = new char[]{ grid[0][2], grid[1][1], grid[2][0] };
            for (int i = 0; i < 2; i++) {
                char[] diag = diags[i];
                if (' ' != diag[0] && diag[0] == diag[1] && diag[0] == diag[2]) {
                    return diag[0] == 'x' ? playerOne : playerTwo;
                }
            }
            boolean draw = true;
            for (char[] row : grid) {
                for (char x : row) {
                    if (x == ' ') {
                        draw = false;
                    }
                }
            }
            if (draw) {
                return "";
            }
            won = false;
            return null;
        }

    }

}
