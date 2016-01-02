package me.nickrobson.skype.superchat.cmd;

import in.kyle.ezskypeezlife.api.SkypeUserRole;
import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.SuperChatShows;

/**
 * Created by Horrgs on 1/1/2016.
 */
public class AddShowCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {
            "addshow", "as"
        };
    }

    @Override
    public SkypeUserRole role() {
        return SkypeUserRole.ADMIN;
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[] { "[show], [showday], [aliases...]", "Add a new show to the list. For aliases enclose in quotes." };
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        String[] showArgs = message.getMessage().split(",");
        String showName = showArgs[0], day = showArgs[1].replace(" ", ""), aliases = showArgs[2];
        if(SuperChatShows.getShow(showName) == null) {
            SuperChatShows.Show show = new SuperChatShows.Show(showName, day, aliases);
            SuperChatShows.addShow(show);
            aliases = aliases.replace(" ", "\n- ");
            group.sendMessage(encode("Added new Show! Let's review the info:\n" +
                    "Display Name: " + show.getDisplay() +
                    "\nDay of the Week: " + show.day +
                    "\nAliases: " + aliases));
        } else {
            group.sendMessage(encode("A show already exists with the name \"" + showName + "\""));
        }
    }

}
