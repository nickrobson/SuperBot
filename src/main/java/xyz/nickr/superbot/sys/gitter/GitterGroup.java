package xyz.nickr.superbot.sys.gitter;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import xyz.nickr.jitter.api.Room;
import xyz.nickr.jitter.api.RoomUser;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupType;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

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
        Message m = this.sys.wrap(this.room.sendMessage(message.build()));
        share(message);
        return m;
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

    @Override
    public boolean isAdmin(User u) {
        Optional<RoomUser> ru = this.room.getUsers().stream().filter(g -> g.getID().equals(u.getUniqueId())).findAny();
        return ru.isPresent() && ru.get().isAdmin();
    }

    @Override
    public Set<User> getUsers() {
        return this.room.getUsers().stream().map(u -> this.sys.wrap(u)).collect(Collectors.toSet());
    }

}
