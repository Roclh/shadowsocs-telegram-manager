package org.Roclh.handlers.callbacks.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.bot.TelegramBotStorage;
import org.Roclh.data.Role;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.CommandHandler;
import org.Roclh.handlers.callbacks.AbstractCallback;
import org.Roclh.handlers.commands.manager.ExportCsvCommand;
import org.Roclh.handlers.messaging.CallbackData;
import org.Roclh.handlers.messaging.CommandData;
import org.Roclh.utils.InlineUtils;
import org.Roclh.utils.MessageUtils;
import org.Roclh.utils.i18n.EmojiConstants;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ManagerCallback extends AbstractCallback<PartialBotApiMethod<? extends Serializable>> {
    private final CommandHandler commandHandler;
    private final TelegramUserService telegramUserService;
    private final TelegramBotStorage telegramBotStorage;

    @Override
    public PartialBotApiMethod<? extends Serializable> apply(CallbackData callbackData) {
        int commandLength = callbackData.getCallbackData().split(" ").length;
        return switch (commandLength) {
            case 1 -> MessageUtils.editMessage(callbackData.getMessageData())
                    .text(i18N.get("callback.manager.select.command"))
                    .replyMarkup(getSelectCommandMarkup(callbackData))
                    .build();
            case 2 -> handleOneArgumentCommand(callbackData);
            case 3 -> handleTwoArgumentCommand(callbackData);
            default ->
                    MessageUtils.editMessage(callbackData.getMessageData()).text(i18N.get("callback.default.navigation.data.error"))
                            .replyMarkup(InlineUtils.getNavigationToStart(callbackData.getMessageData()))
                            .build();
        };
    }

    @Override
    public String getName() {
        return "manager";
    }

    @Override
    public List<InlineKeyboardButton> getCallbackButtonRow() {
        return List.of(InlineKeyboardButton.builder()
                .text(EmojiConstants.WRENCH + i18N.get("callback.manager.inline.button"))
                .callbackData(getName())
                .build());
    }

    @Override
    public boolean isAllowed(Long telegramId) {
        return telegramUserService.isAllowed(telegramId, Role.MANAGER);
    }

    private InlineKeyboardMarkup getSelectCommandMarkup(CallbackData callbackData) {
        Map<String, String> map = new HashMap<>();
        map.put(i18N.get("callback.manager.inline.button.export.csv"), "csv");
        map.put(EmojiConstants.CLIPBOARD + i18N.get("callback.manager.inline.button.screen.list"), "screen");
        return InlineUtils.getListNavigationMarkup(map,
                (data) -> callbackData.getCallbackData() + " " + data,
                callbackData.getMessageData().getLocale(),
                () -> "start"
        );

    }

    private PartialBotApiMethod<? extends Serializable> handleOneArgumentCommand(CallbackData callbackData) {
        String command = callbackData.getCallbackData().split(" ")[1];
        return switch (command) {
            case "csv" -> MessageUtils.editMessage(callbackData.getMessageData())
                    .text(i18N.get("callback.manager.select.data.type"))
                    .replyMarkup(getSelectCsvDatabaseTypeMarkup(callbackData))
                    .build();
            case "screen" -> MessageUtils.editMessage(callbackData.getMessageData())
                    .text(getSendMessageCommandResult(callbackData))
                    .replyMarkup(InlineUtils.getNavigationToStart(callbackData.getMessageData()))
                    .build();
            default -> MessageUtils.editMessage(callbackData.getMessageData())
                    .text(i18N.get("callback.default.navigation.data.error.parse.one.argument"))
                    .replyMarkup(InlineUtils.getNavigationToStart(callbackData.getMessageData()))
                    .build();
        };
    }

    private PartialBotApiMethod<? extends Serializable> handleTwoArgumentCommand(CallbackData callbackData) {
        String command = callbackData.getCallbackData().split(" ")[1];
        return switch (command) {
            case "csv" -> getExportCsvCommandResult(callbackData);
            default -> MessageUtils.editMessage(callbackData.getMessageData())
                    .text(i18N.get("callback.default.navigation.data.error.parse.one.argument"))
                    .replyMarkup(InlineUtils.getNavigationToStart(callbackData.getMessageData()))
                    .build();
        };
    }

    private InlineKeyboardMarkup getSelectCsvDatabaseTypeMarkup(CallbackData callbackData) {
        return InlineUtils.getListNavigationMarkup(
                Arrays.stream(ExportCsvCommand.FileDataTypes.values())
                        .collect(Collectors.toMap(ExportCsvCommand.FileDataTypes::toString, ExportCsvCommand.FileDataTypes::toString)),
                (type) -> callbackData.getCallbackData() + " " + type,
                callbackData.getMessageData().getLocale(),
                () -> trimLastWord(callbackData.getCallbackData())
        );
    }


    private PartialBotApiMethod<? extends Serializable> getExportCsvCommandResult(CallbackData callbackData) {
        CommandData commandData = CommandData.from(callbackData);
        String preparedCommand = commandData.getCommand().substring(commandData.getCommand().indexOf(" ") + 1);
        commandData.setCommand(preparedCommand);
        PartialBotApiMethod<? extends Serializable> resultMessage = commandHandler.handleCommands(commandData);
        if (resultMessage instanceof SendMessage) {
            ((SendMessage) resultMessage).setReplyMarkup(InlineUtils.getNavigationToStart(callbackData.getMessageData()));
            return resultMessage;
        }
        if (resultMessage instanceof SendDocument) {
            callbackData.setCallbackData("start nl");
            ((SendDocument) resultMessage).setReplyMarkup(InlineUtils.getDefaultNavigationMarkup(callbackData));
            return resultMessage;
        }
        if (resultMessage instanceof SendMediaGroup) {
            telegramBotStorage.getTelegramBot().sendMessage(resultMessage);
            telegramBotStorage.getTelegramBot().sendMessage(
                    MessageUtils.sendMessage(callbackData.getMessageData()).text(i18N.get("callback.manager.export.csv.success.result"))
                            .replyMarkup(InlineUtils.getNavigationToStart(callbackData.getMessageData())).build()
            );
            return MessageUtils.deleteMessage(callbackData.getMessageData());
        }
        throw new RuntimeException("Illegal state");
    }

    private String getSendMessageCommandResult(CallbackData callbackData) {
        CommandData commandData = CommandData.from(callbackData);
        String preparedCommand = commandData.getCommand().substring(commandData.getCommand().indexOf(" ") + 1);
        commandData.setCommand(preparedCommand);
        return ((SendMessage) commandHandler.handleCommands(commandData)).getText();
    }
}
