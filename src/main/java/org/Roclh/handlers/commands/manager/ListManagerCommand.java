package org.Roclh.handlers.commands.manager;

import org.Roclh.data.Role;
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
public class ListManagerCommand extends AbstractCommand<SendMessage> {

    public ListManagerCommand(TelegramUserService telegramUserService) {
        super(telegramUserService);
    }

    @Override
    public SendMessage handle(CommandData commandData) {
        long chatId = commandData.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        List<TelegramUserModel> managers = telegramUserService.getUsers(user -> Role.MANAGER.equals(user.getRole()));
        sendMessage.setText(managers.size() + " managers that exists:\n" +
                managers.stream().map(
                        managerModel -> managerModel.getTelegramId() + ":" + managerModel.getTelegramName()
                ).collect(Collectors.joining("\n")));
        return sendMessage;
    }


    @Override
    public String getHelp() {
        return String.join("|", getCommandNames().subList(0, 2)) + "\n -- display all existing managers";
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("listmanager", "listman", "manlist", "managerlist");
    }
}
