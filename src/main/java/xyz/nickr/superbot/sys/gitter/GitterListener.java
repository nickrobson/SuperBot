package xyz.nickr.superbot.sys.gitter;

import xyz.nickr.jitter.api.Message;
import xyz.nickr.jitter.api.event.JitterListener;
import xyz.nickr.jitter.api.event.MessageReceivedEvent;
import xyz.nickr.superbot.SuperBotCommands;

public class GitterListener implements JitterListener {

    private final GitterSys sys;

    GitterListener(GitterSys sys) {
        this.sys = sys;
    }

    public void onMessage(MessageReceivedEvent event) {
        Message m = event.getMessage();
        SuperBotCommands.exec(sys, sys.wrap(m.getRoom()), sys.wrap(m.getSender()), sys.wrap(m));
        m.getRoom().getMessageHistory().markRead();
    }

}
