package me.nickrobson.skype.superchat.cmd;

import java.util.function.Function;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import xyz.gghost.jskype.Group;
import xyz.gghost.jskype.message.Message;
import xyz.gghost.jskype.user.GroupUser;

public class ConvertCommand implements Command {
	
	private final Table<String, String, Conversion> conversions = HashBasedTable.create();
	
	public void init() {
		conversions.put("C", "F", new Conversion("Celsius", "Fahrenheit", true, s -> {
			double a = Double.parseDouble(s);
			return String.valueOf(32 + a * 9.0 / 5.0);
		}));
		conversions.put("F", "C", new Conversion("Fahrenheit", "Celsius", true, s -> {
			double a = Double.parseDouble(s);
			return String.valueOf((a - 32) * 5.0 / 9.0);
		}));
	}
	
	@Override
	public String[] names() {
		return new String[]{ "convert" };
	}

	@Override
	public String[] help(GroupUser user, boolean userChat) {
		return new String[]{ "(list | [from] [to] [input...])", "gets a list of conversions or converts [input] from [from] to [to]" };
	}

	@Override
	public void exec(GroupUser user, Group group, String used, String[] args, Message message) {
		if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
			StringBuilder builder = new StringBuilder();
			for (Cell<String, String, Conversion> cell : conversions.cellSet()) {
				if (builder.length() > 0)
					builder.append("\n");
				builder.append(encode(String.format("%s => %s (%s => %s)", cell.getRowKey(), cell.getColumnKey(), cell.getValue().from, cell.getValue().to)));
			}
			sendMessage(group, builder.toString());
		} else if (args.length < 3) {
			sendMessage(group, bold(encode("Usage: ")) + encode("~convert [from] [to] [input...]"));
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i = 2; i < args.length; i++) {
				if (sb.length() > 0)
					sb.append(" ");
				sb.append(args[i]);
			}
			String from = args[0].toUpperCase();
			String to = args[1].toUpperCase();
			String input = sb.toString();
			if (conversions.contains(from, to)) {
				Conversion conv = conversions.get(from, to);
				if (conv.numbers) {
					try {
						Double.parseDouble(input);
					} catch (Exception ex) {
						sendMessage(group, "[Convert] The conversion between " + from + " and " + to + " requires a number to be input.", true);
						return;
					}
				}
				try {
					String res = conversions.get(from, to).func.apply(input);
					sendMessage(group, encode("[Convert] ") + encode(String.format("(%s => %s) %s => %s", from, to, input, res)), false);
				} catch (Throwable t) {
					sendMessage(group, encode("[Convert] An error occurred while converting : " + t.getClass().getSimpleName()) + "\n" + encode(t.getMessage()));
				}
			} else {
				sendMessage(group, "[Convert] No conversion found between " + from + " and " + to + "!", true);
			}
		}
	}
	
	public static class Conversion {
		
		private final String from, to;
		private final boolean numbers;
		private final Function<String, String> func;
		
		public Conversion(String from, String to, boolean numbers, Function<String, String> func) {
			this.from = from;
			this.to = to;
			this.numbers = numbers;
			this.func = func;
		}
		
	}

}
