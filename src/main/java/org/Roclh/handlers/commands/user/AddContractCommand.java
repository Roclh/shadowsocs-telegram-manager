package org.Roclh.handlers.commands.user;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.entities.ContractModel;
import org.Roclh.data.services.ContractService;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.data.services.UserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.commands.CommandData;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
@Slf4j
public class AddContractCommand extends AbstractCommand<SendMessage> {
    private final ContractService contractService;
    private final UserService userService;
    public AddContractCommand(TelegramUserService telegramUserService, ContractService contractService, UserService userService) {
        super(telegramUserService);
        this.contractService = contractService;
        this.userService = userService;
    }

    @Override
    public SendMessage handle(CommandData commandData) {
        String[] words = commandData.getCommand().split(" ");
        if (words.length < 4) {
            return SendMessage.builder().chatId(commandData.getChatId()).text("Failed to execute command - not enough arguments").build();
        }
        Long telegramId = Long.valueOf(words[1]);

        long chatId = commandData.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        try{
            startDateTime = LocalDateTime.parse(words[2], DateTimeFormatter.ISO_DATE);
            endDateTime = LocalDateTime.parse(words[3], DateTimeFormatter.ISO_DATE);
        }catch (DateTimeParseException e){
            log.error("Failed to parse date time for one of the dates: {} or {}", words[1], words[2], e);
            sendMessage.setText("Failed to parse date time for one of the dates: " + words[2] + " or " + words[3]);
            return sendMessage;
        }

        if(!userService.getUser(telegramId).map(user -> contractService.saveContract(ContractModel.builder().userModel(user)
                .startDate(startDateTime)
                .endDate(endDateTime)
                .build())).orElse(false)){
            log.error("Failed to save a contract for user with id {}", telegramId);
            sendMessage.setText("Failed to save a contract for user with id " + telegramId);
            return sendMessage;
        }
        sendMessage.setText("Successfully saved a contract for user with id " + telegramId);
        return sendMessage;
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("contract", "setContract", "contractset");
    }
}
