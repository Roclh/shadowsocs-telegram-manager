package org.Roclh.handlers.callbacks;

import org.Roclh.handlers.messaging.CallbackData;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public interface Callback<T extends PartialBotApiMethod<? extends Serializable>>{
    T apply(CallbackData callbackData);

    String getName();

    default List<InlineKeyboardButton> getCallbackButtonRow(){
        return List.of(InlineKeyboardButton.builder()
                .text(getName())
                .callbackData(getName())
                .build());
    }

    Callback<T> setI18N(Locale locale);
    default boolean isAllowed(Long telegramId){
        return false;
    }
}
