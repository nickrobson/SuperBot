
package xyz.nickr.superbot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.cmd.HelpCommand;
import xyz.nickr.superbot.cmd.config.EditConfigCommand;
import xyz.nickr.superbot.cmd.config.ShowConfigCommand;
import xyz.nickr.superbot.cmd.permission.AddPermCommand;
import xyz.nickr.superbot.cmd.permission.DelPermCommand;
import xyz.nickr.superbot.cmd.permission.ListPermsCommand;
import xyz.nickr.superbot.cmd.profile.CreateProfileCommand;
import xyz.nickr.superbot.cmd.profile.DeleteTokenCommand;
import xyz.nickr.superbot.cmd.profile.FindProfileCommand;
import xyz.nickr.superbot.cmd.profile.GetProfileCommand;
import xyz.nickr.superbot.cmd.profile.GetTokenCommand;
import xyz.nickr.superbot.cmd.profile.RegisterAccountCommand;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupConfiguration;
import xyz.nickr.superbot.sys.GroupType;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class SuperBotCommands {

    public static final List<Command> CMDS = new LinkedList<>();
    public static final Map<String, Command> COMMANDS = new HashMap<>();

    public static void register(Command cmd) {
        for (String name : cmd.names()) {
            SuperBotCommands.COMMANDS.put(name, cmd);
        }
        CMDS.add(cmd);
        cmd.init();
    }

    public static void loadCommands() {
        SuperBotCommands.register(new HelpCommand());

        SuperBotCommands.register(new EditConfigCommand());
        SuperBotCommands.register(new ShowConfigCommand());

        SuperBotCommands.register(new AddPermCommand());
        SuperBotCommands.register(new DelPermCommand());
        SuperBotCommands.register(new ListPermsCommand());

        SuperBotCommands.register(new GetProfileCommand());
        SuperBotCommands.register(new FindProfileCommand());
        SuperBotCommands.register(new CreateProfileCommand());
        SuperBotCommands.register(new RegisterAccountCommand());
        SuperBotCommands.register(new GetTokenCommand());
        SuperBotCommands.register(new DeleteTokenCommand());
    }

    public static void exec(Sys sys, Group g, User u, Message message) {
        System.out.println(u.getDisplayName().orElse(u.getUsername()) + ": " + message.getMessage());
        String msg = message.getMessage().trim();
        String[] words = msg.split("\\s+");
        String prefix = sys.prefix();

        if (msg.isEmpty() || words.length == 0 || !words[0].startsWith(prefix)) {
            return;
        }

        String cmdName = words[0].substring(prefix.length()).toLowerCase();
        Command cmd = SuperBotCommands.COMMANDS.get(cmdName);
        if (cmd == null) {
            return;
        }

        GroupConfiguration cfg = GroupConfiguration.getGroupConfiguration(g);
        if (g.getType() == GroupType.GROUP) {
            if (cfg.isDisabled() || !cfg.isCommandEnabled(cmd)) {
                return;
            }
        } else if (!cmd.userchat()) {
            return;
        }

        String[] args = new String[words.length - 1];
        System.arraycopy(words, 1, args, 0, args.length);

        boolean userchat = g.getType() == GroupType.USER && cmd.userchat();

        if (g.getType() == GroupType.GROUP || userchat) {
            if (!cmd.perm().has(sys, g, u, u.getProfile())) {
                cmd.sendNoPermission(sys, g);
            } else {
                try {
                    cmd.exec(sys, u, g, cmdName, args, message);
                } catch (Exception ex) {
                    g.sendMessage(sys.message().escaped("[ERROR] " + ex.getClass().getSimpleName() + ": " + ex.getMessage()));
                    ex.printStackTrace();
                }
            }
        }
    }

}
