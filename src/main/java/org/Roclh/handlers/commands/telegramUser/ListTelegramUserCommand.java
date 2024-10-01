package org.Roclh.handlers.commands.telegramUser;

import org.Roclh.data.entities.TelegramUserModel;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.commands.CommandData;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListTelegramUserCommand extends AbstractCommand<SendMessage> {

    public ListTelegramUserCommand(TelegramUserService telegramUserService) {
        super(telegramUserService);
    }
    @Override
    public SendMessage handle(CommandData commandData) {
        long chatId = commandData.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        List<TelegramUserModel> allUsers = telegramUserService.getUsers();
        sendMessage.setText(allUsers.size() + " telegram users:\n" +
                allUsers.stream().map(TelegramUserModel::toString)
                        .collect(Collectors.joining("\n")));
        return sendMessage;
    }


    @Override
    public String getHelp() {
        return getCommandNames().get(0) + "\n -- show full list of users";
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("listtg", "ltg");
    }
}
