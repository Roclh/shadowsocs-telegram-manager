package org.Roclh.handlers.callbacks.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.Role;
import org.Roclh.data.entities.TelegramUserModel;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.CommandHandler;
import org.Roclh.handlers.callbacks.Callback;
import org.Roclh.handlers.callbacks.CallbackData;
import org.Roclh.handlers.commands.CommandData;
import org.Roclh.utils.InlineUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramUserCallback implements Callback<PartialBotApiMethod<? extends Serializable>> {
    private final CommandHandler commandHandler;
    private final TelegramUserService telegramUserService;

    @Override
    public PartialBotApiMethod<? extends Serializable> apply(CallbackData callbackData) {
        int commandLength = callbackData.getCallbackData().split(" ").length;
        return switch (commandLength) {
            case 1 -> EditMessageText.builder()
                    .messageId(callbackData.getMessageId())
                    .text("Select command")
                    .chatId(callbackData.getChatId())
                    .replyMarkup(getSelectCommandMarkup(callbackData))
                    .build();
            case 2 -> handleOneArgumentCommand(callbackData);
            case 3 -> handleTwoArgumentCommand(callbackData);
            default ->
                    SendMessage.builder().text("Failed to process inline navigation data").chatId(callbackData.getChatId()).build();
        };
    }

    @Override
    public String getName() {
        return "tguser";
    }

    @Override
    public boolean isAllowed(Long telegramId) {
        return telegramUserService.isAllowed(telegramId, Role.MANAGER);
    }

    @Override
    public List<InlineKeyboardButton> getCallbackButtonRow() {
        return List.of(InlineKeyboardButton.builder()
                .text("Manage telegram users")
                .callbackData(getName())
                .build());
    }

    private PartialBotApiMethod<? extends Serializable> handleOneArgumentCommand(CallbackData callbackData) {
        String command = callbackData.getCallbackData().split(" ")[1];
        return switch (command) {
            case "listtg" -> EditMessageText.builder()
                    .messageId(callbackData.getMessageId())
                    .text(getSendMessageCommandResult(callbackData))
                    .chatId(callbackData.getChatId())
                    .replyMarkup(InlineUtils.getDefaultNavigationMarkup("Back", "start"))
                    .build();
            case "deltg" -> EditMessageText.builder()
                    .messageId(callbackData.getMessageId())
                    .text("Select telegram user id to delete")
                    .chatId(callbackData.getChatId())
                    .replyMarkup(getSelectTelegramUserIdMarkup(callbackData, user -> true))
                    .build();
            default -> SendMessage.builder()
                    .chatId(callbackData.getChatId())
                    .text("Failed to parse one argument command")
                    .replyMarkup(InlineUtils.getDefaultNavigationMarkup("Back", "start"))
                    .build();
        };
    }

    private PartialBotApiMethod<? extends Serializable> handleTwoArgumentCommand(CallbackData callbackData) {
        String command = callbackData.getCallbackData().split(" ")[1];
        return switch (command) {
            case "deltg" -> EditMessageText.builder()
                    .messageId(callbackData.getMessageId())
                    .text(getSendMessageCommandResult(callbackData))
                    .chatId(callbackData.getChatId())
                    .replyMarkup(InlineUtils.getDefaultNavigationMarkup("Back", "start"))
                    .build();
            default -> SendMessage.builder()
                    .chatId(callbackData.getChatId())
                    .text("Failed to parse two argument command")
                    .replyMarkup(InlineUtils.getDefaultNavigationMarkup("Back", "start"))
                    .build();
        };
    }


    private InlineKeyboardMarkup getSelectCommandMarkup(CallbackData callbackData) {
        Map<String, String> map = new HashMap<>();
        map.put("List of telegram users", "listtg");
        map.put("Delete tg user", "deltg");
        return InlineUtils.getListNavigationMarkup(map,
                (data) -> callbackData.getCallbackData() + " " + data,
                () -> "start"
        );
    }


    private InlineKeyboardMarkup getSelectTelegramUserIdMarkup(CallbackData callbackData, Predicate<TelegramUserModel> filter) {
        return InlineUtils.getListNavigationMarkup(telegramUserService
                        .getUsers()
                        .stream()
                        .filter(filter)
                        .collect(Collectors.toMap(user -> user.getTelegramName() + ":" + user.getTelegramId(),
                                user -> user.getTelegramId().toString())),
                (data) -> callbackData.getCallbackData() + " " + data,
                () -> callbackData.getCallbackData().substring(0, callbackData.getCallbackData().lastIndexOf(" "))
        );
    }

    private String getSendMessageCommandResult(CallbackData callbackData) {
        CommandData commandData = CommandData.from(callbackData);
        String preparedCommand = commandData.getCommand().substring(commandData.getCommand().indexOf(" ") + 1);
        commandData.setCommand(preparedCommand);
        return ((SendMessage) commandHandler.handleCommands(commandData)).getText();
    }
}