package org.Roclh.handlers.commands.telegramUser;

import org.Roclh.data.entities.TelegramUserModel;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.messaging.CommandData;
import org.Roclh.utils.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListTelegramUserCommand extends AbstractCommand<SendMessage> {

    public ListTelegramUserCommand(TelegramUserService telegramUserService) {
        super(telegramUserService);
    }

    @Override
    public SendMessage handle(CommandData commandData) {
        List<TelegramUserModel> allUsers = telegramUserService.getUsers();
        return MessageUtils.sendMessage(commandData.getMessageData())
                .text(allUsers.size() + " telegram users:\n" +
                        allUsers.stream().map(TelegramUserModel::toFormattedString)
                                .collect(Collectors.joining("\n")))
                .build();
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
