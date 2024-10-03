package org.Roclh.handlers.callbacks.common;

import lombok.RequiredArgsConstructor;
import org.Roclh.data.Role;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.CommandHandler;
import org.Roclh.handlers.callbacks.AbstractCallback;
import org.Roclh.handlers.callbacks.CallbackData;
import org.Roclh.handlers.commands.CommandData;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HelpCallback extends AbstractCallback<EditMessageText> {
    private final TelegramUserService telegramUserService;
    private final CommandHandler commandHandler;

    @Override
    public EditMessageText apply(CallbackData callbackData) {
        SendMessage sendMessage = (SendMessage) commandHandler.handleCommands(CommandData.from(callbackData));
        return EditMessageText.builder()
                .chatId(callbackData.getChatId())
                .messageId(callbackData.getMessageId())
                .text(sendMessage.getText())
                .replyMarkup((InlineKeyboardMarkup) sendMessage.getReplyMarkup())
                .build();
    }

    @Override
    public List<InlineKeyboardButton> getCallbackButtonRow() {
        return List.of(InlineKeyboardButton.builder()
                .text(i18N.get("callback.common.help.inline.button"))
                .callbackData(getName())
                .build());
    }

    @Override
    public boolean isAllowed(Long telegramId) {
        return telegramUserService.isAllowed(telegramId, Role.USER);
    }

    @Override
    public String getName() {
        return "help";
    }
}