package xyz.nickr.superchat.cmd;

import in.kyle.ezskypeezlife.api.obj.SkypeConversation;
import in.kyle.ezskypeezlife.api.obj.SkypeUser;

public interface Permission {

    boolean has(SkypeConversation convo, SkypeUser user);

}
