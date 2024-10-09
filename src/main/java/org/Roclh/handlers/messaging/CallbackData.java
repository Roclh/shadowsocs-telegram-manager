package org.Roclh.handlers.messaging;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
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
    @NonNull
    private MessageData messageData;

    @NonNull
    public static CallbackData from(CallbackQuery callbackQuery, Locale locale){
        return CallbackData.builder()
                .callbackCommand(callbackQuery.getData().split(" ")[0])
                .callbackData(callbackQuery.getData())
                .messageData(MessageData.builder()
                        .telegramName(callbackQuery.getFrom().getUserName())
                        .telegramId(callbackQuery.getFrom().getId())
                        .chatId(callbackQuery.getMessage().getChatId())
                        .messageId(callbackQuery.getMessage().getMessageId())
                        .locale(locale)
                        .build())
                .build();
    }

    public static CallbackData.CallbackDataBuilder from(CommandData commandData){
        return CallbackData.builder()
                .callbackCommand(commandData.getCommand())
                .messageData(commandData.getMessageData());
    }
}
