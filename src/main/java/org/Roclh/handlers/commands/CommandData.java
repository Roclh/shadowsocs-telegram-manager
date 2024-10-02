package org.Roclh.handlers.commands;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.Roclh.handlers.callbacks.CallbackData;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Locale;

@Data
@Builder
public class CommandData {
    @NonNull
    private String command;
    @NonNull
    private String telegramName;
    @NonNull
    private Long telegramId;
    @NonNull
    private Long chatId;
    private Integer messageId;
    @NonNull
    private Locale locale;

    @NonNull
    public static CommandData from(@NonNull Message message, Locale locale) {
        return builder()
                .command(message.getText())
                .telegramName(message.getFrom().getUserName())
                .telegramId(message.getFrom().getId())
                .chatId(message.getChatId())
                .messageId(message.getMessageId())
                .locale(locale)
                .build();
    }

    @NonNull
    public static CommandData from(@NonNull CallbackData callbackData){
        return CommandData.builder()
                .command(callbackData.getCallbackData())
                .telegramId(callbackData.getTelegramId())
                .telegramName(callbackData.getTelegramName())
                .chatId(callbackData.getChatId())
                .messageId(callbackData.getMessageId())
                .locale(callbackData.getLocale())
                .build();
    }
}
