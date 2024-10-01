package org.Roclh.handlers.commands.user;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.data.services.UserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@Component
public class ChangeUserPasswordCommand extends AbstractCommand<SendMessage> {
    private final UserService userManager;

    public ChangeUserPasswordCommand(TelegramUserService telegramUserService, UserService userManager) {
        super(telegramUserService);
        this.userManager = userManager;
    }

    @Override
    public SendMessage handle(Update update) {
        String[] words = update.getMessage().getText().split(" ");
        if (words.length < 3) {
            return SendMessage.builder().chatId(update.getMessage().getChatId()).text("Failed to execute command - not enough arguments").build();
        }
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        Long telegramId = Long.valueOf(words[1]);
        String password = words[2];

        if (userManager.getUser(telegramId).map(userManager::executeShScriptChangePassword).orElse(false)) {
            log.error("Failed to change password - failed to execute sh script for user with id {}", telegramId);
            sendMessage.setText("Failed to change password - failed to execute sh script for user with id " + telegramId);
            return sendMessage;
        }
        if (!userManager.changePassword(telegramId, password)) {
            log.error("Failed to change password - failed to change password for user with id {}", telegramId);
            sendMessage.setText("Failed to change password - failed to change password for user with id " + telegramId);
            return sendMessage;
        }
        sendMessage.setText("Successfully changed password for user with id " + telegramId);
        return sendMessage;
    }

    @Override
    public String getHelp() {
        return String.join("|", getCommandNames().subList(0, 2)) + " {telegramId} {password}\n -- change user password";
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("changepassword", "chgpwd", "cpwd", "chpwd", "pwd");
    }
}
