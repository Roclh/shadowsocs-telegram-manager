package org.Roclh.handlers.callbacks;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.List;

public interface Callback<T extends PartialBotApiMethod<? extends Serializable>>{
    T apply(CallbackData callbackData);

    String getName();

    default List<InlineKeyboardButton> getCallbackButtonRow(){
        return List.of(InlineKeyboardButton.builder()
                .text(getName())
                .callbackData(getName())
                .build());
    }

    default boolean isAllowed(Long telegramId){
        return false;
    }
}
