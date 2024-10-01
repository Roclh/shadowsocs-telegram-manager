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
public class ScreenListCommand extends AbstractCommand<SendMessage> {
    private final String scriptPath = "screen_list.sh";
    private final static String scriptContent = """
                #!/bin/bash
                screen -ls
                                
                """;
    public ScreenListCommand(TelegramUserService telegramUserService) {
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
        sendMessage.setText(executeShScript());
        return sendMessage;
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("screen", "screenlist", "screenls", "lsscreen");
    }

    private String executeShScript() {
        log.info("Executing screen -ls sh script");
        return ScriptRunner.runCommandWithResult(new String[] {"./" + scriptPath});
    }
}
