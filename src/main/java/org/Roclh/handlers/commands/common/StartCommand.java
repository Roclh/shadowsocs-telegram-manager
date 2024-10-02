package org.Roclh.handlers.commands.common;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.Role;
import org.Roclh.data.entities.TelegramUserModel;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.commands.CommandData;
import org.Roclh.utils.InlineUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class StartCommand extends AbstractCommand<SendMessage> {
    public StartCommand(TelegramUserService telegramUserService) {
        super(telegramUserService);
    }

    @Override
    public SendMessage handle(CommandData commandData) {
        long chatId = commandData.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (telegramUserService.exists(commandData.getTelegramId()) && telegramUserService.isAllowed(commandData.getTelegramId(), Role.USER)) {
            sendMessage.setText("Welcome back! Select your next command");
            sendMessage.setReplyMarkup(getInlineKeyboardButtons());
        } else {
            telegramUserService.saveUser(TelegramUserModel.builder()
                    .role(Role.GUEST)
                    .telegramId(commandData.getTelegramId())
                    .telegramName(commandData.getTelegramName())
                    .chatId(chatId)
                    .build());
            sendMessage.setText("Hi! \n\n Nice to meet you! Welcome to PepegaVPN manager bot! To continue, " +
                    "you need to confirm that you agree with terms and conditions by " +
                    "pressing register button below!");
            sendMessage.setReplyMarkup(getGuestKeyboardMarkup());
        }
        return sendMessage;
    }

    @Override
    public boolean isManager(Long userId) {
        return true;
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("start", "s");
    }

    public InlineKeyboardMarkup getGuestKeyboardMarkup() {
        return InlineUtils.getDefaultNavigationMarkup("Register!", "register");
    }

    private InlineKeyboardMarkup getInlineKeyboardButtons() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        keyboardRows.add(List.of(InlineKeyboardButton.builder()
                .text("Test")
                .callbackData("test It's working!").build()));
        keyboardRows.add(List.of(InlineKeyboardButton.builder()
                .text("Manage users")
                .callbackData("user")
                .build()));
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }
}
