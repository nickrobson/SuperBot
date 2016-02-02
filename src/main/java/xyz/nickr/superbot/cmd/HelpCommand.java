package xyz.nickr.superbot.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import xyz.nickr.superbot.SuperBotCommands;
import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupConfiguration;
import xyz.nickr.superbot.sys.GroupType;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class HelpCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "help" };
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] { "(search)", "see the help menu, or only matching lines" };
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public boolean alwaysEnabled() {
        return true;
    }

    String getCmdHelp(Command cmd, String pre, User user, boolean userChat) {
        String s = pre;
        for (String n : cmd.names()) {
            if (s.length() > pre.length())
                s += ",";
            s += n.trim();
        }
        String cmdHelp = cmd.help(user, userChat)[0];
        if (cmdHelp.length() > 0)
            s += " " + cmd.help(user, userChat)[0];
        return s;
    }

    String pad(String str, int len) {
        while (str.length() < len)
            str += " "; // + str;
        return str;
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        List<Command> cmds = new ArrayList<>(SuperBotCommands.COMMANDS.size());
        SuperBotCommands.COMMANDS.forEach((name, cmd) -> {
            boolean go = true;
            for (Command c : cmds)
                if (c == cmd)
                    go = false;
            if (go)
                cmds.add(cmd);
        });
        GroupConfiguration cfg = SuperBotController.getGroupConfiguration(group);
        if (cfg != null)
            cmds.removeIf(cmd -> !cfg.isCommandEnabled(cmd));
        else if (group.getType() == GroupType.USER)
            cmds.removeIf(cmd -> !cmd.userchat());
        else
            cmds.removeIf(cmd -> !cmd.alwaysEnabled());
        if (cmds.isEmpty()) {
            group.sendMessage("It looks like there are no commands enabled in this chat.");
            return;
        }
        String prefix = sys.prefix();
        AtomicInteger maxLen = new AtomicInteger(0);
        cmds.forEach(c -> {
            String cmdHelp = getCmdHelp(c, prefix, user, group.getType() == GroupType.USER);
            if (c.perm() == Command.DEFAULT_PERMISSION && cmdHelp.length() > maxLen.get())
                maxLen.set(cmdHelp.length());
        });
        List<String> strings = new ArrayList<>(SuperBotCommands.COMMANDS.size());
        boolean cols = sys.columns();
        if (!cols)
            maxLen.set(0);
        cmds.forEach(c -> {
            String[] help = c.help(user, group.getType() == GroupType.USER);
            if (c.perm() == Command.DEFAULT_PERMISSION)
                strings.add(pad(getCmdHelp(c, prefix, user, group.getType() == GroupType.USER), maxLen.get()) + (cols ? "" : "\n  ") + " - " + help[1]);
        });
        if (SuperBotController.HELP_IGNORE_WHITESPACE)
            strings.sort((s1, s2) -> s1.trim().compareTo(s2.trim()));
        else
            strings.sort(null);
        if (args.length > 0)
            strings.removeIf(s -> !s.contains(args[0]));
        String welcome = String.format(SuperBotController.WELCOME_MESSAGE, group.getDisplayName());
        if (group.getType() == GroupType.USER)
            welcome = "Welcome, " + user.getUsername();
        if (strings.isEmpty()) {
            group.sendMessage(sys.message().bold(true).escaped(welcome));
            return;
        }
        int mid = welcome.length() / 2;
        String wel = pad(welcome.substring(0, mid), maxLen.get());
        String come = welcome.substring(mid);
        String spaces = SuperBotController.HELP_WELCOME_CENTRED ? strings.get(0).replaceAll("\\S.+", "") : wel.replaceAll("\\S+", "");
        MessageBuilder<?> mb = sys.message().code(true).escaped(spaces).code(false).bold(true).escaped(wel.trim() + come).bold(false).code(true);
        strings.forEach(s -> mb.newLine().escaped(s));
        group.sendMessage(mb);
    }

}
