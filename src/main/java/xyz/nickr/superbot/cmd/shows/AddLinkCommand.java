package xyz.nickr.superbot.cmd.shows;

import xyz.nickr.jomdb.JavaOMDB;
import xyz.nickr.jomdb.model.TitleResult;
import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.SuperBotShows;
import xyz.nickr.superbot.SuperBotShows.Show;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.cmd.Permission;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

/**
 * Created by Horrgs on 1/1/2016.
 *
 * @author Horrgs
 * @author Nick Robson
 */
public class AddLinkCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "addlink" };
    }

    @Override
    public Permission perm() {
        return string("shows.add");
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] { "[imdbId] [aliases...]", "links an IMDB ID with aliases" };
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        if (args.length < 2) {
            sendUsage(sys, user, group);
        } else {
            MessageBuilder<?> mb = sys.message();
            String imdb = args[0];
            if (!JavaOMDB.IMDB_ID_PATTERN.matcher(imdb).matches()) {
                group.sendMessage(mb.escaped("Not an IMDB ID: " + imdb));
                return;
            }
            TitleResult res = SuperBotController.OMDB.titleById(imdb);
            if (res == null) {
                group.sendMessage(mb.escaped("No show found with IMDB ID " + imdb));
                return;
            }
            mb.bold(true).escaped("Linking results:").bold(false);
            for (int i = 1; i < args.length; i++) {
                String alias = args[i].toLowerCase();
                Show show = SuperBotShows.getShow(alias);
                if (show != null) {
                    mb.newLine().escaped("Link exists: " + alias + " => " + show.getDisplay());
                } else if (SuperBotShows.addLink(imdb, alias)) {
                    show = SuperBotShows.getShow(imdb);
                    mb.newLine().escaped("New link added: " + alias + " => " + res.title);
                } else {
                    mb.newLine().escaped("Something went wrong.");
                    break;
                }
            }
            group.sendMessage(mb);
        }
    }

}
