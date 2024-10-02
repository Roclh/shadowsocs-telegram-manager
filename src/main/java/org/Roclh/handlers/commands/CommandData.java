package org.Roclh.handlers.commands;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.Roclh.handlers.callbacks.CallbackData;
import org.telegram.telegrambots.meta.api.objects.Message;

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
    public static CommandData from(@NonNull Message message) {
        return builder()
                .command(message.getText())
                .telegramName(message.getFrom().getUserName())
                .telegramId(message.getFrom().getId())
                .chatId(message.getChatId())
                .messageId(message.getMessageId())
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
                .build();
    }
}
