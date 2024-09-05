package org.Roclh.handlers.commands.manager;

import org.Roclh.bot.TelegramBotProperties;
import org.Roclh.data.model.manager.ManagerService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.utils.PropertiesContainer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DeleteManagerCommand extends AbstractCommand {
    private final TelegramBotProperties telegramBotProperties;

    public DeleteManagerCommand(PropertiesContainer propertiesContainer, ManagerService managerService, TelegramBotProperties telegramBotProperties) {
        super(propertiesContainer, managerService);
        this.telegramBotProperties = telegramBotProperties;
    }

    @Override
    public SendMessage handle(Update update) {
        String[] words = update.getMessage().getText().split(" ");
        if (words.length < 2) {
            return SendMessage.builder().chatId(update.getMessage().getChatId()).text("Failed to execute command - not enough arguments").build();
        }
        String managerId = words[1];
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        if (!telegramBotProperties.getDefaultManagerId().equals(managerId) && managerService.delManager(managerId)) {
            sendMessage.setText("Manager with id " + managerId + " was deleted successfully!");
        } else {
            sendMessage.setText("Manager with id " + managerId + " was not deleted. Managers that exists:\n" +
                    propertiesContainer.getProperties(PropertiesContainer.MANAGERS_KEY)
                            .stream().filter(id -> !id.equals(telegramBotProperties.getDefaultManagerId())).collect(Collectors.joining("\n")));
        }
        return sendMessage;
    }

    @Override
    public String getHelp() {
        return String.join("|", getCommandNames().subList(0, 2)) + "\n -- {id} - delete existing manager\n -- {id}: telegram id of a manager user";
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("deletemanager", "delmanager", "delman", "managerdel", "mandel");
    }
}
