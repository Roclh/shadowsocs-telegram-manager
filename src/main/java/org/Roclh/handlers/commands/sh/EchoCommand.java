package org.Roclh.handlers.commands.sh;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.commands.CommandData;
import org.Roclh.sh.ScriptRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

import static org.Roclh.sh.ScriptRunner.isShScriptExists;

@Component
@Slf4j
public class EchoCommand extends AbstractCommand<SendMessage> {
    private final String scriptPath = "check_sh_script.sh";
    private final static String scriptContent = """
                #!/bin/bash
                echo 'Hello World!'
                                
                """;

    public EchoCommand(TelegramUserService telegramUserService) {
        super(telegramUserService);
    }


    @Override
    public SendMessage handle(CommandData commandData) {
        long chatId = commandData.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        if (!isShScriptExists(scriptPath)) {
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

    private boolean executeShScript() {
        log.info("Executing echo sh script");
        return ScriptRunner.runCommand(new String[] {"./" + scriptPath});
    }
}
