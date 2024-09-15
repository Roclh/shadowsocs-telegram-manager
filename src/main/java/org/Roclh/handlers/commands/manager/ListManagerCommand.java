package org.Roclh.handlers.commands.manager;

import org.Roclh.data.model.manager.ManagerModel;
import org.Roclh.data.model.manager.ManagerService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.utils.PropertiesContainer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListManagerCommand extends AbstractCommand<SendMessage> {

    public ListManagerCommand(PropertiesContainer propertiesContainer, ManagerService managerService) {
        super(propertiesContainer, managerService);
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        List<ManagerModel> managers = managerService.getManagers();
        sendMessage.setText(managers.size() + " managers that exists:\n" +
                managers.stream().map(
                        managerModel -> managerModel.getTelegramId() + ":" + managerModel.getTelegramName()
                ).collect(Collectors.joining("\n")));
        return sendMessage;
    }

    @Override
    public String inlineName() {
        return "Список менеджеров";
    }

    @Override
    public String getHelp() {
        return String.join("|", getCommandNames().subList(0, 2)) + "\n -- display all existing managers";
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("listmanager", "listman", "manlist", "managerlist", inlineName().replace(' ', '_').toLowerCase());
    }
}
