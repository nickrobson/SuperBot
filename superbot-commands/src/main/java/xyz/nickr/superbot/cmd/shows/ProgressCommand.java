package xyz.nickr.superbot.cmd.shows;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import xyz.nickr.superbot.SuperBotResource;
import xyz.nickr.superbot.SuperBotShows;
import xyz.nickr.superbot.SuperBotShows.Show;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

public class ProgressCommand implements Command {

    @Override
    public String[] names() {
        return new String[] {"progress", "prg"};
    }

    @Override
    public String[] help(User user, boolean userChat) {
        return new String[] {"(-a) [shows...]", "see progress on all or provided shows"};
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        MessageBuilder builder = sys.message();
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
                Show show = SuperBotShows.getShow(argz.get(i));
                if (i > 0) {
                    builder.newLine();
                }
                if (show == null) {
                    builder = builder.escaped("Invalid show: " + argz.get(i));
                } else {
                    builder = this.show(show.getIMDB(), builder, all_eps);
                }
            }
        } else {
            this.sendUsage(sys, user, group);
            return;
        }
        if (sent) {
            group.sendMessage(builder);
        } else {
            group.sendMessage(sys.message().escaped("No progress submitted for any show."));
        }
    }

    MessageBuilder show(String show, MessageBuilder builder, boolean all_eps) {
        Map<String, String> prg = SuperBotResource.getProgress(show);
        List<String> eps = prg.values().stream().filter(s -> SuperBotShows.EPISODE_PATTERN.matcher(s).matches()).sorted((e1, e2) -> SuperBotResource.whichEarlier(e1, e2).equals(e1) ? -1 : 1).collect(Collectors.toList());
        List<String> epz = new LinkedList<>();
        eps.forEach(e -> {
            if (!epz.contains(e)) {
                epz.add(e);
            }
        });
        builder.bold(true).escaped("Episode progress: " + SuperBotShows.getShow(show).getDisplay()).bold(false);
        if (epz.size() > 0) {
            if (all_eps) {
                for (String ep : epz) {
                    builder.newLine().escaped("- " + ep.toUpperCase() + ": " + SuperBotResource.getUsersOn(show, ep));
                }
            } else {
                builder.newLine().escaped("- Earliest: " + epz.get(0) + " (" + SuperBotResource.getUsersOn(show, epz.get(0)) + ")");
                builder.newLine().escaped("- Latest:   " + epz.get(epz.size() - 1) + " (" + SuperBotResource.getUsersOn(show, epz.get(epz.size() - 1)) + ")");
            }
        } else {
            builder.newLine().escaped("No progress submitted.");
        }
        return builder;
    }

}
