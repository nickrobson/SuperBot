package me.nickrobson.skype.superchat.cmd;

import in.kyle.ezskypeezlife.api.SkypeUserRole;
import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.SuperChatShows;

/**
 * Created by Horrgs on 1/1/2016.
 */
public class RemoveShowCommand implements Command {
    @Override
    public String[] names() {
        return new String[] {
                "removeshow", "rs"
        };
    }

    @Override
    public SkypeUserRole role() {
        return SkypeUserRole.ADMIN;
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[] { "[show]", "Remove a show from the list" };
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        if(SuperChatShows.getShow(args[0]) != null) {
            SuperChatShows.removeShow(args[0]);
            group.sendMessage(encode("Remove show " + args[0]));
        } else {
            group.sendMessage(encode("Couldn't find a show with the name \"" + args[0] + "\""));
        }
    }
}
