package org.Roclh.handlers.commands.user;

import org.Roclh.data.model.manager.ManagerService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.data.model.user.UserService;
import org.Roclh.utils.PropertiesContainer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class DeleteUserCommand extends AbstractCommand<SendMessage> {
    public final UserService userManager;

    public DeleteUserCommand(PropertiesContainer propertiesContainer, ManagerService managerService, UserService userManager) {
        super(propertiesContainer, managerService);
        this.userManager = userManager;
    }

    @Override
    public SendMessage handle(Update update) {
        String[] words = update.getMessage().getText().split(" ");
        if (words.length < 2) {
            return SendMessage.builder().chatId(update.getMessage().getChatId()).text("Failed to execute command - not enough arguments").build();
        }
        String identifier = words[1];
        AtomicBoolean delResult = new AtomicBoolean(false);
        userManager.getUser(identifier)
                .or(() -> userManager.getAllUsers().stream().filter(userModel -> userModel.getTelegramName().equals(identifier)).findFirst())
                .ifPresent(userModel1 -> delResult.set(userManager.delUser(userModel1)));
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        if (delResult.get()) {
            sendMessage.setText("User with identifier " + identifier + " was deleted successfully!");
        } else {
            sendMessage.setText("Failed to delete user with identifier " + identifier);
        }
        return sendMessage;
    }

    @Override
    public String getHelp() {
        return String.join("|", getCommandNames().subList(0, 2)) + " {identifier}\n -- delete user\n -- {identifier}: either id or username of user";
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("del", "delete", "remove", "rem");
    }
}
