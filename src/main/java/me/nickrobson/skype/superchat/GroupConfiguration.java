package me.nickrobson.skype.superchat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import me.nickrobson.skype.superchat.cmd.Command;

public class GroupConfiguration {

    private final Map<String, Boolean> enabledCommands = new HashMap<>();

    private String            groupId         = null;
    private boolean           everythingOn    = false;
    private boolean           showJoinMessage = false;
    private boolean           showEdited      = false;
    private boolean           disabled        = false;

    private File              file;

    public GroupConfiguration(File file) {
        this.file = file;
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("cmd.")) {
                    String[] spl = line.split("=");
                    enabledCommands.put(spl[0].substring(4).trim().toLowerCase(), Boolean.parseBoolean(spl[1]));
                } else if (line.startsWith("everything-on")) {
                    everythingOn = true;
                } else if (line.startsWith("join-message")) {
                    showJoinMessage = true;
                } else if (line.startsWith("show-edited")) {
                    showEdited = true;
                } else if (line.startsWith("disabled")) {
                    disabled = true;
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
            if (everythingOn) {
                writer.write("everything-on");
                writer.newLine();
            }
            if (showJoinMessage) {
                writer.write("join-message");
                writer.newLine();
            }
            if (showEdited) {
                writer.write("show-edited");
                writer.newLine();
            }
            if (disabled) {
                writer.write("disabled");
                writer.newLine();
            }
            enabledCommands.forEach((c,m) -> {
                try {
                    writer.write("cmd." + c + "=" + m);
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
        return everythingOn || enabledCommands.getOrDefault(cmd.names()[0].toLowerCase(), cmd.alwaysEnabled());
    }

    public boolean isShowJoinMessage() {
        return everythingOn || showJoinMessage;
    }

    public boolean isShowEditedMessages() {
        return showEdited;
    }

    public boolean isDisabled() {
        return disabled;
    }

}
