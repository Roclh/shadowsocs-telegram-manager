package org.Roclh.handlers.commands.user;

import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.data.model.user.UserService;
import org.Roclh.utils.PasswordGenerator;
import org.Roclh.utils.PropertiesContainer;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AddUserWithoutPasswordCommand extends AbstractCommand {
    private final UserService userManager;

    public AddUserWithoutPasswordCommand(PropertiesContainer propertiesContainer, UserService userManager) {
        super(propertiesContainer);
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
        String password = PasswordGenerator.md5(words[2]).map(pwd -> new String(ArrayUtils.toPrimitive(pwd), Charset.forName("windows-1251")))
                .orElseThrow();

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
        return String.join("|", getCommandNames().subList(0, 2)) + " {telegramId} {port}\n -- add user with generated password";
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("addusernopwd", "addnopwd", "nopwd", "addwnopwd", "adduserwithoutpwd");
    }
}
