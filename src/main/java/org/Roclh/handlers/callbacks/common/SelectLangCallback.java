package org.Roclh.handlers.callbacks.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.bot.TelegramBotProperties;
import org.Roclh.handlers.CommandHandler;
import org.Roclh.handlers.callbacks.AbstractCallback;
import org.Roclh.handlers.callbacks.CallbackData;
import org.Roclh.handlers.commands.CommandData;
import org.Roclh.utils.InlineUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SelectLangCallback extends AbstractCallback<EditMessageText> {
    private final TelegramBotProperties botProperties;
    private final CommandHandler commandHandler;

    @Override
    public EditMessageText apply(CallbackData callbackData) {
        int commandLength = callbackData.getCallbackData().split(" ").length;
        return switch (commandLength) {
            case 1 -> EditMessageText.builder()
                    .messageId(callbackData.getMessageId())
                    .text(i18N.get("callback.common.selectlang.select.lang.message"))
                    .chatId(callbackData.getChatId())
                    .replyMarkup(getSelectLangMarkup(callbackData))
                    .build();
            case 2 -> handleOneArgumentCommand(callbackData);
            default -> EditMessageText.builder()
                    .text(i18N.get("callback.default.navigation.data.error"))
                    .replyMarkup(InlineUtils.getNavigationToStart(callbackData))
                    .chatId(callbackData.getChatId()).build();
        };
    }

    @Override
    public String getName() {
        return "lang";
    }

    @Override
    public List<InlineKeyboardButton> getCallbackButtonRow() {
        return List.of(InlineKeyboardButton.builder()
                .text(i18N.get("callback.common.selectlang.select.button.inline"))
                .callbackData(getName())
                .build()
        );
    }

    @Override
    public boolean isAllowed(Long telegramId) {
        return true;
    }

    private InlineKeyboardMarkup getSelectLangMarkup(CallbackData callbackData) {
        return InlineUtils.getListNavigationMarkup(botProperties
                        .getSupportedLocales()
                        .stream().collect(Collectors.toMap(lang -> i18N.get(lang), lang -> lang)),
                (data) -> callbackData.getCallbackData() + " " + data,
                callbackData.getLocale(),
                () -> "start"
        );
    }

    private EditMessageText handleOneArgumentCommand(CallbackData callbackData) {
        return EditMessageText.builder()
                .text(((SendMessage) commandHandler.handleCommands(CommandData.from(callbackData))).getText())
                .messageId(callbackData.getMessageId())
                .chatId(callbackData.getChatId())
                .replyMarkup(InlineUtils.getNavigationToStart(callbackData))
                .build();
    }

}
