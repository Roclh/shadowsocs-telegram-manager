package org.Roclh.handlers.commands;

import lombok.AllArgsConstructor;
import org.Roclh.data.services.TelegramUserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;

@Component
@AllArgsConstructor
public abstract class AbstractCommand<T extends PartialBotApiMethod<? extends Serializable>> implements Command<T> {

    protected final TelegramUserService telegramUserService;

    public boolean isManager(Long userId) {
        return telegramUserService.getManager(userId).isPresent();
    }
}
