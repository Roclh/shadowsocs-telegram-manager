package org.Roclh.handlers.commands.user;

import org.Roclh.data.entities.UserModel;
import org.Roclh.data.services.BandwidthService;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.data.services.UserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.commands.CommandData;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListCommand extends AbstractCommand<SendMessage> {
    private final UserService userService;
    private final BandwidthService bandwidthService;

    public ListCommand(TelegramUserService telegramUserService, UserService userService, BandwidthService bandwidthService) {
        super(telegramUserService);
        this.userService = userService;
        this.bandwidthService = bandwidthService;
    }

    @Override
    public SendMessage handle(CommandData commandData) {
        long chatId = commandData.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        List<UserModel> allUsers = userService.getAllUsers();
        sendMessage.setText(allUsers.size() + " added users:\n" +
                allUsers.stream().map(u -> u.toString() + bandwidthService.getRule(u.getUserModel().getTelegramId()).map(r -> "," + r).orElse(""))
                        .collect(Collectors.joining("\n")));
        return sendMessage;
    }

    @Override
    public String getHelp() {
        return getCommandNames().get(0) + "\n -- show list of added users";
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("list", "l", "listusers");
    }
}
