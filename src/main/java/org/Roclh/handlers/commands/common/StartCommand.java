package org.Roclh.handlers.commands.common;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.Role;
import org.Roclh.data.entities.TelegramUserModel;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.CallbackHandler;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.commands.CommandData;
import org.Roclh.utils.InlineUtils;
import org.Roclh.utils.i18n.I18N;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

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
            sendMessage.setText(i18N.get("command.common.start.select.command"));
            sendMessage.setReplyMarkup(getInlineKeyboardButtons(commandData));
        } else {
            telegramUserService.saveUser(TelegramUserModel.builder()
                    .role(Role.GUEST)
                    .telegramId(commandData.getTelegramId())
                    .telegramName(commandData.getTelegramName())
                    .chatId(chatId)
                    .build());
            sendMessage.setText(i18N.get("command.common.start.welcome.message"));
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
        return InlineUtils.getDefaultNavigationMarkup(i18N.get("command.common.start.register.button"), "register");
    }

    private InlineKeyboardMarkup getInlineKeyboardButtons(CommandData commandData) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(CallbackHandler.getAllowedCallbackButtons(commandData.getTelegramId(), commandData.getLocale()));
        return keyboardMarkup;
    }
}
