package org.Roclh.handlers.callbacks.common;

import lombok.RequiredArgsConstructor;
import org.Roclh.handlers.CommandHandler;
import org.Roclh.handlers.callbacks.Callback;
import org.Roclh.handlers.callbacks.CallbackData;
import org.Roclh.handlers.commands.CommandData;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class DefaultCallback implements Callback<PartialBotApiMethod<? extends Serializable>> {

    private final CommandHandler commandHandler;
    @Override
    public PartialBotApiMethod<? extends Serializable> apply(CallbackData callbackData) {
        SendMessage sendMessage = (SendMessage) commandHandler.handleCommands(CommandData.from(callbackData));
        return EditMessageText.builder()
                .chatId(callbackData.getChatId())
                .messageId(callbackData.getMessageId())
                .text(sendMessage.getText())
                .replyMarkup((InlineKeyboardMarkup) sendMessage.getReplyMarkup())
                .build();
    }

    @Override
    public String getName() {
        return "default";
    }
}
