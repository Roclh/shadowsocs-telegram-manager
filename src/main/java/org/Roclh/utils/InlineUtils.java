package org.Roclh.utils;

import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class InlineUtils {

    @NonNull
    public static InlineKeyboardMarkup getListNavigationMarkup(Map<String, String> selectables,
                                                               Function<String, String> callbackDataConsumer) {
        return getListNavigationMarkup(selectables, callbackDataConsumer, () -> null);
    }

    @NonNull
    public static InlineKeyboardMarkup getListNavigationMarkup(Map<String, String> selectables,
                                                               Function<String, String> callbackDataConsumer,
                                                               Supplier<String> redoCallbackSupplier) {
        InlineKeyboardMarkup.InlineKeyboardMarkupBuilder inlineKeyboardMarkupBuilder = InlineKeyboardMarkup.builder();

        List<List<InlineKeyboardButton>> inlineKeyboardButtons = new ArrayList<>();
        for (Map.Entry<String, String> selectable : selectables.entrySet()) {
            InlineKeyboardButton inlineKeyboardButton = InlineKeyboardButton.builder()
                    .text(selectable.getKey())
                    .callbackData(callbackDataConsumer.apply(selectable.getValue()))
                    .build();
            inlineKeyboardButtons.add(List.of(inlineKeyboardButton));
        }
        List<InlineKeyboardButton> lastRow = getDefaultRedoLastChangeCommandRow("Back", redoCallbackSupplier);
        if(lastRow != null){
            inlineKeyboardButtons.add(lastRow);
        }
        return inlineKeyboardMarkupBuilder.keyboard(inlineKeyboardButtons).build();
    }

    public static InlineKeyboardMarkup getDefaultNavigationMarkup(@NonNull String text, @NonNull String callbackData) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        InlineKeyboardButton backToStartButton = InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
        inlineKeyboardButtons.add(backToStartButton);
        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtons));
        return inlineKeyboardMarkup;
    }

    @Nullable
    public static List<InlineKeyboardButton> getDefaultRedoLastChangeCommandRow(String redoButtonText, Supplier<String> callbackDataSupplier) {
        if (callbackDataSupplier.get() == null) {
            return null;
        }
        InlineKeyboardButton inlineKeyboardButton = InlineKeyboardButton.builder()
                .text(redoButtonText)
                .callbackData(callbackDataSupplier.get())
                .build();
        return List.of(inlineKeyboardButton);
    }
}
