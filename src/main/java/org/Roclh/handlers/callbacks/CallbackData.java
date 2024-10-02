package org.Roclh.handlers.callbacks;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.handlers.commands.CommandData;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

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
    public static CallbackData from(Update update){
        return CallbackData.builder()
                .callbackCommand(update.getCallbackQuery().getData().split(" ")[0])
                .callbackData(update.getCallbackQuery().getData())
                .telegramId(update.getCallbackQuery().getFrom().getId())
                .telegramName(update.getCallbackQuery().getFrom().getUserName())
                .chatId(update.getCallbackQuery().getMessage().getChatId())
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .build();
    }

    public static CallbackData.CallbackDataBuilder from(CommandData commandData){
        return CallbackData.builder()
                .messageId(commandData.getMessageId())
                .telegramName(commandData.getTelegramName())
                .chatId(commandData.getChatId())
                .telegramId(commandData.getTelegramId())
                .callbackCommand(commandData.getCommand());
    }
}
