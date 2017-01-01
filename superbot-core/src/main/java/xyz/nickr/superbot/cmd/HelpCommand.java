package xyz.nickr.superbot.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import xyz.nickr.superbot.SuperBotCommands;
import xyz.nickr.superbot.SuperBotResource;
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
        return new String[] {"help"};
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] {"(search)", "see the help menu, or only matching lines"};
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
            if (s.length() > pre.length()) {
                s += ",";
            }
            s += n.trim();
        }
        String cmdHelp = cmd.help(user, userChat)[0];
        if (cmdHelp.length() > 0) {
            s += " " + cmd.help(user, userChat)[0];
        }
        return s;
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        List<Command> cmds = new ArrayList<>(SuperBotCommands.COMMANDS.size());
        SuperBotCommands.COMMANDS.forEach((name, cmd) -> {
            boolean go = true;
            for (Command c : cmds) {
                if (c == cmd) {
                    go = false;
                }
            }
            if (go) {
                cmds.add(cmd);
            }
        });
        GroupConfiguration cfg = GroupConfiguration.getGroupConfiguration(group);
        if (cfg != null) {
            cmds.removeIf(cmd -> !cfg.isCommandEnabled(cmd));
        } else if (group.getType() == GroupType.USER) {
            cmds.removeIf(cmd -> !cmd.userchat());
        } else {
            cmds.removeIf(cmd -> !cmd.alwaysEnabled());
        }
        if (cmds.isEmpty()) {
            group.sendMessage(sys.message().escaped("It looks like there are no commands enabled in this chat."));
            return;
        }
        String prefix = sys.prefix();
        List<String> strings = new ArrayList<>(SuperBotCommands.COMMANDS.size());
        cmds.forEach(c -> {
            String[] help = c.help(user, group.getType() == GroupType.USER);
            if (c.perm() == Command.DEFAULT_PERMISSION) {
                strings.add(this.getCmdHelp(c, prefix, user, group.getType() == GroupType.USER) + "\n   - " + help[1]);
            }
        });
        strings.sort(null);
        if (args.length > 0) {
            strings.removeIf(s -> !s.contains(args[0]));
        }
        String welcome;
        if (group.getType() == GroupType.USER) {
            welcome = "Welcome, " + user.getUsername();
        } else {
            welcome = String.format(SuperBotResource.WELCOME_MESSAGE, group.getDisplayName());
        }
        if (strings.isEmpty()) {
            group.sendMessage(sys.message().bold(true).escaped(welcome));
            return;
        }
        MessageBuilder mb = sys.message().bold(true).escaped(welcome).bold(false).escaped(" ").codeblock(true);
        strings.stream().flatMap(s -> Arrays.stream(s.split("\\n"))).forEachOrdered(s -> mb.newLine().escaped(s));
        group.sendMessage(mb);
    }

}
