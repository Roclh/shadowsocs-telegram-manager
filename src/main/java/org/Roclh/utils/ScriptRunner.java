package org.Roclh.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;

@Slf4j
public class ScriptRunner {
    public static void createShScript(String scriptContent, String scriptPath) {
        try (FileWriter fileWriter = new FileWriter(scriptPath)) {
            fileWriter.write(scriptContent);
            log.info("Changing file permissons for {}", scriptPath);
            Files.setPosixFilePermissions(Path.of(scriptPath), PosixFilePermissions.fromString("rwxrwxrwx"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean runCommand(String[] command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            Process p = processBuilder.start();
            return true;
        } catch (IOException e) {
            log.error("Failed to execute script " + String.join(" ", command), e);
            return false;
        }

    }
}
