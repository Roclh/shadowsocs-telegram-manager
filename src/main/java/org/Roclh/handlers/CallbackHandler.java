package org.Roclh.handlers;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.services.LocalizationService;
import org.Roclh.handlers.callbacks.Callback;
import org.Roclh.handlers.callbacks.CallbackData;
import org.Roclh.handlers.callbacks.common.DefaultCallback;
import org.Roclh.handlers.callbacks.common.TestInlineCallback;
import org.Roclh.handlers.callbacks.user.TelegramUserCallback;
import org.Roclh.handlers.callbacks.user.UserCallback;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CallbackHandler {
    private final String DEFAULT_CALLBACK_KEY;
    private static final Map<String, Callback<? extends PartialBotApiMethod<? extends Serializable>>> callbacks = new HashMap<>();
    private final LocalizationService localizationService;

    public CallbackHandler(DefaultCallback defaultCallback,
                           TestInlineCallback inlineCallback,
                           UserCallback userCallback,
                           TelegramUserCallback telegramUserCallback, LocalizationService localizationService) {
        DEFAULT_CALLBACK_KEY = defaultCallback.getName();
        this.localizationService = localizationService;
        callbacks.put(DEFAULT_CALLBACK_KEY, defaultCallback);
        callbacks.put(inlineCallback.getName(), inlineCallback);
        callbacks.put(userCallback.getName(), userCallback);
        callbacks.put(telegramUserCallback.getName(), telegramUserCallback);
    }

    public PartialBotApiMethod<? extends Serializable> handleCallbacks(Update update) {
        return handleCallbacks(CallbackData.from(update.getCallbackQuery(), localizationService.getOrCreate(update.getCallbackQuery().getFrom().getId())));
    }

    public PartialBotApiMethod<? extends Serializable> handleCallbacks(CallbackData callbackData) {
        long chatId = callbackData.getChatId();
        log.info("Received a callback from id {} with data {}", chatId, callbackData);
        log.info("Existing keys: {}", callbacks.keySet());

        if (callbacks.containsKey(callbackData.getCallbackCommand())) {
            return callbacks.get(callbackData.getCallbackCommand()).setI18N(callbackData.getLocale()).apply(callbackData);
        } else {
            return callbacks.get(DEFAULT_CALLBACK_KEY).setI18N(callbackData.getLocale()).apply(callbackData);
        }
    }

    public static List<List<InlineKeyboardButton>> getAllowedCallbackButtons(Long telegramId, Locale locale){
        return callbacks.values()
                .stream()
                .filter(callback -> callback.isAllowed(telegramId))
                .map(callback -> callback.setI18N(locale))
                .map(Callback::getCallbackButtonRow)
                .collect(Collectors.toList());
    }

}
