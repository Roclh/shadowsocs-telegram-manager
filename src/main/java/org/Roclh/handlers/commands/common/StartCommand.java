package org.Roclh.handlers.commands.common;

import org.Roclh.handlers.CommandHandler;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.commands.Command;
import org.Roclh.utils.Consts;
import org.Roclh.utils.PropertiesContainer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class StartCommand extends AbstractCommand {
    public StartCommand(PropertiesContainer propertiesContainer) {
        super(propertiesContainer);
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(Consts.HELLO_TELEGRAM_TEXT + "\n\nAvailable commands:\n" +
                CommandHandler.getCommandNames(update.getMessage().getFrom().getId().toString()));
        sendMessage.setReplyMarkup(getInlineKeyboardButtons(update));
        return sendMessage;
    }

    @Override
    public boolean isManager(String userId) {
        return true;
    }

    @Override
    public String inlineName() {
        return "Помощь";
    }

    @Override
    public String getHelp() {
        return String.join("|", getCommandNames().subList(0, 3)) + "\n -- shows available commands";
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("start", "help", "h", inlineName().toLowerCase());
    }

    private ReplyKeyboardMarkup getInlineKeyboardButtons(Update update) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        for (Command command : CommandHandler.getCommands(update).stream().filter(command -> Objects.nonNull(command.inlineName())).toList()) {
            KeyboardRow row = new KeyboardRow();
            row.add(KeyboardButton.builder()
                    .text(command.inlineName())
                    .build());
            keyboardRows.add(row);
        }
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }
}
