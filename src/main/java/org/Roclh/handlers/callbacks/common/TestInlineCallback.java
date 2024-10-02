package org.Roclh.handlers.callbacks.common;

import lombok.RequiredArgsConstructor;
import org.Roclh.data.Role;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.callbacks.Callback;
import org.Roclh.handlers.callbacks.CallbackData;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TestInlineCallback implements Callback<PartialBotApiMethod<? extends Serializable>> {

    private final TelegramUserService telegramUserService;
    @Override
    public PartialBotApiMethod<? extends Serializable> apply(CallbackData callbackData) {
        return EditMessageText.builder().chatId(callbackData.getChatId())
                .messageId(callbackData.getMessageId())
                .text(callbackData.getCallbackData())
                .build();
    }

    @Override
    public List<InlineKeyboardButton> getCallbackButtonRow() {
        return List.of(InlineKeyboardButton
                .builder()
                .text("Test inline button")
                .callbackData(getName())
                .build());
    }

    @Override
    public boolean isAllowed(Long telegramId) {
        return telegramUserService.isAllowed(telegramId, Role.MANAGER);
    }

    @Override
    public String getName() {
        return "test";
    }
}
