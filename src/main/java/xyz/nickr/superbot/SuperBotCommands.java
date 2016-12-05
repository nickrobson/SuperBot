
package xyz.nickr.superbot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.cmd.HelpCommand;
import xyz.nickr.superbot.cmd.ReloadCommand;
import xyz.nickr.superbot.cmd.StopCommand;
import xyz.nickr.superbot.cmd.config.EditConfigCommand;
import xyz.nickr.superbot.cmd.config.ShowConfigCommand;
import xyz.nickr.superbot.cmd.fun.FCCPointsCommand;
import xyz.nickr.superbot.cmd.fun.HangmanCommand;
import xyz.nickr.superbot.cmd.fun.NumberwangCommand;
import xyz.nickr.superbot.cmd.game.PatternGameCommand;
import xyz.nickr.superbot.cmd.game.ReactionGameCommand;
import xyz.nickr.superbot.cmd.game.TicTacToeGameCommand;
import xyz.nickr.superbot.cmd.LinkCommand;
import xyz.nickr.superbot.cmd.omdb.OmdbEpisodeCommand;
import xyz.nickr.superbot.cmd.omdb.OmdbSearchCommand;
import xyz.nickr.superbot.cmd.omdb.OmdbSeasonCommand;
import xyz.nickr.superbot.cmd.omdb.OmdbSeasonsCommand;
import xyz.nickr.superbot.cmd.omdb.OmdbTitleCommand;
import xyz.nickr.superbot.cmd.permission.AddPermCommand;
import xyz.nickr.superbot.cmd.permission.DelPermCommand;
import xyz.nickr.superbot.cmd.permission.ListPermsCommand;
import xyz.nickr.superbot.cmd.profile.CreateProfileCommand;
import xyz.nickr.superbot.cmd.profile.DeleteTokenCommand;
import xyz.nickr.superbot.cmd.profile.FindProfileCommand;
import xyz.nickr.superbot.cmd.profile.GetProfileCommand;
import xyz.nickr.superbot.cmd.profile.GetTokenCommand;
import xyz.nickr.superbot.cmd.profile.RegisterAccountCommand;
import xyz.nickr.superbot.cmd.shows.AddShowCommand;
import xyz.nickr.superbot.cmd.shows.MissedShowsCommand;
import xyz.nickr.superbot.cmd.shows.ProgressCommand;
import xyz.nickr.superbot.cmd.shows.RemoveShowCommand;
import xyz.nickr.superbot.cmd.shows.SetProgressCommand;
import xyz.nickr.superbot.cmd.shows.ShowsCommand;
import xyz.nickr.superbot.cmd.shows.TimetableCommand;
import xyz.nickr.superbot.cmd.shows.UpcomingCommand;
import xyz.nickr.superbot.cmd.shows.ViewingOrderCommand;
import xyz.nickr.superbot.cmd.shows.WhoCommand;
import xyz.nickr.superbot.cmd.shows.WipeCommand;
import xyz.nickr.superbot.cmd.util.ColourCommand;
import xyz.nickr.superbot.cmd.util.ConvertCommand;
import xyz.nickr.superbot.cmd.util.CurrencyCommand;
import xyz.nickr.superbot.cmd.util.DistanceCommand;
import xyz.nickr.superbot.cmd.util.GitCommand;
import xyz.nickr.superbot.cmd.util.GraphCommand;
import xyz.nickr.superbot.cmd.util.JenkinsCommand;
import xyz.nickr.superbot.cmd.util.MathsCommand;
import xyz.nickr.superbot.cmd.util.PasteFetchCommand;
import xyz.nickr.superbot.cmd.util.UidCommand;
import xyz.nickr.superbot.cmd.util.VersionCommand;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupConfiguration;
import xyz.nickr.superbot.sys.GroupType;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
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
        SuperBotCommands.COMMANDS.clear();

        SuperBotCommands.register(new HelpCommand());
        SuperBotCommands.register(new ReloadCommand());
        SuperBotCommands.register(new StopCommand());

        SuperBotCommands.register(new EditConfigCommand());
        SuperBotCommands.register(new ShowConfigCommand());

        SuperBotCommands.register(new LinkCommand());

        SuperBotCommands.register(new AddPermCommand());
        SuperBotCommands.register(new DelPermCommand());
        SuperBotCommands.register(new ListPermsCommand());

        SuperBotCommands.register(new GetProfileCommand());
        SuperBotCommands.register(new FindProfileCommand());

        SuperBotCommands.register(new CreateProfileCommand());
        SuperBotCommands.register(new RegisterAccountCommand());

        SuperBotCommands.register(new GetTokenCommand());
        SuperBotCommands.register(new DeleteTokenCommand());

        SuperBotCommands.register(new OmdbSearchCommand());
        SuperBotCommands.register(new OmdbTitleCommand());
        SuperBotCommands.register(new OmdbSeasonCommand());
        SuperBotCommands.register(new OmdbSeasonsCommand());
        SuperBotCommands.register(new OmdbEpisodeCommand());

        SuperBotCommands.register(new AddShowCommand());
        SuperBotCommands.register(new MissedShowsCommand());
        SuperBotCommands.register(new ProgressCommand());
        SuperBotCommands.register(new RemoveShowCommand());
        SuperBotCommands.register(new SetProgressCommand());
        SuperBotCommands.register(new ShowsCommand());
        SuperBotCommands.register(new TimetableCommand());
        SuperBotCommands.register(new UpcomingCommand());
        SuperBotCommands.register(new ViewingOrderCommand());
        SuperBotCommands.register(new WhoCommand());
        SuperBotCommands.register(new WipeCommand());

        SuperBotCommands.register(new HangmanCommand());
        SuperBotCommands.register(new NumberwangCommand());
        SuperBotCommands.register(new FCCPointsCommand());

        SuperBotCommands.register(new PatternGameCommand());
        SuperBotCommands.register(new ReactionGameCommand());
        SuperBotCommands.register(new TicTacToeGameCommand());

        SuperBotCommands.register(new ColourCommand());
        SuperBotCommands.register(new ConvertCommand());
        SuperBotCommands.register(new CurrencyCommand());
        SuperBotCommands.register(new DistanceCommand());
        SuperBotCommands.register(new GitCommand());
        SuperBotCommands.register(new GraphCommand());
        SuperBotCommands.register(new JenkinsCommand());
        SuperBotCommands.register(new MathsCommand());
        SuperBotCommands.register(new PasteFetchCommand());
        SuperBotCommands.register(new UidCommand());
        SuperBotCommands.register(new VersionCommand());

        CMDS.sort((c1, c2) -> c1.names()[0].compareTo(c2.names()[0]));
    }

    public static void exec(Sys sys, Group g, User u, Message message) {
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

        GroupConfiguration cfg = SuperBotController.getGroupConfiguration(g);
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
                MessageBuilder mb = sys.message();
                mb.bold(true).escaped("Error: ").bold(false);
                mb.escaped("You don't have permission to use " + prefix + cmdName + "!");
                g.sendMessage(mb);
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
