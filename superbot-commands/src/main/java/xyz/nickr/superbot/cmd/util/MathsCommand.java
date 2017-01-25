package xyz.nickr.superbot.cmd.util;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
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
        return new String[] {"maths", "math"};
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[] {"[maths]", "interprets maths for you"};
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length == 0) {
            this.sendUsage(sys, user, group);
            return;
        }
        MessageBuilder mb = sys.message();
        mb.escaped("[Maths] Query: " + String.join(" ", args)).newLine();
        List<String> ags = new ArrayList<>(Arrays.asList(args));
        Map<String, Matcher> vars = ags.stream().collect(Collectors.toMap(a -> a, VARIABLE_ARG::matcher));
        vars.entrySet().removeIf(e -> !e.getValue().matches());
        ags.removeIf(vars::containsKey);
        String input = String.join("", ags);
        List<Map.Entry<String, String>> vs = vars.values().stream().map(m -> new AbstractMap.SimpleEntry<>(m.group(1), m.group(2))).collect(Collectors.toList());
        Expression e = new ExpressionBuilder(input).variables(vs.stream().map(Map.Entry::getKey).collect(Collectors.toSet())).variables("e", "Pi").build();
        for (Map.Entry<String, String> ent : vs) {
            e.setVariable(ent.getKey(), Double.parseDouble(ent.getValue()));
        }
        e.setVariable("e", Math.E);
        e.setVariable("Pi", Math.PI);
        double result = e.evaluate();
        mb.escaped("[Maths] Result: " + result);
        group.sendMessage(mb);
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
