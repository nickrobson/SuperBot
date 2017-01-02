package xyz.nickr.superbot.event;

import java.util.LinkedHashSet;
import java.util.Set;
import xyz.nickr.superbot.sys.Conversable;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

/**
 * @author Nick Robson
 */
public class EventManager {

    private static Set<Listener> listenerSet = new LinkedHashSet<>();

    public static void register(Listener listener) {
        listenerSet.add(listener);
    }

    public static void onMessage(Sys sys, Group group, User user, Message message) {
        listenerSet.forEach(l -> {
            try {
                l.onMessage(sys, group, user, message);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void onSend(Conversable conversable, MessageBuilder message) {
        listenerSet.forEach(l -> {
            try {
                l.onSend(conversable, message);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
