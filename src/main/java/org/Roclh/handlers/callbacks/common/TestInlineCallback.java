package org.Roclh.handlers.callbacks.common;

import org.Roclh.handlers.callbacks.Callback;
import org.Roclh.handlers.callbacks.CallbackData;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.io.Serializable;

@Component
public class TestInlineCallback implements Callback<PartialBotApiMethod<? extends Serializable>> {
    @Override
    public PartialBotApiMethod<? extends Serializable> apply(CallbackData callbackData) {
        return EditMessageText.builder().chatId(callbackData.getChatId())
                .messageId(callbackData.getMessageId())
                .text(callbackData.getCallbackData() == null ? "Empty" : callbackData.getCallbackData())
                .build();
    }

    @Override
    public String getName() {
        return "test";
    }
}
