package xyz.nickr.superbot.sys.gitter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import xyz.nickr.jitter.Jitter;
import xyz.nickr.jitter.api.Message;
import xyz.nickr.jitter.api.Room;
import xyz.nickr.jitter.api.User;
import xyz.nickr.superbot.SuperBotCommands;
import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.sys.GroupConfiguration;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;

public class GitterSys implements Sys {

    private final Map<String, GroupConfiguration> configs = new HashMap<>();

    Jitter jitter;

    public GitterSys(String token) {
        new Thread(() -> {
            long now = System.currentTimeMillis();
            System.out.println("Loading SuperBot: Gitter");
            if (token == null) {
                System.err.println("Gitter Token Missing!");
                jitter = null;
                return;
            }
            jitter = Jitter.builder().token(token).build();
            jitter.onMessage(m -> SuperBotCommands.exec(this, wrap(m.getRoom()), wrap(m.getSender()), wrap(m)));

            User user = jitter.getCurrentUser();

            jitter.bayeux().subscribeUserRooms(user);
            jitter.bayeux().subscribeUserInformation(user);

            jitter.getCurrentRooms().forEach(room -> {
                System.out.println("Found room: " + room.getName());
                jitter.bayeux().subscribeRoom(room);
                jitter.bayeux().subscribeRoomUsers(room);
                jitter.bayeux().subscribeRoomEvents(room);
                jitter.bayeux().subscribeRoomMessages(room);
                jitter.bayeux().subscribeUserRoomUnread(user, room);
            });
            onLoaded();
            System.out.println("Done SuperBot: Gitter (" + (System.currentTimeMillis() - now) + "ms)");
        }).start();
    }

    private void loadRooms() {
        jitter.getCurrentRooms().forEach(room -> {
            SuperBotController.getGroupConfiguration(wrap(room));
        });
    }

    private final AtomicBoolean doneLoading = new AtomicBoolean(false);

    @Override
    public void onLoaded() {
        // only go through on second call to this function
        if (doneLoading.getAndSet(true))
            loadRooms();
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
    public boolean columns() {
        return false;
    }

    @Override
    public MessageBuilder<?> message() {
        return new MarkdownMessageBuilder();
    }

    @Override
    public String getUserFriendlyName(String uniqueId) {
        return uniqueId;
    }

    @Override
    public GroupConfiguration getGroupConfiguration(String uniqueId) {
        return configs.get(uniqueId);
    }

    @Override
    public void addGroupConfiguration(GroupConfiguration cfg) {
        configs.put(cfg.getUniqueId(), cfg);
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
