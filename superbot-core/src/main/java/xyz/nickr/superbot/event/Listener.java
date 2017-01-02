package xyz.nickr.superbot.event;

import xyz.nickr.superbot.sys.Conversable;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

/**
 * @author Nick Robson
 */
public interface Listener {

    void onMessage(Sys sys, Group group, User user, Message message);

    void onSend(Conversable conversable, MessageBuilder message);

}
