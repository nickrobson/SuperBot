package me.nickrobson.skype.superchat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import me.nickrobson.skype.superchat.cmd.Command;

public class GroupConfiguration {

    private final Set<String> enabledCommands = new HashSet<>();

    private String            groupId         = null;
    private boolean           everythingOn    = false;
    private boolean           showJoinMessage = false;

    public GroupConfiguration(File file) {
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("cmd.")) {
                    enabledCommands.add(line.split("=")[0].substring(4).trim().toLowerCase());
                } else if (line.startsWith("everything-on")) {
                    everythingOn = true;
                } else if (line.startsWith("join-message")) {
                    showJoinMessage = true;
                } else if (line.startsWith("groupId=")) {
                    groupId = line.substring(8);
                }
            }
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getLongGroupId() {
        return groupId;
    }

    public boolean isCommandEnabled(Command cmd) {
        return everythingOn || enabledCommands.contains(cmd.names()[0].toLowerCase());
    }

    public boolean isShowJoinMessage() {
        return everythingOn || showJoinMessage;
    }

}
