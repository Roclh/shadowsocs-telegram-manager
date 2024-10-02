package org.Roclh.handlers.callbacks;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.handlers.commands.CommandData;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Locale;

@Data
@Builder
@Slf4j
@Jacksonized
public class CallbackData {
    @NonNull
    private String callbackCommand;
    @NonNull
    private String callbackData;
    @Nullable
    private Integer messageId;
    @NonNull
    private Long telegramId;
    private Long chatId;
    private String telegramName;
    @NonNull
    private Locale locale;

    @NonNull
    public static CallbackData from(CallbackQuery callbackQuery, Locale locale){
        return CallbackData.builder()
                .callbackCommand(callbackQuery.getData().split(" ")[0])
                .callbackData(callbackQuery.getData())
                .telegramId(callbackQuery.getFrom().getId())
                .telegramName(callbackQuery.getFrom().getUserName())
                .chatId(callbackQuery.getMessage().getChatId())
                .messageId(callbackQuery.getMessage().getMessageId())
                .locale(locale)
                .build();
    }

    public static CallbackData.CallbackDataBuilder from(CommandData commandData){
        return CallbackData.builder()
                .messageId(commandData.getMessageId())
                .telegramName(commandData.getTelegramName())
                .chatId(commandData.getChatId())
                .telegramId(commandData.getTelegramId())
                .callbackCommand(commandData.getCommand())
                .locale(commandData.getLocale());
    }
}
