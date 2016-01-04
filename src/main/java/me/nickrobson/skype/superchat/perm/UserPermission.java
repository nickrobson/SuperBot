package me.nickrobson.skype.superchat.perm;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;

public class UserPermission implements Permission {

    @Override
    public boolean has(SkypeConversation convo, SkypeUser user) {
        return true;
    }

}
