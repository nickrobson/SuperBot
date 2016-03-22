package xyz.nickr.superbot.cmd.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Pattern;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import xyz.nickr.superbot.Joiner;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class GraphCommand implements Command {

    public static final Pattern VARIABLE_ARG = Pattern.compile("(?i)([a-z]+)=([0-9]+(?:\\.[0-9]+)?)");

    @Override
    public String[] names() {
        return new String[]{ "graph" };
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[]{ "[equation]", "graphs an equation for you" };
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length == 0) {
            sendUsage(sys, user, group);
            return;
        }
        MessageBuilder<?> mb = sys.message();
        try {
            String input = Joiner.join("", args);
            Expression e = new ExpressionBuilder(input).variables("x").build();
            Map<Double, Double> values = new HashMap<>();
            Function<Double, Double> mapper = x -> e.setVariable("x", x).evaluate();
            int bounds = 39;
            for (double x = -bounds; x <= bounds; x += 1) {
                values.put(x, mapper.apply(x));
            }
            values.forEach((x, y) -> System.out.format("%.3f %.3f\n", x, y));
            System.out.println("-----------------");
            int numbersPerColumn = bounds / 39; // skype has width of 79, so we use one for axis
            for (int y = 0; y < 39; y++) {
                int ay = (y - 19) * numbersPerColumn * 2;
                mb.code(true);
                for (int x = 0; x < 79; x++) {
                    int ax = (x - 39) * numbersPerColumn;
                    System.out.format("(%d, %d) (%d, %d)\n", ax, ay, x, y);
                    if (ax == 0 && ay == 0) {
                        mb.escaped("+");
                    } else if (ax == 0) {
                        mb.escaped("|");
                    } else if (ay == 0) {
                        mb.escaped("-");
                    } else {
                        boolean found = false;
                        for (Entry<Double, Double> entry : values.entrySet()) {
                            if (Math.abs(entry.getKey() - ax) <= numbersPerColumn / 2) {
                                if (Math.abs(entry.getValue() - ay) <= numbersPerColumn) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                        mb.escaped(found ? "." : " ");
                    }
                }
                mb.code(false).newLine();
            }
            group.sendMessage(mb);
        } catch (Exception ex) {
            group.sendMessage(mb.escaped("An error occurred: " + ex.getMessage()));
            ex.printStackTrace();
        }
    }

    @Override
    public boolean alwaysEnabled() {
        return true;
    }

    @Override
    public boolean userchat() {
        return true;
    }

}
