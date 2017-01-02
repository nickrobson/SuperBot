package xyz.nickr.superbot.sys.gitter;

import xyz.nickr.jitter.api.Message;
import xyz.nickr.jitter.api.event.JitterListener;
import xyz.nickr.jitter.api.event.MessageReceivedEvent;
import xyz.nickr.superbot.SuperBotCommands;
import xyz.nickr.superbot.event.EventManager;

public class GitterListener implements JitterListener {

    private final GitterSys sys;

    GitterListener(GitterSys sys) {
        this.sys = sys;
    }

    public void onMessage(MessageReceivedEvent event) {
        Message m = event.getMessage();
        if (!m.getSender().getUsername().equalsIgnoreCase(sys.jitter.getCurrentUser().getUsername()))
            EventManager.onMessage(sys, sys.wrap(m.getRoom()), sys.wrap(m.getSender()), sys.wrap(m));
        SuperBotCommands.exec(sys, sys.wrap(m.getRoom()), sys.wrap(m.getSender()), sys.wrap(m));
        m.getRoom().getMessageHistory().markRead();
    }

}
