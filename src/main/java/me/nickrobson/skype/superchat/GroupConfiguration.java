package me.nickrobson.skype.superchat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

import me.nickrobson.skype.superchat.cmd.Command;

public class GroupConfiguration {

    private final Set<String> enabledCommands = new HashSet<>();

    private String            groupId         = null;
    private boolean           everythingOn    = false;
    private boolean           showJoinMessage = false;
    private boolean           showEdited      = false;

    private File              file;

    public GroupConfiguration(File file) {
        this.file = file;
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("cmd.")) {
                    enabledCommands.add(line.split("=")[0].substring(4).trim().toLowerCase());
                } else if (line.startsWith("everything-on")) {
                    everythingOn = true;
                } else if (line.startsWith("join-message")) {
                    showJoinMessage = true;
                } else if (line.startsWith("show-edited")) {
                    showEdited = true;
                } else if (line.startsWith("groupId=")) {
                    groupId = line.substring(8);
                }
            }
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void save() {
        try {
            BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardOpenOption.CREATE);
            if (groupId != null) {
                writer.write("groupId=" + groupId);
                writer.newLine();
            }
            if (showEdited) {
                writer.write("show-edited");
                writer.newLine();
            }
            if (everythingOn) {
                writer.write("everything-on");
                writer.newLine();
            }
            if (showJoinMessage) {
                writer.write("join-message");
                writer.newLine();
            }
            enabledCommands.forEach(c -> {
                try {
                    writer.write("cmd." + c + "=true");
                    writer.newLine();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
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

    public boolean isShowEditedMessages() {
        return showEdited;
    }

}
