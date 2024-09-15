package org.Roclh.handlers.commands.common;

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

    public RegisterCommand(TelegramUserService telegramUserService) {
        super(telegramUserService);
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
