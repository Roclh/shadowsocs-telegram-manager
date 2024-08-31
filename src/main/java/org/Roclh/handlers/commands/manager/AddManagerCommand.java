package org.Roclh.handlers.commands.manager;

import org.Roclh.data.model.manager.ManagerModel;
import org.Roclh.data.model.manager.ManagerService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.utils.PropertiesContainer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class AddManagerCommand extends AbstractCommand {
    private final ManagerService managerService;


    public AddManagerCommand(PropertiesContainer propertiesContainer, ManagerService managerService) {
        super(propertiesContainer);
        this.managerService = managerService;
    }

    @Override
    public SendMessage handle(Update update) {
        String[] words = update.getMessage().getText().split(" ");
        if (words.length < 3) {
            return SendMessage.builder().chatId(update.getMessage().getChatId()).text("Failed to execute command - not enough arguments").build();
        }
        String managerId = words[1];
        String managerUsername = words[2];
        boolean wasAdded = managerService.addManager(ManagerModel.builder()
                .telegramId(managerId)
                .telegramName(managerUsername)
                .build());
        long chatId = update.getMessage().getChatId();
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
