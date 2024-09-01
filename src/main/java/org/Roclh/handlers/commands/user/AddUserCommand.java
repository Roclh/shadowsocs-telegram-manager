package org.Roclh.handlers.commands.user;

import org.Roclh.data.model.user.UserModel;
import org.Roclh.data.model.user.UserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.utils.PropertiesContainer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class AddUserCommand extends AbstractCommand {
    private final UserService userManager;

    public AddUserCommand(PropertiesContainer propertiesContainer, UserService userManager) {
        super(propertiesContainer);
        this.userManager = userManager;
    }

    @Override
    public SendMessage handle(Update update) {
        String[] words = update.getMessage().getText().split(" ");
        if (words.length < 4) {
            return SendMessage.builder().chatId(update.getMessage().getChatId()).text("Failed to execute command - not enough arguments").build();
        }
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));


        String telegramId = words[1];
        String port = words[2];
        String password = words[3];

        UserModel userModel = userManager.getUser(telegramId)
                .orElse(null);
        if (userModel == null) {
            sendMessage.setText("User with id " + telegramId + "was not added! Either it exists or failed to add");
            return sendMessage;
        }
        userModel.setUsedPort(port);
        userModel.setPassword(password);
        userModel.setAdded(true);

        boolean isScriptExecuted = userManager.executeShScriptAddUser(userModel);
        if (isScriptExecuted) {
            userManager.saveUser(userModel);
            sendMessage.setText("User with id " + telegramId + " was added successfully!");
            return sendMessage;
        }
        sendMessage.setText("User with id " + telegramId + " was not added! Either it exists or failed to add");
        return sendMessage;
    }

    @Override
    public String getHelp() {
        return String.join("|", getCommandNames().subList(0, 2)) + " {telegramId} {port} {password}\n -- add user with defined password";
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("adduser", "add", "ad", "addpwd");
    }
}
