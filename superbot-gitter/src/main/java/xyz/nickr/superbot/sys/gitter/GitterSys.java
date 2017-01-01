package xyz.nickr.superbot.sys.gitter;

import java.util.concurrent.atomic.AtomicBoolean;

import xyz.nickr.jitter.Jitter;
import xyz.nickr.jitter.api.Message;
import xyz.nickr.jitter.api.Room;
import xyz.nickr.jitter.api.User;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.GroupConfiguration;
import xyz.nickr.superbot.sys.Sys;

public class GitterSys extends Sys {

    Jitter jitter;

    public GitterSys(String token) {
        new Thread(() -> {
            long now = System.currentTimeMillis();
            System.out.println("Loading SuperBot: Gitter");
            if (token == null) {
                System.err.println("Gitter Token Missing!");
                this.jitter = null;
                return;
            }
            this.jitter = Jitter.builder().token(token).build();
            this.jitter.events().register(new GitterListener(this));

            User user = this.jitter.getCurrentUser();

            this.jitter.bayeux().subscribeUserRooms(user);
            this.jitter.bayeux().subscribeUserInformation(user);

            this.jitter.getCurrentRooms().forEach(room -> {
                System.out.println("Found room: " + room.getName());
                this.jitter.bayeux().subscribeRoom(room);
                this.jitter.bayeux().subscribeRoomUsers(room);
                this.jitter.bayeux().subscribeRoomEvents(room);
                this.jitter.bayeux().subscribeRoomMessages(room);
                this.jitter.bayeux().subscribeUserRoomUnread(user, room);
            });
            this.onLoaded();
            System.out.println("Done SuperBot: Gitter (" + (System.currentTimeMillis() - now) + "ms)");
        }).start();
    }

    private void loadRooms() {
        this.jitter.getCurrentRooms().forEach(room -> {
            GroupConfiguration.getGroupConfiguration(this.wrap(room));
        });
    }

    private final AtomicBoolean doneLoading = new AtomicBoolean(false);

    @Override
    public void onLoaded() {
        // only go through on second call to this function
        if (this.doneLoading.getAndSet(true)) {
            this.loadRooms();
        }
    }

    @Override
    public String getName() {
        return "Gitter";
    }

    @Override
    public String prefix() {
        return "+";
    }

    @Override
    public boolean isUIDCaseSensitive() {
        return false;
    }

    @Override
    public String getUserFriendlyName(String uniqueId) {
        return uniqueId;
    }

    @Override
    public Group getGroup(String uniqueId) {
        return wrap(jitter.getRoom(uniqueId));
    }

    GitterMessage wrap(Message message) {
        return new GitterMessage(this, message);
    }

    GitterUser wrap(User user) {
        return new GitterUser(this, user);
    }

    GitterGroup wrap(Room room) {
        return new GitterGroup(this, room);
    }

}
