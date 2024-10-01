package org.Roclh.utils;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;
import org.Roclh.handlers.callbacks.CallbackData;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class InlineUtils {

    @NonNull
    public static InlineKeyboardMarkup getListNavigationMarkup(List<String> selectables,
                                                               String command,
                                                               Function<String, InlineNavigationData> callbackDataConsumer) {
        InlineKeyboardMarkup.InlineKeyboardMarkupBuilder inlineKeyboardMarkupBuilder = InlineKeyboardMarkup.builder();

        List<List<InlineKeyboardButton>> inlineKeyboardButtons = new ArrayList<>();
        for (String selectable : selectables) {
            InlineKeyboardButton inlineKeyboardButton = InlineKeyboardButton.builder()
                    .text(selectable)
                    .callbackData(JsonHandler.toJson(CallbackData.builder()
                            .callbackCommand(command)
                            .callbackData(JsonHandler.toJson(callbackDataConsumer.apply(command)))
                            .build()))
                    .build();
            inlineKeyboardButtons.add(List.of(inlineKeyboardButton));
        }
        return inlineKeyboardMarkupBuilder.keyboard(inlineKeyboardButtons).build();

    }

    @Data
    @Builder
    @Jacksonized
    public static class InlineNavigationData {
        private String value;
        private long index;
    }
}
