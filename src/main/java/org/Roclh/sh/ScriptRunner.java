package org.Roclh.sh;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return runCommandWithResult(command, (output) -> true, () -> false);
    }


    public static boolean runCommand(String[] command, Predicate<String> successCondition) {
        return runCommandWithResult(command, successCondition::test, () -> false);
    }

    @Nullable
    public static String runCommandWithResult(String[] command) {
        return runCommandWithResult(command, (output) -> output);
    }

    @Nullable
    public static <T> T runCommandWithResult(String[] command, Function<String, T> resultProcessor) {
        return runCommandWithResult(command, resultProcessor, () -> null);
    }

    public static <T> T runCommandWithResult(String[] command, Function<String, T> resultProcessor, Supplier<T> fallbackProvider) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        log.info("Executing command {}, command with args: {}", command[0], command);
        try {
            Process p = processBuilder.start()
                    .onExit().get(10, TimeUnit.SECONDS);
            StringBuilder output = new StringBuilder();
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
            stdInput.lines().forEach(line -> {
                log.info(line);
                output.append(line);
            });

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));
            stdError.lines().forEach(line -> {
                log.info(line);
                output.append(line);
            });
            return resultProcessor.apply(output.toString());
        } catch (IOException | ExecutionException | InterruptedException | TimeoutException e) {
            log.error("Failed to execute script " + String.join(" ", command), e);
            return fallbackProvider.get();
        }
    }

    @Getter
    public class CommandOutput {
        SortedMap<Long, String> stdOutputLines = new TreeMap<>();
        SortedMap<Long, String> stdErrorLines = new TreeMap<>();

        public void appendOutput(@NonNull String line) {
            this.stdOutputLines.put(System.currentTimeMillis(), line);
        }

        public void appendError(@NonNull String line) {
            this.stdErrorLines.put(System.currentTimeMillis(), line);
        }

        public String getOutput() {
            return String.join("\n", this.stdOutputLines.values());
        }

        public String getError() {
            return String.join("\n", this.stdErrorLines.values());
        }

        public String get(){
            return String.join("\n",
                    Stream.concat(
                            stdOutputLines.entrySet().stream(),
                            stdErrorLines.entrySet().stream())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                            .values()
                    );
        }
    }
}
