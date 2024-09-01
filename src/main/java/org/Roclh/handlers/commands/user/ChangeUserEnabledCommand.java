package org.Roclh.handlers.commands.user;

import org.Roclh.data.model.user.UserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.utils.PropertiesContainer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ChangeUserEnabledCommand extends AbstractCommand {
    private final List<String> enableCommands = List.of("enable");
    private final List<String> disableCommands = List.of("disable", "dis");

    private final UserService userService;

    public ChangeUserEnabledCommand(PropertiesContainer propertiesContainer, UserService userService) {
        super(propertiesContainer);
        this.userService = userService;
    }

    @Override
    public SendMessage handle(Update update) {
        String[] words = update.getMessage().getText().split(" ");
        if (words.length < 2) {
            return SendMessage.builder().chatId(update.getMessage().getChatId()).text("Failed to execute command - not enough arguments").build();
        }
        String cmd = words[0];
        String userId = words[1];
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        boolean isEnabled = enableCommands.contains(cmd);
        if (userService.changeUserEnabled(userId, isEnabled)) {
            sendMessage.setText("User was " + (isEnabled ? "enabled" : "disabled"));
        } else {
            sendMessage.setText("User was not " + (isEnabled ? "enabled" : "disabled"));
        }
        return sendMessage;
    }

    @Override
    public String getHelp() {
        return super.getHelp();
    }

    @Override
    public List<String> getCommandNames() {
        return Stream.concat(enableCommands.stream(), disableCommands.stream()).collect(Collectors.toList());
    }
}
