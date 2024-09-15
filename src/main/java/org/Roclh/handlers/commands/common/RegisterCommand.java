package org.Roclh.handlers.commands.common;

import org.Roclh.data.model.manager.ManagerService;
import org.Roclh.data.model.user.UserModel;
import org.Roclh.data.model.user.UserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.utils.PropertiesContainer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class RegisterCommand extends AbstractCommand<SendMessage> {
    private final UserService userManager;

    public RegisterCommand(PropertiesContainer propertiesContainer, ManagerService managerService, UserService userManager) {
        super(propertiesContainer, managerService);
        this.userManager = userManager;
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        boolean isSaved = userManager.saveUser(UserModel.builder()
                .telegramId(update.getMessage().getFrom().getId().toString())
                .telegramName(update.getMessage().getFrom().getUserName())
                .chatId(update.getMessage().getChatId())
                .usedPort(null)
                .isAdded(false)
                .build());
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
    public boolean isManager(String userId) {
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
