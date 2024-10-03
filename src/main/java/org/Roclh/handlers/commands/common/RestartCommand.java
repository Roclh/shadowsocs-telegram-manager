package org.Roclh.handlers.commands.common;


import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.commands.CommandData;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Component
public class RestartCommand extends AbstractCommand<SendMessage> {

    public RestartCommand(TelegramUserService telegramUserService) {
        super(telegramUserService);
    }

    @Override
    public SendMessage handle(CommandData commandData) {
        return null;
    }

    @Override
    public List<String> getCommandNames() {
        return List.of();
    }
}
