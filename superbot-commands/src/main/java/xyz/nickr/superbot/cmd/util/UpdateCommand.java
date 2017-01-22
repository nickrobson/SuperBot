package xyz.nickr.superbot.cmd.util;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.client.methods.HttpPost;
import xyz.nickr.superbot.cmd.Command;
import xyz.nickr.superbot.cmd.Permission;
import xyz.nickr.superbot.sys.Group;
import xyz.nickr.superbot.sys.Message;
import xyz.nickr.superbot.sys.MessageBuilder;
import xyz.nickr.superbot.sys.Sys;
import xyz.nickr.superbot.sys.User;

/**
 * @author Nick Robson
 */
public class UpdateCommand implements Command {

    private final String GIT_URL = "https://github.com/nickrobson/SuperBot.git";
    private final File LAST_PULL_FILE = new File(".version");

    @Override
    public String[] names() {
        return new String[]{ "update" };
    }

    @Override
    public String[] help(User user, boolean userchat) {
        return new String[]{ "", "updates the bot" };
    }

    @Override
    public Permission perm() {
        return string("admin.update");
    }

    @Override
    public boolean userchat() {
        return true;
    }

    @Override
    public void exec(Sys sys, User user, Group group, String used, String[] args, Message message) {
        File buildDir = new File("build");
        try {
            ProcessBuilder gitProcBuilder = new ProcessBuilder();
            if (buildDir.exists()) {
                gitProcBuilder.command("git", "pull").directory(buildDir);
            } else {
                gitProcBuilder.command("git", "clone", GIT_URL, buildDir.toString());
            }
            Process gitProc = gitProcBuilder.start();
            int gitExit = gitProc.waitFor();
            if (gitExit != 0) {
                group.sendMessage(sys.message().escaped("Git process exited with a non-zero exit code."));
                return;
            }
            Process gitHashProc = new ProcessBuilder()
                    .command("git", "log", "-n", "1", "--pretty=format:%H:%s")
                    .directory(buildDir)
                    .start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(gitHashProc.getInputStream()));
            String[] parts = reader.readLine().split(":", 2);
            reader.close();
            try {
                List<String> lines = Files.readAllLines(LAST_PULL_FILE.toPath());
                if (parts[0].equals(lines.get(0))) {
                    group.sendMessage(sys.message().escaped("No new updates."));
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            group.sendMessage(sys.message()
                    .escaped("Successfully pulled from repo.")
                    .newLine()
                    .bold(z -> z.escaped("Latest commit:"))
                    .escaped(" " + parts[1] + " (")
                    .italic(z -> z.escaped(parts[0].substring(0, 6)))
                    .escaped(")"));
            Files.write(LAST_PULL_FILE.toPath(), Collections.singletonList(parts[0]), StandardOpenOption.CREATE);
            Process mvnProc = new ProcessBuilder()
                    .command("mvn", "package")
                    .directory(buildDir)
                    .redirectErrorStream(true)
                    .start();
            int mvnExit = mvnProc.waitFor();
            List<String> updateOutput = new LinkedList<>();
            BufferedReader gitReader = new BufferedReader(new InputStreamReader(gitProc.getInputStream()));
            BufferedReader mvnReader = new BufferedReader(new InputStreamReader(mvnProc.getInputStream()));
            String line;
            updateOutput.add("\n=== GIT ===\n");
            while ((line = gitReader.readLine()) != null)
                updateOutput.add(line);
            updateOutput.add("\n=== MAVEN ===\n");
            while ((line = mvnReader.readLine()) != null)
                updateOutput.add(line);
            String pasteId = null;
            try {
                pasteId = Unirest.post("https://nickr.xyz/cgi/paste.py")
                        .field("lang", "text")
                        .field("text", String.join("\n", updateOutput))
                        .asString()
                        .getBody();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            if (mvnExit != 0) {
                group.sendMessage(sys.message().escaped("Maven process exited with a non-zero exit code."));
                return;
            } else {
                MessageBuilder mb = sys.message().escaped("Successfully built new version.");
                if (pasteId != null)
                    mb.newLine().escaped("Git and maven output: ").link("https://nickr.xyz/paste/" + pasteId);
                group.sendMessage(mb);
            }
            File targetDir = new File(buildDir, "superbot-main");
            targetDir = new File(targetDir, "target");
            if (targetDir.isDirectory()) {
                File[] matching = targetDir.listFiles(fn -> fn.getName().endsWith("-with-dependencies.jar"));
                if (matching == null || matching.length == 0) {
                    group.sendMessage(sys.message().escaped("No jar files ending with '-with-dependencies.jar'"));
                    return;
                }
                File jarFile = matching[0];
                Files.copy(jarFile.toPath(), new File("SuperBot.jar").toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.exit(0);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
