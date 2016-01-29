package xyz.nickr.superchat.sys;

public interface Group extends Conversable {

    String getUniqueId();

    String getDisplayName();

    GroupType getType();

    boolean isAdmin(User u);

}
