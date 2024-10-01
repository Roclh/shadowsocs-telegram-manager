package org.Roclh.handlers.commands;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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

    @NonNull
    public static CommandData from(@NonNull Message message) {
        return builder()
                .command(message.getText())
                .telegramName(message.getFrom().getUserName())
                .telegramId(message.getFrom().getId())
                .chatId(message.getChatId())
                .build();
    }

    @NonNull
    public static CommandData from(@NonNull CallbackQuery callbackQuery, @NonNull String command){
        return CommandData.builder()
                .command(command)
                .telegramId(callbackQuery.getFrom().getId())
                .telegramName(callbackQuery.getFrom().getUserName())
                .chatId(callbackQuery.getMessage().getChatId())
                .build();
    }
}
