package me.nickrobson.skype.superchat.perm;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;
import me.nickrobson.skype.superchat.SuperChatPermissions;

public class StringPermission implements Permission {

    private final String perm;

    public StringPermission(String perm) {
        this.perm = perm;
    }

    @Override
    public boolean has(SkypeConversation convo, SkypeUser user) {
        return SuperChatPermissions.has(user.getUsername(), perm);
    }

}
