package me.nickrobson.skype.superchat.cmd;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeMessage;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.MessageBuilder;
import me.nickrobson.skype.superchat.SuperChatController;
import me.nickrobson.skype.superchat.SuperChatShows;
import me.nickrobson.skype.superchat.SuperChatShows.Show;

public class ProgressCommand implements Command {

    @Override
    public String[] names() {
        return new String[] { "progress", "prg" };
    }

    @Override
    public String[] help(SkypeUser user, boolean userChat) {
        return new String[] { "[-a] [shows...]", "see progress on all or provided shows" };
    }

    @Override
    public void exec(SkypeUser user, SkypeConversation group, String used, String[] args, SkypeMessage message) {
        MessageBuilder builder = new MessageBuilder();
        boolean sent = false;
        boolean all_eps = false;

        List<String> argz = new LinkedList<>(Arrays.asList(args));

        for (Iterator<String> it = argz.iterator(); it.hasNext();) {
            String s = it.next();
            if (s.equals("-a")) {
                all_eps = true;
                it.remove();
            }
        }

        if (argz.size() > 0) {
            sent = true;
            for (int i = 0; i < argz.size(); i++) {
                Show show = SuperChatShows.getShow(argz.get(i));
                if (i > 0)
                    builder.html("\n");
                if (show == null)
                    builder = builder.text("Invalid show: " + argz.get(i));
                else {
                    builder = show(show.getMainName(), builder, all_eps);
                }
            }
        } else {
            group.sendMessage(
                    bold(encode("Usage: ")) + encode(SuperChatController.COMMAND_PREFIX + "progress [show...]"));
            return;
        }
        if (sent) {
            group.sendMessage(builder.build());
        } else {
            group.sendMessage(encode("No progress submitted for any show."));
        }
    }

    MessageBuilder show(String show, MessageBuilder builder, boolean all_eps) {
        Map<String, String> prg = SuperChatController.getProgress(show);
        List<String> eps = prg.values().stream().filter(s -> SuperChatShows.EPISODE_PATTERN.matcher(s).matches())
                .sorted((e1, e2) -> SuperChatController.whichEarlier(e1, e2).equals(e1) ? -1 : 1)
                .collect(Collectors.toList());
        List<String> epz = new LinkedList<>();
        eps.forEach(e -> {
            if (!epz.contains(e))
                epz.add(e);
        });
        builder.bold(true).text("Episode progress: " + SuperChatShows.getShow(show).getDisplay()).bold(false);
        if (epz.size() > 0) {
            if (all_eps) {
                for (String ep : epz) {
                    builder.newLine().text("- " + ep.toUpperCase() + ": " + SuperChatController.getUsersOn(show, ep));
                }
            } else {
                builder.newLine().text(
                        "- Earliest: " + epz.get(0) + " (" + SuperChatController.getUsersOn(show, epz.get(0)) + ")");
                builder.newLine().text("- Latest:   " + epz.get(epz.size() - 1) + " ("
                        + SuperChatController.getUsersOn(show, epz.get(epz.size() - 1)) + ")");
            }
        } else {
            builder.newLine().text("No progress submitted.");
        }
        return builder;
    }

}
