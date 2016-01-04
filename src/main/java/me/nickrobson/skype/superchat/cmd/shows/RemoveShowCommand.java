package me.nickrobson.skype.superchat.cmd.shows;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.SuperChatShows;
import me.nickrobson.skype.superchat.SuperChatShows.Show;
import me.nickrobson.skype.superchat.cmd.Command;
import me.nickrobson.skype.superchat.perm.Permission;
import me.nickrobson.skype.superchat.perm.StringPermission;

/**
 * Created by Horrgs on 1/1/2016.
 *
 * @author Horrgs
 * @author Nick Robson
 */
public class RemoveShowCommand implements Command {
    @Override
    public String[] names() {
        return new String[] { "removeshow" };
    }

    @Override
    public Permission perm() {
        return new StringPermission("shows.remove");
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[] { "[show]", "Remove a show from the list" };
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        if (args.length == 0) {
            group.sendMessage(bold(encode("Usage: ")) + encode(PREFIX + "removeshow"));
        } else {
            Show show = SuperChatShows.getShow(args[0]);
            if (show != null) {
                if (SuperChatShows.removeShow(args[0])) {
                    group.sendMessage(bold(encode("Removed show: ")) + encode(show.display));
                } else {
                    group.sendMessage(encode("Something went wrong."));
                }
            } else {
                group.sendMessage(encode("Couldn't find a show with the name \"" + args[0] + "\""));
            }
        }
    }
}
