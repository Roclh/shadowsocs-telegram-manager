package org.Roclh.handlers.commands.sh;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.messaging.CommandData;
import org.Roclh.sh.scripts.ScreenListScript;
import org.Roclh.utils.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ScreenListCommand extends AbstractCommand<SendMessage> {
    private final ScreenListScript screenListScript;

    public ScreenListCommand(TelegramUserService telegramUserService, ScreenListScript screenListScript) {
        super(telegramUserService);
        this.screenListScript = screenListScript;
    }

    @Override
    public SendMessage handle(CommandData commandData) {
        return MessageUtils.sendMessage(commandData.getMessageData())
                .text(screenListScript.execute()
                        .stream().map(line -> telegramUserService.getUser(Long.valueOf(line.split(":")[1])).orElse(null))
                        .map((userModel) -> {
                            if (userModel == null) {
                                return "Unknown screen\n";
                            }
                            return userModel.getTelegramId() + ":" + userModel.getTelegramName();
                        })
                        .collect(Collectors.joining("\n")))
                .build();
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("screen", "screenlist", "screenls", "lsscreen");
    }
}
