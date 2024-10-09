package org.Roclh.handlers.commands.user;

import org.Roclh.data.entities.UserModel;
import org.Roclh.data.services.BandwidthService;
import org.Roclh.data.services.ContractService;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.data.services.UserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.messaging.CommandData;
import org.Roclh.utils.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListCommand extends AbstractCommand<SendMessage> {
    private final UserService userService;
    private final BandwidthService bandwidthService;
    private final ContractService contractService;

    public ListCommand(TelegramUserService telegramUserService, UserService userService, BandwidthService bandwidthService, ContractService contractService) {
        super(telegramUserService);
        this.userService = userService;
        this.bandwidthService = bandwidthService;
        this.contractService = contractService;
    }

    @Override
    public SendMessage handle(CommandData commandData) {
        List<UserModel> allUsers = userService.getAllUsers();
        return MessageUtils.sendMessage(commandData.getMessageData())
                .text(allUsers.size() + " added users:\n" +
                        allUsers.stream().map(UserModel::toFormattedString)
                                .map(s -> bandwidthService.getRule(commandData.getMessageData().getTelegramId())
                                        .map(b -> s + b.toFormattedString()).orElse(s))
                                .map(s -> contractService.getContract(commandData.getMessageData().getTelegramId())
                                        .map(c -> s + c.toFormattedString()).orElse(s))
                                .collect(Collectors.joining("\n")))
                .build();
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
