package org.Roclh.handlers.messaging;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.Locale;

@Data
@Builder
public class MessageData {
    @NonNull
    private String telegramName;
    @NonNull
    private Long telegramId;
    @NonNull
    private Long chatId;
    @Nullable
    private Integer messageId;
    @NonNull
    private Locale locale;
}
