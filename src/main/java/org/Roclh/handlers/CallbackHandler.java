package org.Roclh.handlers;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.handlers.callbacks.Callback;
import org.Roclh.handlers.callbacks.CallbackData;
import org.Roclh.handlers.callbacks.common.TestInlineCallback;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CallbackHandler {
    private final Map<String, Callback<? extends PartialBotApiMethod<? extends Serializable>>> callbacks = new HashMap<>();

    public CallbackHandler(TestInlineCallback inlineCallback) {
        this.callbacks.put(inlineCallback.getName(), inlineCallback);
    }

    public PartialBotApiMethod<? extends Serializable> handleCallbacks(Update update) {
        CallbackData callbackData = CallbackData.from(update);
        if(callbackData == null){
            return SendMessage.builder().text("Failed to parse callback data").chatId(update.getCallbackQuery().getMessage().getChatId()).build();
        }
        return handleCallbacks(callbackData);
    }


    public PartialBotApiMethod<? extends Serializable> handleCallbacks(CallbackData callbackData) {
        long chatId = callbackData.getChatId();
        log.info("Received a callback from id {} with data {}", chatId, callbackData);
        log.info("Existing keys: {}", callbacks.keySet());

        if (callbacks.containsKey(callbackData.getCallbackCommand())) {
            return callbacks.get(callbackData.getCallbackCommand()).apply(callbackData);
        }
        return SendMessage.builder().text("Unknown callback command").chatId(chatId).build();
    }


}
