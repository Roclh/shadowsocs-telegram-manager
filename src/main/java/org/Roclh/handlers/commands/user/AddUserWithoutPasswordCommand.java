package org.Roclh.handlers.commands.user;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.entities.TelegramUserModel;
import org.Roclh.data.entities.UserModel;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.data.services.UserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.utils.PasswordGenerator;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@Component
public class AddUserWithoutPasswordCommand extends AbstractCommand<SendMessage> {
    private final UserService userManager;

    public AddUserWithoutPasswordCommand(TelegramUserService telegramUserService, UserService userManager) {
        super(telegramUserService);
        this.userManager = userManager;
    }

    @Override
    public SendMessage handle(Update update) {
        String[] words = update.getMessage().getText().split(" ");
        if (words.length < 3) {
            return SendMessage.builder().chatId(update.getMessage().getChatId()).text("Failed to execute command - not enough arguments").build();
        }

        Long telegramId = Long.parseLong(words[1]);
        Long port = Long.parseLong(words[2]);
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        TelegramUserModel telegramUserModel = telegramUserService.getUser(telegramId).orElse(null);
        if (telegramUserModel == null) {
            log.error("Failed to add user - Telegram user with id {} does not exists!", telegramId);
            sendMessage.setText("Failed to add user - Telegram user with id " + telegramId + " does not exists!");
            return sendMessage;
        }
        String password = PasswordGenerator.md5(telegramUserModel.getTelegramName() + ":" + telegramUserModel.getTelegramId())
                .orElseThrow();
        UserModel userModel = UserModel.builder()
                .userModel(telegramUserModel)
                .password(password)
                .usedPort(port)
                .isAdded(true)
                .build();

        if (!userManager.executeShScriptAddUser(userModel)) {
            log.error("Failed to add user - failed to execute sh script for user with id {}", telegramId);
            sendMessage.setText("Failed to add user - failed to execute sh script for user with id " + telegramId);
            return sendMessage;
        }
        if (!userManager.saveUser(userModel)) {
            log.error("Failed to add user - failed to save user model with id {}", telegramId);
            sendMessage.setText("Failed to add user - failed to save user model with id " + telegramId);
            return sendMessage;
        }
        sendMessage.setText("User with id " + telegramId + " added successfully!");
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
