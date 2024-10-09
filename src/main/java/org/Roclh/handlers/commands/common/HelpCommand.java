package org.Roclh.handlers.commands.common;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.Role;
import org.Roclh.data.entities.TelegramUserModel;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.CallbackHandler;
import org.Roclh.handlers.CommandHandler;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.messaging.CommandData;
import org.Roclh.handlers.messaging.MessageData;
import org.Roclh.utils.MessageUtils;
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
        MessageData messageData = commandData.getMessageData();
        long chatId = messageData.getChatId();
        if(!telegramUserService.exists(messageData.getTelegramId())){
            telegramUserService.saveUser(TelegramUserModel.builder()
                    .telegramId(messageData.getTelegramId())
                    .telegramName(messageData.getTelegramName())
                    .chatId(chatId)
                    .role(Role.GUEST)
                    .build());
        }
        return MessageUtils.sendMessage(messageData)
                .text(i18N.get("command.common.help.text") +
                        CommandHandler.getCommandNames(messageData.getTelegramId(), messageData.getLocale()))
                .replyMarkup(getInlineKeyboardButtons(commandData.getMessageData()))
                .build();
    }

    @Override
    public boolean isAllowed(Long userId) {
        return telegramUserService.isAllowed(userId, Role.USER);
    }


    @Override
    public String getHelp() {
        return String.join("|", getCommandNames().subList(0, 2)) + "\n";
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("help", "h");
    }

    private InlineKeyboardMarkup getInlineKeyboardButtons(MessageData messageData) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(CallbackHandler.getAllowedCallbackButtons(messageData.getTelegramId(), messageData.getLocale()));
        return keyboardMarkup;
    }
}
