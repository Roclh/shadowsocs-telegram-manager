package org.Roclh.sh.scripts;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.sh.ShScript;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;

@Slf4j
public abstract class AbstractShScript<T> implements ShScript<T> {
    protected final String fileName;
    private boolean wasInitialized = false;

    private final String content;

    protected AbstractShScript(String fileName, String content) {
        this.fileName = fileName;
        this.content = content;
    }

    public void init(){
        if(wasInitialized){
            return;
        }
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(content);
            log.info("Changing file permissons for {}", fileName);
            Files.setPosixFilePermissions(Path.of(fileName), PosixFilePermissions.fromString("rwxrwxrwx"));
            wasInitialized = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
