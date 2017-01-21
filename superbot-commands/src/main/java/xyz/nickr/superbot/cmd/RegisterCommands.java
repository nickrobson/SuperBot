package xyz.nickr.superbot.cmd;

import xyz.nickr.superbot.SuperBotCommands;
import xyz.nickr.superbot.cmd.fun.FCCPointsCommand;
import xyz.nickr.superbot.cmd.fun.HangmanCommand;
import xyz.nickr.superbot.cmd.fun.NumberwangCommand;
import xyz.nickr.superbot.cmd.game.PatternGameCommand;
import xyz.nickr.superbot.cmd.game.ReactionGameCommand;
import xyz.nickr.superbot.cmd.game.TicTacToeGameCommand;
import xyz.nickr.superbot.cmd.omdb.OmdbEpisodeCommand;
import xyz.nickr.superbot.cmd.omdb.OmdbSearchCommand;
import xyz.nickr.superbot.cmd.omdb.OmdbSeasonCommand;
import xyz.nickr.superbot.cmd.omdb.OmdbSeasonsCommand;
import xyz.nickr.superbot.cmd.omdb.OmdbTitleCommand;
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
import xyz.nickr.superbot.cmd.util.UpdateCommand;

/**
 * @author Nick Robson
 */
public class RegisterCommands {

    public static void register() {
        SuperBotCommands.loadCommands();

        SuperBotCommands.register(new ReloadCommand());
        SuperBotCommands.register(new StopCommand());

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
        SuperBotCommands.register(new UpdateCommand());
    }

}
