package org.Roclh.handlers.commands.user;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.entities.TelegramUserModel;
import org.Roclh.data.entities.UserModel;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.data.services.UserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.commands.CommandData;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Slf4j
@Component
public class AddUserCommand extends AbstractCommand<SendMessage> {
    private final UserService userManager;
    private final TelegramUserService telegramUserService;

    public AddUserCommand(TelegramUserService telegramUserService, UserService userManager, TelegramUserService telegramUserService1) {
        super(telegramUserService);
        this.userManager = userManager;
        this.telegramUserService = telegramUserService1;
    }

    @Override
    public SendMessage handle(CommandData commandData) {
        String[] words = commandData.getCommand().split(" ");
        if (words.length < 4) {
            return SendMessage.builder().chatId(commandData.getChatId()).text("Failed to execute command - not enough arguments").build();
        }
        long chatId = commandData.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));


        Long telegramId = Long.valueOf(words[1]);
        Long port = Long.valueOf(words[2]);
        String password = words[3];

        TelegramUserModel telegramUserModel = telegramUserService.getUser(telegramId)
                .orElse(null);
        if (telegramUserModel == null) {
            log.error("Failed to add user - Telegram user with id {} does not exists!", telegramId);
            sendMessage.setText("Failed to add user - Telegram user with id " + telegramId + " does not exists!");
            return sendMessage;
        }
        UserModel userModel = UserModel.builder()
                .userModel(telegramUserModel)
                .usedPort(port)
                .password(password)
                .isAdded(true)
                .build();
        if (!userManager.executeShScriptAddUser(userModel)) {
            log.error("Failed to add user - failed to execute sh script for user with id {}", telegramId);
            sendMessage.setText("Failed to add user - failed to execute sh script for user with id" + telegramId);
            return sendMessage;
        }
        if(!userManager.saveUser(userModel)){
            log.error("Failed to add user - failed to save user model with id {}", telegramId);
            sendMessage.setText("Failed to add user - failed to save user model with id " + telegramId);
            return sendMessage;
        }
        sendMessage.setText("User with id " + telegramId + " was added successfully!");
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
