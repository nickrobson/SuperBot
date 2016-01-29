package xyz.nickr.superchat.sys.skype;

import java.util.HashMap;
import java.util.Map;

import in.kyle.ezskypeezlife.EzSkype;
import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import xyz.nickr.superchat.sys.Group;
import xyz.nickr.superchat.sys.GroupConfiguration;
import xyz.nickr.superchat.sys.Message;
import xyz.nickr.superchat.sys.MessageBuilder;
import xyz.nickr.superchat.sys.Sys;
import xyz.nickr.superchat.sys.User;

public class SkypeSys implements Sys {

    private final Map<String, GroupConfiguration> configs = new HashMap<>();

    public EzSkype skype;

    public SkypeSys(String username, String password) {
        try {
            SkypeListener listener = new SkypeListener(this);

            skype = new EzSkype(username, password);
            skype.login();
            skype.setErrorHandler(listener);
            skype.getEventManager().registerEvents(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getProviderName() {
        return "Skype";
    }

    @Override
    public MessageBuilder<?> message() {
        return new HtmlMessageBuilder();
    }

    @Override
    public GroupConfiguration getGroupConfiguration(String uniqueId) {
        return configs.get(uniqueId);
    }

    @Override
    public void addGroupConfiguration(GroupConfiguration cfg) {
        configs.put(cfg.getUniqueId(), cfg);
    }

    Group wrap(SkypeConversation group) {
        return new SkypeGroup(this, group);
    }

    User wrap(in.kyle.ezskypeezlife.api.obj.SkypeUser user) {
        return new SkypeUser(this, user);
    }

    Message wrap(in.kyle.ezskypeezlife.api.obj.SkypeMessage message) {
        return new SkypeMessage(this, message);
    }

}
