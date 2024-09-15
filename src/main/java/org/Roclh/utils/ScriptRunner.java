package org.Roclh.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

@Slf4j
public class ScriptRunner {

    public static boolean isShScriptExists(String scriptPath) {
        return Files.exists(Path.of(scriptPath));
    }
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
            Process p = processBuilder.start().onExit().get();

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
            stdInput.lines().forEach(log::info);

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));
            stdError.lines().forEach(log::info);
            return true;
        } catch (IOException | ExecutionException | InterruptedException e) {
            log.error("Failed to execute script " + String.join(" ", command), e);
            return false;
        }
    }


    public static boolean runCommand(String[] command, Predicate<String> successCondition) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            Process p = processBuilder.start().onExit().get(10, TimeUnit.SECONDS);
            StringBuilder output = new StringBuilder();
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
            stdInput.lines().forEach(line->{
                log.info(line);
                output.append(line);
            });

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));
            stdError.lines().forEach(line->{
                log.info(line);
                output.append(line);
            });
            return successCondition.test(output.toString());
        } catch (IOException | ExecutionException | InterruptedException | TimeoutException e) {
            log.error("Failed to execute script " + String.join(" ", command), e);
            return false;
        }
    }

    @Nullable
    public static String runCommandWithResult(String[] command){
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            Process p = processBuilder.start().onExit().get(10, TimeUnit.SECONDS);
            StringBuilder output = new StringBuilder();
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
            stdInput.lines().forEach(line->{
                log.info(line);
                output.append(line);
            });

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));
            stdError.lines().forEach(line->{
                log.info(line);
                output.append(line);
            });
            return output.toString();
        } catch (IOException | ExecutionException | InterruptedException | TimeoutException e) {
            log.error("Failed to execute script " + String.join(" ", command), e);
            return null;
        }
    }
}
