package org.Roclh.handlers.commands.user;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.data.services.UserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.messaging.CommandData;
import org.Roclh.handlers.messaging.MessageData;
import org.Roclh.sh.scripts.DisableShadowsocksServerScript;
import org.Roclh.utils.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Component
@Slf4j
public class DeleteUserCommand extends AbstractCommand<SendMessage> {
    private final UserService userService;
    private final DisableShadowsocksServerScript disableScript;

    public DeleteUserCommand(TelegramUserService telegramUserService, UserService userService, DisableShadowsocksServerScript disableScript) {
        super(telegramUserService);
        this.userService = userService;
        this.disableScript = disableScript;
    }

    @Override
    public SendMessage handle(CommandData commandData) {
        MessageData messageData = commandData.getMessageData();
        String[] words = commandData.getCommand().split(" ");
        if (words.length < 2) {
            return MessageUtils.sendMessage(commandData.getMessageData()).text("Failed to execute command - not enough arguments").build();
        }
        Long id = Long.valueOf(words[1]);

        long chatId = messageData.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        if (!userService.getUser(id).map(disableScript::execute).orElse(false)) {
            log.error("Failed to delete user with id {}, failed to stop screen", id);
            sendMessage.setText("Failed to delete user with id " + id + ", failed to stop screen");
            return sendMessage;
        }
        if (!userService.deleteUser(id)) {
            log.error("Failed to delete user with identifier {}", id);
            sendMessage.setText("Failed to delete user with identifier " + id);
            return sendMessage;
        }

        log.info("User with identifier {} was deleted successfully!", id);
        sendMessage.setText("User with identifier " + id + " was deleted successfully!");
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
