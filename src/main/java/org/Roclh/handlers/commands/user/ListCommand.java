package org.Roclh.handlers.commands.user;

import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.data.services.UserService;
import org.Roclh.data.entities.UserModel;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListCommand extends AbstractCommand<SendMessage> {
    private final UserService userManager;

    public ListCommand(TelegramUserService telegramUserService, UserService userManager) {
        super(telegramUserService);
        this.userManager = userManager;
    }
    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        List<UserModel> allUsers = userManager.getAllUsers();
        sendMessage.setText(allUsers.size() + " registered users:\n" +
                allUsers.stream().map(UserModel::toString)
                        .collect(Collectors.joining("\n")));
        return sendMessage;
    }

    @Override
    public String inlineName() {
        return "Список пользователей";
    }

    @Override
    public String getHelp() {
        return getCommandNames().get(0) + "\n -- show full list of users";
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("list", "l", inlineName().replace(' ', '_').toLowerCase());
    }
}
