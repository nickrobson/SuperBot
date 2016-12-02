package xyz.nickr.superbot.cmd.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class ConvertCommand implements Command {

    public static final Map<String, Map<String, Conversion>> conversions = new HashMap<>();

    private void register(String row, String col, Conversion con) {
        Map<String, Conversion> map = conversions.get(row);
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(col, con);
        conversions.put(row, map);
    }

    @Override
    public void init() {
        conversions.clear();
        this.register("C", "F", new Conversion("Celsius", "Fahrenheit", true, true, s -> {
            BigDecimal a = new BigDecimal(s);
            BigDecimal d = a.multiply(BigDecimal.valueOf(9.0 / 5.0)).add(BigDecimal.valueOf(32));
            d = d.round(new MathContext(7, RoundingMode.HALF_UP));
            try {
                return d.toBigIntegerExact().toString();
            } catch (Exception ex) {
                return d.toString();
            }
        }));
        this.register("F", "C", new Conversion("Fahrenheit", "Celsius", true, true, s -> {
            BigDecimal a = new BigDecimal(s);
            BigDecimal d = a.subtract(BigDecimal.valueOf(32)).multiply(BigDecimal.valueOf(5.0 / 9.0));
            d = d.round(new MathContext(7, RoundingMode.HALF_UP));
            try {
                return d.toBigIntegerExact().toString();
            } catch (Exception ex) {
                return d.toString();
            }
        }));
        MultiplierConversion kmmi = new MultiplierConversion("Kilometres", "Miles", true, 0.6214);
        this.register("km", "mi", kmmi);
        this.register("mi", "km", kmmi.reverse());
    }

    @Override
    public String[] names() {
        return new String[] {"convert"};
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] {"[from] [to] [input...]", "converts [input] : [from] => [to]"};
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        MessageBuilder builder = sys.message();
        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            for (Map.Entry<String, Map<String, Conversion>> cell : conversions.entrySet()) {
                for (Map.Entry<String, Conversion> sub : cell.getValue().entrySet()) {
                    if (!builder.isEmpty()) {
                        builder.newLine();
                    }
                    builder.escaped(String.format("%s => %s (%s => %s)", cell.getKey(), sub.getKey(), sub.getValue().from, sub.getValue().to));
                }
            }
            group.sendMessage(builder);
        } else if (args.length < 3) {
            this.sendUsage(sys, user, group);
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(args[i]);
            }
            String from = args[0];
            String to = args[1];
            String input = sb.toString();
            if (conversions.containsKey(from) && conversions.get(from).containsKey(to)) {
                Conversion conv = conversions.get(from).get(to);
                if (conv.numbers) {
                    try {
                        new BigDecimal(input);
                    } catch (Exception ex) {
                        group.sendMessage(sys.message().escaped("[Convert] The conversion between " + from + " and " + to + " requires a number to be input."));
                        return;
                    }
                }
                try {
                    String res = conv.apply(input);
                    if (conv.appendSymbol) {
                        group.sendMessage(sys.message().escaped("[Convert] " + String.format("%s%s => %s%s", input, from, res, to)));
                    } else {
                        group.sendMessage(sys.message().escaped("[Convert] " + String.format("(%s => %s) %s => %s", from, to, input, res)));
                    }
                } catch (Throwable t) {
                    group.sendMessage(sys.message().escaped("[Convert] An error occurred while converting : " + t.getClass().getSimpleName() + "\n" + t.getMessage()));
                }
            } else {
                group.sendMessage(sys.message().escaped("[Convert] No conversion found between " + from + " and " + to + "!"));
            }
        }
    }

    public static class Conversion {

        public final String from, to;
        final boolean numbers, appendSymbol;
        final Function<String, String> func;

        public Conversion(String from, String to, boolean numbers, boolean appendSymbol, Function<String, String> func) {
            this.from = from;
            this.to = to;
            this.numbers = numbers;
            this.appendSymbol = appendSymbol;
            this.func = func;
        }

        public String apply(String s) {
            return this.func.apply(s);
        }

    }

    public static class MultiplierConversion extends Conversion {

        double multiplier;

        public MultiplierConversion(String from, String to, boolean appendSymbol, double multiplier) {
            super(from, to, true, appendSymbol, s -> {
                BigDecimal a = new BigDecimal(s);
                BigDecimal d = a.multiply(BigDecimal.valueOf(multiplier));
                d = d.round(new MathContext(7, RoundingMode.HALF_UP));
                try {
                    return d.toBigIntegerExact().toString();
                } catch (Exception ex) {
                    return d.toString();
                }
            });
            this.multiplier = multiplier;
        }

        public MultiplierConversion reverse() {
            return new MultiplierConversion(this.to, this.from, this.appendSymbol, 1.0 / this.multiplier);
        }

    }

    public static class AdderConversion extends Conversion {

        double add;

        public AdderConversion(String from, String to, boolean appendSymbol, double add) {
            super(from, to, true, appendSymbol, s -> {
                BigDecimal a = new BigDecimal(s);
                BigDecimal d = a.add(BigDecimal.valueOf(add));
                d = d.round(new MathContext(7, RoundingMode.HALF_UP));
                try {
                    return d.toBigIntegerExact().toString();
                } catch (Exception ex) {
                    return d.toString();
                }
            });
            this.add = add;
        }

        public AdderConversion reverse() {
            return new AdderConversion(this.to, this.from, this.appendSymbol, -this.add);
        }

    }

}
