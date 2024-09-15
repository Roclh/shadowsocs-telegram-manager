package org.Roclh.handlers.commands.user;

import org.Roclh.data.services.TelegramUserService;
import org.Roclh.data.services.UserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class DeleteUserCommand extends AbstractCommand<SendMessage> {
    private final UserService userService;
    public DeleteUserCommand(TelegramUserService telegramUserService, UserService userService) {
        super(telegramUserService);
        this.userService = userService;
    }

    @Override
    public SendMessage handle(Update update) {
        String[] words = update.getMessage().getText().split(" ");
        if (words.length < 2) {
            return SendMessage.builder().chatId(update.getMessage().getChatId()).text("Failed to execute command - not enough arguments").build();
        }
        Long id = Long.valueOf(words[1]);

        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        if (userService.deleteUser(id)) {
            sendMessage.setText("User with identifier " + id + " was deleted successfully!");
        } else {
            sendMessage.setText("Failed to delete user with identifier " + id);
        }
        return sendMessage;
    }

    @Override
    public String getHelp() {
        return String.join("|", getCommandNames().subList(0, 2)) + " {id}\n -- delete user\n -- {id}: user telegram id";
    }
    @Override
    public List<String> getCommandNames() {
        return List.of("del", "delete", "rem", "remove");
    }
}
