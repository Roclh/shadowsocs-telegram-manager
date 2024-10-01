package org.Roclh.handlers.commands.manager;

import org.Roclh.bot.TelegramBotProperties;
import org.Roclh.data.Role;
import org.Roclh.data.entities.TelegramUserModel;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.commands.CommandData;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DeleteManagerCommand extends AbstractCommand<SendMessage> {
    private final TelegramBotProperties telegramBotProperties;

    public DeleteManagerCommand(TelegramUserService telegramUserService, TelegramBotProperties telegramBotProperties) {
        super(telegramUserService);
        this.telegramBotProperties = telegramBotProperties;
    }

    @Override
    public SendMessage handle(CommandData commandData) {
        String[] words = commandData.getCommand().split(" ");
        if (words.length < 2) {
            return SendMessage.builder().chatId(commandData.getChatId()).text("Failed to execute command - not enough arguments").build();
        }
        Long managerId = Long.valueOf(words[1]);
        long chatId = commandData.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        if (!telegramBotProperties.getDefaultManagerId().equals(managerId) && telegramUserService.setRole(managerId, Role.USER)) {
            sendMessage.setText("Manager with id " + managerId + " was deleted successfully!");
        } else {
            sendMessage.setText("Manager with id " + managerId + " was not deleted. Managers that exists:\n" +
                    telegramUserService.getUsers(user->user.getRole().equals(Role.MANAGER)).stream()
                            .map(TelegramUserModel::toString).collect(Collectors.joining("\n")));
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
