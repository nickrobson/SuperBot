package me.nickrobson.skype.superchat.cmd;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.Function;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;

public class ConvertCommand implements Command {

    private final Table<String, String, Conversion> conversions = HashBasedTable.create();

    @Override
    public void init() {
        conversions.put("C", "F", new Conversion("Celsius", "Fahrenheit", true, true, s -> {
            BigDecimal a = new BigDecimal(s);
            BigDecimal d = a.multiply(BigDecimal.valueOf(9.0 / 5.0)).add(BigDecimal.valueOf(32));
            d = d.round(new MathContext(7, RoundingMode.HALF_UP));
            try {
                return d.toBigIntegerExact().toString();
            } catch (Exception ex) {
                return d.toString();
            }
        }));
        conversions.put("F", "C", new Conversion("Fahrenheit", "Celsius", true, true, s -> {
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
        conversions.put("km", "mi", kmmi);
        conversions.put("mi", "km", kmmi.reverse());
    }

    @Override
    public String[] names() {
        return new String[] { "convert" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[] { "[from] [to] [input...]", "converts [input] : [from] => [to]" };
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            StringBuilder builder = new StringBuilder();
            for (Cell<String, String, Conversion> cell : conversions.cellSet()) {
                if (builder.length() > 0)
                    builder.append("\n");
                builder.append(encode(String.format("%s => %s (%s => %s)", cell.getRowKey(), cell.getColumnKey(),
                        cell.getValue().from, cell.getValue().to)));
            }
            group.sendMessage(builder.toString());
        } else if (args.length < 3) {
            group.sendMessage(bold(encode("Usage: ")) + encode("~convert [from] [to] [input...]"));
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                if (sb.length() > 0)
                    sb.append(" ");
                sb.append(args[i]);
            }
            String from = args[0];
            String to = args[1];
            String input = sb.toString();
            if (conversions.contains(from, to)) {
                Conversion conv = conversions.get(from, to);
                if (conv.numbers) {
                    try {
                        new BigDecimal(input);
                    } catch (Exception ex) {
                        group.sendMessage(encode("[Convert] The conversion between " + from + " and " + to
                                + " requires a number to be input."));
                        return;
                    }
                }
                try {
                    String res = conv.func.apply(input);
                    if (conv.appendSymbol)
                        group.sendMessage(
                                encode("[Convert] ") + encode(String.format("%s%s => %s%s", input, from, res, to)));
                    else
                        group.sendMessage(encode("[Convert] ")
                                + encode(String.format("(%s => %s) %s => %s", from, to, input, res)));
                } catch (Throwable t) {
                    group.sendMessage(
                            encode("[Convert] An error occurred while converting : " + t.getClass().getSimpleName())
                                    + "\n" + encode(t.getMessage()));
                }
            } else {
                group.sendMessage(encode("[Convert] No conversion found between " + from + " and " + to + "!"));
            }
        }
    }

    public static class Conversion {

        final String from, to;
        final boolean numbers, appendSymbol;
        final Function<String, String> func;

        public Conversion(String from, String to, boolean numbers, boolean appendSymbol,
                Function<String, String> func) {
            this.from = from;
            this.to = to;
            this.numbers = numbers;
            this.appendSymbol = appendSymbol;
            this.func = func;
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
            return new MultiplierConversion(to, from, appendSymbol, 1.0 / multiplier);
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
            return new AdderConversion(to, from, appendSymbol, -add);
        }

    }

}
