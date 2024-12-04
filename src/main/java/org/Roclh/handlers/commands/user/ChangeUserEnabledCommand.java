package org.Roclh.handlers.commands.user;

import org.Roclh.data.entities.UserModel;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.data.services.UserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.messaging.CommandData;
import org.Roclh.handlers.messaging.MessageData;
import org.Roclh.sh.scripts.DisableShadowsocksServerScript;
import org.Roclh.sh.scripts.EnableDefaultShadowsocksServerScript;
import org.Roclh.utils.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ChangeUserEnabledCommand extends AbstractCommand<SendMessage> {
    private final List<String> enableCommands = List.of("enable");
    private final List<String> disableCommands = List.of("disable", "dis");

    private final UserService userService;
    private final EnableDefaultShadowsocksServerScript enableScript;
    private final DisableShadowsocksServerScript disableScript;

    public ChangeUserEnabledCommand(TelegramUserService telegramUserService, UserService userService, EnableDefaultShadowsocksServerScript enableScript, DisableShadowsocksServerScript disableScript) {
        super(telegramUserService);
        this.userService = userService;
        this.enableScript = enableScript;
        this.disableScript = disableScript;
    }


    @Override
    public SendMessage handle(CommandData commandData) {
        MessageData messageData = commandData.getMessageData();
        String[] words = commandData.getCommand().split(" ");
        if (words.length < 2) {
            return MessageUtils.sendMessage(commandData.getMessageData()).text("Failed to execute command - not enough arguments").build();
        }
        String cmd = words[0];
        Long userId = Long.parseLong(words[1]);
        long chatId = messageData.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        UserModel userModel = userService.getUser(userId).orElse(null);
        if (userModel == null) {
            sendMessage.setText("User does not exists");
            return sendMessage;
        }

        boolean isEnabled = enableCommands.contains(cmd);
        if (changeEnabled(userModel, isEnabled)) {
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

    public boolean changeEnabled(UserModel userModel, boolean enable) {
        if (enable) {
            return enableScript.execute(userModel);
        } else {
            return disableScript.execute(userModel);
        }
    }
}
