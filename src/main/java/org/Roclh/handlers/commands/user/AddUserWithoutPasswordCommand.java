package org.Roclh.handlers.commands.user;

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

        if(!telegramUserService.exists(telegramId)){
            sendMessage.setText("User with id " + telegramId + " was not added! User is not registered!");
            return sendMessage;
        }
        TelegramUserModel telegramUserModel = telegramUserService.getUser(telegramId).orElseThrow(() -> new RuntimeException("Illegal state"));
        String password = PasswordGenerator.md5(telegramUserModel.getTelegramName() + ":" + telegramUserModel.getTelegramId())
                .orElseThrow();
        UserModel userModel = UserModel.builder()
                .userModel(telegramUserModel)
                .password(password)
                .usedPort(port)
                .isAdded(true)
                .build();

        if (!userManager.executeShScriptAddUser(userModel)) {
            sendMessage.setText("User with id " + telegramId + " was not added! Failed to execute script");
            return sendMessage;
        }
        if (!userManager.saveUser(userModel)) {
            sendMessage.setText("User with id " + telegramId + "was not added! Failed to update database");
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
