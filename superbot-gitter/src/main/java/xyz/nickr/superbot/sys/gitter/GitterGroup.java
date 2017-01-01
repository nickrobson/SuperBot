package xyz.nickr.superbot.sys.gitter;

import xyz.nickr.jitter.api.Room;
import xyz.nickr.superbot.cmd.LinkCommand;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupType;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;

public class GitterGroup implements Group {

    private final GitterSys sys;
    private final Room room;

    public GitterGroup(GitterSys sys, Room room) {
        this.sys = sys;
        this.room = room;
    }

    @Override
    public Sys getProvider() {
        return this.sys;
    }

    @Override
    public String getUniqueId() {
        return this.room.getID();
    }

    @Override
    public Message sendMessage(MessageBuilder message) {
        Message m = this.sys.wrap(this.room.sendMessage(GitterMessageBuilder.build(message)));
        LinkCommand.share(this, message);
        return m;
    }

    @Override
    public Message sendMessageNoShare(MessageBuilder message) {
        return this.sys.wrap(this.room.sendMessage(GitterMessageBuilder.build(message)));
    }

    @Override
    public String getDisplayName() {
        return this.room.getName();
    }

    @Override
    public GroupType getType() {
        switch (this.room.getType()) {
            case USER:
                return GroupType.USER;
            default:
                return GroupType.GROUP;
        }
    }

}
