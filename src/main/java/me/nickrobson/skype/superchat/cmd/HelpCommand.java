package me.nickrobson.skype.superchat.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import in.kyle.ezskypeezlife.api.SkypeConversationType;
import in.kyle.ezskypeezlife.api.SkypeUserRole;
import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.GroupConfiguration;
import me.nickrobson.skype.superchat.SuperChatController;

public class HelpCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "help" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[] { "", "see this help message" };
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public boolean alwaysEnabled() {
        return true;
    }

    String getCmdHelp(Command cmd, SkypeUser user, boolean userChat) {
        String pre = SuperChatController.COMMAND_PREFIX;
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
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        List<Command> cmds = new ArrayList<>(SuperChatController.COMMANDS.size());
        SuperChatController.COMMANDS.forEach((name, cmd) -> {
            boolean go = true;
            for (Command c : cmds)
                if (c == cmd)
                    go = false;
            if (go)
                cmds.add(cmd);
        });
        GroupConfiguration cfg = SuperChatController.GCONFIGS.get(group.getLongId());
        if (cfg != null)
            cmds.removeIf(cmd -> !cfg.isCommandEnabled(cmd) && !cmd.alwaysEnabled());
        else if (group.getConversationType() == SkypeConversationType.USER)
            cmds.removeIf(cmd -> !cmd.userchat() && !cmd.alwaysEnabled());
        else
            cmds.removeIf(cmd -> !cmd.alwaysEnabled());
        if (cmds.isEmpty()) {
            group.sendMessage("It looks like there are no commands enabled in this chat.");
            return;
        }
        AtomicInteger maxLen = new AtomicInteger(0);
        cmds.forEach(c -> {
            String cmdHelp = getCmdHelp(c, user, group.getConversationType() == SkypeConversationType.USER);
            if (c.role() == SkypeUserRole.USER && cmdHelp.length() > maxLen.get())
                maxLen.set(cmdHelp.length());
        });
        List<String> strings = new ArrayList<>(SuperChatController.COMMANDS.size());
        StringBuilder builder = new StringBuilder();
        cmds.forEach(c -> {
            String[] help = c.help(user, group.getConversationType() == SkypeConversationType.USER);
            if (c.role() == SkypeUserRole.USER)
                strings.add(pad(getCmdHelp(c, user, group.getConversationType() == SkypeConversationType.USER), maxLen.get()) + " - " + help[1]);
        });
        if (SuperChatController.HELP_IGNORE_WHITESPACE)
            strings.sort((s1, s2) -> s1.trim().compareTo(s2.trim()));
        else
            strings.sort(null);
        String welcome = String.format(SuperChatController.WELCOME_MESSAGE, group.getTopic());
        if (group.getConversationType() == SkypeConversationType.USER)
            welcome = "Welcome, " + user.getUsername();
        int mid = welcome.length() / 2;
        String wel = pad(welcome.substring(0, mid), maxLen.get());
        String come = welcome.substring(mid);
        maxLen.set(0);
        strings.forEach(s -> {
            if (s.length() > maxLen.get())
                maxLen.set(s.length());
            builder.append("\n" + encode(s));
        });
        String spaces = SuperChatController.HELP_WELCOME_CENTRED ? strings.get(0).replaceAll("\\S.+", "") : wel.replaceAll("\\S+", "");
        group.sendMessage(code(encode(spaces)) + bold(encode(wel.trim() + come)) + code(builder.toString()));
    }

}
