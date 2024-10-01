package org.Roclh.handlers.callbacks;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.utils.JsonHandler;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

@Data
@Builder
@Slf4j
@Jacksonized
public class CallbackData {
    @NonNull
    private String callbackCommand;
    @Nullable
    private String callbackData;
    @Nullable
    private Integer messageId;
    @NonNull
    private Long telegramId;
    private Long chatId;
    @NonNull
    private String telegramName;

    public static CallbackData from(Update update){
        try{
            return JsonHandler.toObject(update.getCallbackQuery().getData(), CallbackData.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse update {}", update, e);
            return null;
        }
    }
}
