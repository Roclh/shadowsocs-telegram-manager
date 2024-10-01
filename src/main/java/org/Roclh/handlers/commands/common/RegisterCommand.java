package org.Roclh.handlers.commands.common;

import org.Roclh.bot.TelegramBotStorage;
import org.Roclh.data.Role;
import org.Roclh.data.entities.TelegramUserModel;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.commands.CommandData;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Component
public class RegisterCommand extends AbstractCommand<SendMessage> {
    public final TelegramBotStorage botStorage;

    public RegisterCommand(TelegramUserService telegramUserService, TelegramBotStorage botStorage) {
        super(telegramUserService);
        this.botStorage = botStorage;
    }

    @Override
    public SendMessage handle(CommandData commandData) {
        long chatId = commandData.getChatId();
        boolean isSaved = telegramUserService.saveUser(telegramUserService.getUser(commandData.getTelegramId())
                .map(user -> {
                    if (user.getRole().prior < Role.USER.prior) {
                        user.setRole(Role.USER);
                    }
                    return user;
                }).orElse(TelegramUserModel.builder()
                        .telegramId(commandData.getTelegramId())
                        .role(Role.USER)
                        .telegramName(commandData.getTelegramName())
                        .chatId(commandData.getChatId())
                        .build()));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        if (isSaved) {
            sendMessage.setText("Successfully registered!");
            telegramUserService.getUsers(user -> user.getRole().prior >= Role.MANAGER.prior)
                    .stream().filter(user -> user.getChatId() != null)
                    .forEach(user -> botStorage.getTelegramBot().sendMessage(SendMessage.builder()
                            .chatId(user.getChatId())
                            .text("New user " + commandData.getTelegramName() + ":" + commandData.getTelegramId() + " was registred!")
                            .build()));
        } else {
            sendMessage.setText("Already registered!");
        }
        return sendMessage;
    }


    @Override
    public boolean isManager(Long userId) {
        return true;
    }

    @Override
    public String getHelp() {
        return getCommandNames().get(0) + "\n -- register for access to personal information";
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("register");
    }
}
