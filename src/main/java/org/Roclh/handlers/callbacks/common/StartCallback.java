package org.Roclh.handlers.callbacks.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.handlers.CommandHandler;
import org.Roclh.handlers.callbacks.AbstractCallback;
import org.Roclh.handlers.callbacks.CallbackData;
import org.Roclh.handlers.commands.CommandData;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.Serializable;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartCallback extends AbstractCallback<PartialBotApiMethod<? extends Serializable>> {
    private final CommandHandler commandHandler;

    @Override
    public PartialBotApiMethod<? extends Serializable> apply(CallbackData callbackData) {
        String[] command = callbackData.getCallbackData().split(" ");
        SendMessage commandResult = (SendMessage) commandHandler.handleCommands(CommandData.from(callbackData));
        if (command.length == 1) {
            return EditMessageText.builder()
                    .text(commandResult.getText())
                    .chatId(callbackData.getChatId())
                    .messageId(callbackData.getMessageId())
                    .replyMarkup((InlineKeyboardMarkup) commandResult.getReplyMarkup())
                    .build();
        } else {
            return commandResult;
        }
    }

    @Override
    public String getName() {
        return "start";
    }
}