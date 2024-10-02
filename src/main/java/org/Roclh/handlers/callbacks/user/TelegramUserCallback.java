package org.Roclh.handlers.callbacks.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.handlers.CommandHandler;
import org.Roclh.handlers.callbacks.Callback;
import org.Roclh.handlers.callbacks.CallbackData;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramUserCallback implements Callback<PartialBotApiMethod<? extends Serializable>> {
    private final CommandHandler commandHandler;
    @Override
    public PartialBotApiMethod<? extends Serializable> apply(CallbackData callbackData) {
        return null;
    }

    @Override
    public String getName() {
        return "tguser";
    }
}
