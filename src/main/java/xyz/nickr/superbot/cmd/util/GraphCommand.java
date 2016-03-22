package xyz.nickr.superbot.cmd.util;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
            List<String> ags = new ArrayList<>(Arrays.asList(args));
            Map<String, Matcher> vars = ags.stream().collect(Collectors.toMap(a -> a, a -> VARIABLE_ARG.matcher(a)));
            vars.entrySet().removeIf(e -> !e.getValue().matches());
            ags.removeIf(a -> vars.containsKey(a));
            String input = Joiner.join("", ags);
            List<Map.Entry<String, String>> vs = vars.entrySet().stream()
                                                        .map(e -> e.getValue())
                                                        .map(m -> new AbstractMap.SimpleEntry<>(m.group(1), m.group(2)))
                                                        .collect(Collectors.toList());
            Expression e = new ExpressionBuilder(input)
                    .variables(vs.stream().map(z -> z.getKey()).collect(Collectors.toSet()))
                    .build();
            for (Map.Entry<String, String> ent : vs) {
                e.setVariable(ent.getKey(), Double.parseDouble(ent.getValue()));
            }
            Map<Double, Double> values = new HashMap<>();
            Function<Double, Double> mapper = x -> e.setVariable("x", x).evaluate();
            int bounds = 39;
            for (double x = -bounds; x <= bounds; x += 1) {
                values.put(x, mapper.apply(x));
            }
            values.forEach((x, y) -> System.out.format("%d %d\n", x, y));
            int numbersPerColumn = bounds / 39; // skype has width of 79, so we use one for axis
            for (int y = 0; y < 39; y++) {
                int ay = (y - 19) * numbersPerColumn;
                mb.code(true);
                for (int x = 0; x < 79; x++) {
                    int ax = (x - 39) * numbersPerColumn;
                    if (ax == 0 && ay == 0) {
                        mb.escaped("+");
                    } else if (ax == 0) {
                        mb.escaped("|");
                    } else if (ay == 0) {
                        mb.escaped("-");
                    } else {
                        boolean found = false;
                        for (Entry<Double, Double> entry : values.entrySet()) {
                            if (Math.abs(entry.getKey() - ax) < numbersPerColumn / 2) {
                                if (Math.abs(entry.getValue() - ay) < numbersPerColumn / 2) {
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
