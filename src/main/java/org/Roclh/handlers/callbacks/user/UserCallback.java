package org.Roclh.handlers.callbacks.user;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.handlers.callbacks.Callback;
import org.Roclh.handlers.callbacks.CallbackData;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;


@Slf4j
@Component
public class UserCallback implements Callback<PartialBotApiMethod<? extends Serializable>> {
    @Override
    public PartialBotApiMethod<? extends Serializable> apply(CallbackData callbackData) {

        return null;
    }

    @Override
    public String getName() {
        return "user";
    }
}
