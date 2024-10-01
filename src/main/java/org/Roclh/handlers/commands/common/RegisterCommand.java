package org.Roclh.handlers.commands.common;

import org.Roclh.bot.TelegramBotStorage;
import org.Roclh.data.Role;
import org.Roclh.data.entities.TelegramUserModel;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class RegisterCommand extends AbstractCommand<SendMessage> {
    public final TelegramBotStorage botStorage;

    public RegisterCommand(TelegramUserService telegramUserService, TelegramBotStorage botStorage) {
        super(telegramUserService);
        this.botStorage = botStorage;
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        boolean isSaved = telegramUserService.saveUser(telegramUserService.getUser(update.getMessage().getFrom().getId())
                .map(user -> {
                    if (user.getRole().prior < Role.USER.prior) {
                        user.setRole(Role.USER);
                    }
                    return user;
                }).orElse(TelegramUserModel.builder()
                        .telegramId(update.getMessage().getFrom().getId())
                        .role(Role.USER)
                        .telegramName(update.getMessage().getFrom().getUserName())
                        .chatId(update.getMessage().getChatId())
                        .build()));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        if (isSaved) {
            sendMessage.setText("Successfully registered!");
            telegramUserService.getUsers(user -> user.getRole().prior >= Role.MANAGER.prior)
                    .stream().filter(user -> user.getChatId() != null)
                    .forEach(user -> botStorage.getTelegramBot().sendMessage(SendMessage.builder()
                            .chatId(user.getChatId())
                            .text("New user " + update.getMessage().getFrom().getUserName() + ":" + update.getMessage().getFrom().getId() + " was registred!")
                            .build()));
        } else {
            sendMessage.setText("Already registered!");
        }
        return sendMessage;
    }

    @Override
    public String inlineName() {
        return "Регистрация";
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
        return List.of("register", inlineName().toLowerCase());
    }
}
