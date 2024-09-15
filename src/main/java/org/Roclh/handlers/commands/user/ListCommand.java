package org.Roclh.handlers.commands.user;

import org.Roclh.data.entities.UserModel;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.data.services.UserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListCommand extends AbstractCommand<SendMessage> {
    private final UserService userService;
    public ListCommand(TelegramUserService telegramUserService, UserService userService) {
        super(telegramUserService);
        this.userService = userService;
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        List<UserModel> allUsers = userService.getAllUsers();
        sendMessage.setText(allUsers.size() + " added users:\n" +
                allUsers.stream().map(UserModel::toString)
                        .collect(Collectors.joining("\n")));
        return sendMessage;
    }

    @Override
    public String getHelp() {
        return getCommandNames().get(0) + "\n -- show list of added users";
    }
    @Override
    public List<String> getCommandNames() {
        return List.of("l", "list", "listusers");
    }
}
