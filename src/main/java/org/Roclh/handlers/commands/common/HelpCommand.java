package org.Roclh.handlers.commands.common;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.Role;
import org.Roclh.data.entities.TelegramUserModel;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.CallbackHandler;
import org.Roclh.handlers.CommandHandler;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.commands.CommandData;
import org.Roclh.utils.Consts;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Slf4j
@Component
public class HelpCommand extends AbstractCommand<SendMessage> {

    public HelpCommand(TelegramUserService telegramUserService) {
        super(telegramUserService);
    }

    @Override
    public SendMessage handle(CommandData commandData) {
        long chatId = commandData.getChatId();
        SendMessage sendMessage = new SendMessage();
        telegramUserService.saveUser(TelegramUserModel.builder()
                .telegramId(commandData.getTelegramId())
                .telegramName(commandData.getTelegramName())
                .chatId(chatId)
                .role(Role.GUEST)
                .build());
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(Consts.HELLO_TELEGRAM_TEXT + "\n\nAvailable commands:\n" +
                CommandHandler.getCommandNames(commandData.getTelegramId()));
        sendMessage.setReplyMarkup(getInlineKeyboardButtons(commandData));
        return sendMessage;
    }

    @Override
    public boolean isManager(Long userId) {
        return true;
    }


    @Override
    public String getHelp() {
        return String.join("|", getCommandNames().subList(0, 2)) + "\n -- shows available commands";
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("help", "h");
    }

    private InlineKeyboardMarkup getInlineKeyboardButtons(CommandData commandData) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(CallbackHandler.getAllowedCallbackButtons(commandData.getTelegramId()));
        return keyboardMarkup;
    }
}