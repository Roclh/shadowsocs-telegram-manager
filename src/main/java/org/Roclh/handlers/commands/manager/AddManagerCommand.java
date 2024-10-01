package org.Roclh.handlers.commands.manager;

import org.Roclh.data.Role;
import org.Roclh.data.entities.TelegramUserModel;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.commands.CommandData;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Component
public class AddManagerCommand extends AbstractCommand<SendMessage> {

    public AddManagerCommand(TelegramUserService telegramUserService) {
        super(telegramUserService);
    }


    @Override
    public SendMessage handle(CommandData commandData) {
        String[] words = commandData.getCommand().split(" ");
        if (words.length < 3) {
            return SendMessage.builder().chatId(commandData.getChatId()).text("Failed to execute command - not enough arguments").build();
        }
        String managerId = words[1];
        String managerUsername = words[2];
        long chatId = commandData.getChatId();
        boolean wasAdded = telegramUserService.saveUser(telegramUserService.getUser(Long.parseLong(managerId))
                .map(user -> {
                    user.setRole(Role.MANAGER);
                    return user;
                })
                .orElse(TelegramUserModel.builder()
                        .telegramId(Long.parseLong(managerId))
                        .telegramName(managerUsername)
                        .chatId(chatId)
                        .role(Role.MANAGER)
                        .build()));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        if (wasAdded) {
            sendMessage.setText("Manager with id " + managerId + " was added successfully!");
        } else {
            sendMessage.setText("Failed to add manager with id " + managerId);
        }
        return sendMessage;
    }

    @Override
    public String getHelp() {
        return String.join("|", getCommandNames().subList(0, 2)) + " {id} {username}\n -- add a new manager \n -- {id}: telegram id of a manager user" +
                "\n -- {username}: name of a manager user";
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("addmanager", "manageradd", "addman", "manadd");
    }
}
