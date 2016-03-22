package xyz.nickr.superbot.cmd.util;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

public class MathsCommand implements Command {

    public static final Pattern VARIABLE_ARG = Pattern.compile("(?i)([a-z]+)=([0-9]+(?:\\.[0-9]+)?)");

    @Override
    public String[] names() {
        return new String[]{ "maths", "math" };
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[]{ "[maths]", "interprets maths for you" };
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
            List<String> vars = ags.stream().filter(a -> VARIABLE_ARG.asPredicate().test(a)).collect(Collectors.toList());
            ags.removeIf(a -> vars.contains(a));
            String input = Joiner.join("", ags);
            mb.escaped("[Maths] Query: " + input).newLine();
            List<Map.Entry<String, String>> vs = vars.stream()
                                                        .map(a -> VARIABLE_ARG.matcher(a))
                                                        .map(m -> new AbstractMap.SimpleEntry<>(m.group(1), m.group(2)))
                                                        .collect(Collectors.toList());
            Expression e = new ExpressionBuilder(input)
                    .variables(vs.stream().map(z -> z.getKey()).collect(Collectors.toSet()))
                    .build();
            for (Map.Entry<String, String> ent : vs) {
                e.setVariable(ent.getKey(), Double.parseDouble(ent.getValue()));
            }
            double result = e.evaluate();
            mb.escaped("[Maths] Result: " + result);
            group.sendMessage(mb);
        } catch (Exception ex) {
            group.sendMessage(mb.escaped("An error occurred: " + ex.getMessage()));
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
