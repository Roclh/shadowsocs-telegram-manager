package org.Roclh.handlers.commands.user;

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

        String telegramId = words[1];
        String port = words[2];
        String password = words[3];

        boolean isAdded = userManager.getUser(telegramId)
                .map(userModel -> {
                    userModel.setUsedPort(port);
                    userModel.setAdded(true);
                    userModel.setPassword(password);
                    userManager.saveUser(userModel);
                    return userModel.isAdded();
                }).orElse(false);
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        if (isAdded) {
            sendMessage.setText("User with id " + telegramId + " was added successfully!");
        } else {
            sendMessage.setText("User with id " + telegramId + "was not added! Either it exists or failed to add");
        }
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
