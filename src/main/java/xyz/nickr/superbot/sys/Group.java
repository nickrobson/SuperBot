package xyz.nickr.superbot.sys;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import xyz.nickr.superbot.SuperBotController;
import xyz.nickr.superbot.cmd.LinkCommand;

public interface Group extends Conversable {

    String getDisplayName();

    GroupType getType();

    boolean isAdmin(User u);

    Set<User> getUsers();

    default Map<String, User> getUserMap() {
        return getUsers().stream().collect(Collectors.toMap(u -> u.getUsername(), u -> u));
    }

    default void share(MessageBuilder m) {
        new Thread(() -> {
            try {
                Set<Map.Entry<String, String>> linkedGroups = LinkCommand.getLinkedGroups(this);
                for (Map.Entry<String, String> linkedGroup : linkedGroups) {
                    try {
                        Sys sys = SuperBotController.PROVIDERS.get(linkedGroup.getKey());
                        if (sys != null) {
                            Group o = sys.getGroup(linkedGroup.getValue());
                            if (o != null) {
                                o.sendMessageNoShare(sys.message().raw(m));
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

}
