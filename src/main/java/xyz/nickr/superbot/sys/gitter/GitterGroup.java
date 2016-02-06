package xyz.nickr.superbot.sys.gitter;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import xyz.nickr.jitter.api.Room;
import xyz.nickr.jitter.api.RoomUser;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupType;
import xyz.nickr.superbot.sys.Message;
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
        return sys;
    }

    @Override
    public String getUniqueId() {
        return room.getID();
    }

    @Override
    public Message sendMessage(String message) {
        return sys.wrap(room.sendMessage(message));
    }

    @Override
    public String getDisplayName() {
        return room.getName();
    }

    @Override
    public GroupType getType() {
        switch (room.getType()) {
            case USER:
                return GroupType.USER;
            default:
                return GroupType.GROUP;
        }
    }

    @Override
    public boolean isAdmin(User u) {
        Optional<RoomUser> ru = room.getUsers().stream().filter(g -> g.getID().equals(u.getUniqueId())).findAny();
        return ru.isPresent() && ru.get().isAdmin();
    }

    @Override
    public Set<User> getUsers() {
        return room.getUsers().stream().map(u -> sys.wrap(u)).collect(Collectors.toSet());
    }

}
