package org.Roclh.handlers;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.services.LocalizationService;
import org.Roclh.handlers.callbacks.Callback;
import org.Roclh.handlers.callbacks.common.GuideCallback;
import org.Roclh.handlers.callbacks.manager.ManagerCallback;
import org.Roclh.handlers.messaging.CallbackData;
import org.Roclh.handlers.callbacks.common.DefaultCallback;
import org.Roclh.handlers.callbacks.common.GetQrCallback;
import org.Roclh.handlers.callbacks.common.HelpCallback;
import org.Roclh.handlers.callbacks.common.SelectLangCallback;
import org.Roclh.handlers.callbacks.common.StartCallback;
import org.Roclh.handlers.callbacks.user.TelegramUserCallback;
import org.Roclh.handlers.callbacks.user.UserCallback;
import org.Roclh.handlers.messaging.MessageData;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CallbackHandler {
    private final String DEFAULT_CALLBACK_KEY;
    private static final Map<String, Callback<? extends PartialBotApiMethod<? extends Serializable>>> callbacks = new LinkedHashMap<>();
    private final LocalizationService localizationService;

    public CallbackHandler(DefaultCallback defaultCallback,
                           StartCallback startCallback,
                           UserCallback userCallback,
                           HelpCallback helpCallback,
                           TelegramUserCallback telegramUserCallback,
                           GetQrCallback getQrCallback,
                           SelectLangCallback selectLangCallback,
                           ManagerCallback managerCallback,
                           LocalizationService localizationService,
                           GuideCallback guideCallback) {
        DEFAULT_CALLBACK_KEY = defaultCallback.getName();
        this.localizationService = localizationService;
        callbacks.put(DEFAULT_CALLBACK_KEY, defaultCallback);
        callbacks.put(startCallback.getName(), startCallback);
        callbacks.put(userCallback.getName(), userCallback);
        callbacks.put(telegramUserCallback.getName(), telegramUserCallback);
        callbacks.put(helpCallback.getName(), helpCallback);
        callbacks.put(getQrCallback.getName(), getQrCallback);
        callbacks.put(selectLangCallback.getName(), selectLangCallback);
        callbacks.put(managerCallback.getName(), managerCallback);
        callbacks.put(guideCallback.getName(), guideCallback);
    }

    public PartialBotApiMethod<? extends Serializable> handleCallbacks(Update update) {
        return handleCallbacks(CallbackData.from(update.getCallbackQuery(), localizationService.getOrCreate(update.getCallbackQuery().getFrom().getId())));
    }

    public PartialBotApiMethod<? extends Serializable> handleCallbacks(CallbackData callbackData) {
        MessageData messageData = callbackData.getMessageData();
        long chatId = messageData.getChatId();
        log.info("Received a callback from id {} with data {}", chatId, callbackData);
        log.info("Existing keys: {}", callbacks.keySet());

        if (callbacks.containsKey(callbackData.getCallbackCommand())) {
            return callbacks.get(callbackData.getCallbackCommand()).setI18N(messageData.getLocale()).apply(callbackData);
        } else {
            return callbacks.get(DEFAULT_CALLBACK_KEY).setI18N(messageData.getLocale()).apply(callbackData);
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
