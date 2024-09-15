package org.Roclh.handlers;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.handlers.commands.manager.AddManagerCommand;
import org.Roclh.handlers.commands.sh.EchoCommand;
import org.Roclh.handlers.commands.user.AddUserCommand;
import org.Roclh.handlers.commands.user.AddUserWithoutPasswordCommand;
import org.Roclh.handlers.commands.Command;
import org.Roclh.handlers.commands.manager.DebugCommand;
import org.Roclh.handlers.commands.manager.DeleteManagerCommand;
import org.Roclh.handlers.commands.user.ChangeUserEnabledCommand;
import org.Roclh.handlers.commands.user.ChangeUserPasswordCommand;
import org.Roclh.handlers.commands.user.DeleteUserCommand;
import org.Roclh.handlers.commands.user.ListCommand;
import org.Roclh.handlers.commands.manager.ListManagerCommand;
import org.Roclh.handlers.commands.common.RegisterCommand;
import org.Roclh.handlers.commands.common.StartCommand;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class CommandHandler {

    private static final Map<List<String>, Command<? extends BotApiMethod<?>>> commands = new HashMap<>();

    public CommandHandler(DebugCommand debugCommand,
                          StartCommand startCommand,
                          AddManagerCommand addManagerCommand,
                          DeleteManagerCommand deleteManagerCommand,
                          ListManagerCommand listManagerCommand,
                          RegisterCommand registerCommand,
                          ListCommand listCommand,
                          AddUserWithoutPasswordCommand addUserWithoutPasswordCommand,
                          EchoCommand echoCommand,
                          AddUserCommand addUserCommand,
                          ChangeUserPasswordCommand changeUserPasswordCommand,
                          ChangeUserEnabledCommand changeUserEnabledCommand,
                          DeleteUserCommand deleteUserCommand) {
        commands.put(debugCommand.getCommandNames(), debugCommand);
        commands.put(debugCommand.getCommandNames(), debugCommand);
        commands.put(startCommand.getCommandNames(), startCommand);
        commands.put(addManagerCommand.getCommandNames(), addManagerCommand);
        commands.put(deleteManagerCommand.getCommandNames(), deleteManagerCommand);
        commands.put(listManagerCommand.getCommandNames(), listManagerCommand);
        commands.put(registerCommand.getCommandNames(), registerCommand);
        commands.put(addUserWithoutPasswordCommand.getCommandNames(), addUserWithoutPasswordCommand);
        commands.put(listCommand.getCommandNames(), listCommand);
        commands.put(echoCommand.getCommandNames(), echoCommand);
        commands.put(addUserCommand.getCommandNames(), addUserCommand);
        commands.put(changeUserPasswordCommand.getCommandNames(), changeUserPasswordCommand);
        commands.put(changeUserEnabledCommand.getCommandNames(), changeUserEnabledCommand);
        commands.put(deleteUserCommand.getCommandNames(), deleteUserCommand);
    }

    public BotApiMethod<? extends Serializable> handleCommands(Update update) {
        String messageText = update.getMessage().getText();
        String command = messageText.split(" ")[0];
        long chatId = update.getMessage().getChatId();
        log.info("Received a message from user {} from a chat with id:\"{}\", containing message \"{}\"", update.getMessage().getFrom().getUserName(), chatId, messageText);

        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        String finalCommand = command;
        Command<? extends BotApiMethod<?>> commandHandler = commands.keySet().stream()
                .filter(keys -> keys.contains(finalCommand.toLowerCase()) || keys.contains(messageText.toLowerCase().replace(' ', '_')))
                .findFirst()
                .map(commands::get).orElse(null);
        if (commandHandler != null && commandHandler.isManager(update.getMessage().getFrom().getId().toString())) {
            log.info("Recognized command {}, starting handling", command);
            return commandHandler.handle(update);
        } else {
            return new SendMessage(String.valueOf(chatId), "Unknown command");
        }
    }

    public static String getCommandNames(String telegramId) {
        return commands.values().stream()
                .filter(command -> command.isManager(telegramId))
                .map(Command::getHelp)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n\n"));
    }

    public static List<Command> getCommands(Update update) {
        return commands.values().stream()
                .filter(command -> command.isManager(update.getMessage().getFrom().getId().toString()))
                .collect(Collectors.toList());
    }
}
