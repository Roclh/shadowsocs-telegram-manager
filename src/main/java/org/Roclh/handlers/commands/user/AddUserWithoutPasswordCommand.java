package org.Roclh.handlers.commands.user;

import org.Roclh.data.model.manager.ManagerService;
import org.Roclh.data.model.user.UserModel;
import org.Roclh.data.model.user.UserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.utils.PasswordGenerator;
import org.Roclh.utils.PropertiesContainer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class AddUserWithoutPasswordCommand extends AbstractCommand<SendMessage> {
    private final UserService userManager;

    public AddUserWithoutPasswordCommand(PropertiesContainer propertiesContainer, ManagerService managerService, UserService userManager) {
        super(propertiesContainer, managerService);
        this.userManager = userManager;
    }

    @Override
    public SendMessage handle(Update update) {
        String[] words = update.getMessage().getText().split(" ");
        if (words.length < 3) {
            return SendMessage.builder().chatId(update.getMessage().getChatId()).text("Failed to execute command - not enough arguments").build();
        }

        String telegramId = words[1];
        String port = words[2];
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        UserModel userModel = userManager.getUser(telegramId)
                .orElse(null);
        if (userModel == null || userModel.isAdded()) {
            sendMessage.setText("User with id " + telegramId + "was not added! Either user is not registered or already added");
            return sendMessage;
        }

        String password = PasswordGenerator.md5(userModel.getTelegramName() + ":" + userModel.getTelegramId())
                .orElseThrow();
        boolean updateUser = userManager.updateUser(telegramId, port, password, true);
        if (!updateUser) {
            sendMessage.setText("User with id " + telegramId + "was not added! Failed to update database");
            return sendMessage;
        }

        boolean isScriptExecuted = userManager.executeShScriptAddUser(userModel);
        if (isScriptExecuted) {
            sendMessage.setText("User with id " + telegramId + " was added successfully!");
            return sendMessage;
        }
        sendMessage.setText("User with id " + telegramId + " was not added! Failed to execute script");
        return sendMessage;
    }

    @Override
    public String getHelp() {
        return String.join("|", getCommandNames().subList(0, 2)) + " {telegramId} {port}\n -- add user with generated password";
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("addusernopwd", "addnopwd", "nopwd", "addwnopwd", "adduserwithoutpwd");
    }
}
