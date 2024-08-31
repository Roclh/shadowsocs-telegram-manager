package org.Roclh.handlers.commands.sh;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.utils.PropertiesContainer;
import org.Roclh.utils.ScriptRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@Slf4j
public class EchoCommand extends AbstractCommand {
    private final String scriptPath = "check_sh_script.sh";
    private final static String scriptContent = """
                #!/bin/bash
                echo 'Hello World!'
                                
                """;

    public EchoCommand(PropertiesContainer propertiesContainer) {
        super(propertiesContainer);
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        if (!isShScriptExists()) {
            ScriptRunner.createShScript(scriptContent, scriptPath);
        }
        if (executeShScript()) {
            sendMessage.setText("Executed Echo");
        } else {
            sendMessage.setText("Failed to execute echo");
        }
        return sendMessage;
    }

    @Override
    public String getHelp() {
        return String.join("|", getCommandNames()) + "\n -- executes echo script in server sh";
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("echo", "e");
    }

    private boolean isShScriptExists() {
        return Files.exists(Path.of(scriptPath));
    }

    private boolean executeShScript() {
        log.info("Executing echo sh script");
        return ScriptRunner.runCommand(new String[] {"./" + scriptPath});
    }
}
