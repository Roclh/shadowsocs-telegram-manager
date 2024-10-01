package org.Roclh.handlers.commands.user;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.entities.BandwidthModel;
import org.Roclh.data.entities.UserModel;
import org.Roclh.data.services.BandwidthService;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.data.services.UserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@Component
public class DeleteFlowLimitCommand extends AbstractCommand<SendMessage> {
    private final BandwidthService bandwidthService;
    private final UserService userService;
    public DeleteFlowLimitCommand(TelegramUserService telegramUserService, BandwidthService bandwidthService, UserService userService) {
        super(telegramUserService);
        this.bandwidthService = bandwidthService;
        this.userService = userService;
    }

    @Override
    public SendMessage handle(Update update) {
        String[] words = update.getMessage().getText().split(" ");
        if (words.length < 2) {
            return SendMessage.builder().chatId(update.getMessage().getChatId()).text("Failed to execute command - not enough arguments").build();
        }
        Long telegramId = Long.parseLong(words[1]);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());

        UserModel userModel = userService.getUser(telegramId).orElse(null);
        if(userModel == null){
            log.error("Failed to delete flow limit - user with id {} does not exists", telegramId);
            sendMessage.setText("Failed to delete flow limit - user with id " + telegramId + " does not exists");
            return sendMessage;
        }
        BandwidthModel bandwidthModel = bandwidthService.getRule(telegramId).orElse(null);
        if(bandwidthModel == null){
            log.error("Failed to delete flow limit - bandwidth for user with id {} does not exists", telegramId);
            sendMessage.setText("Failed to delete flow limit - bandwidth for user with id " + telegramId + " does not exists");
            return sendMessage;
        }
        if(!bandwidthService.executeShScriptDeleteBandwidthRule(bandwidthModel)){
            log.error("Failed to delete flow limit - failed to execute sh script for user with id {}", telegramId);
            sendMessage.setText("Failed to delete flow limit - failed to execute sh script for user with id " + telegramId);
            return sendMessage;
        }
        if(!bandwidthService.deleteRule(bandwidthModel)){
            log.error("Failed to delete flow limit - either it not exists or failed to delete with id {}", telegramId);
            sendMessage.setText("Failed to delete flow limit - either it not exists or failed to delete with id " + telegramId);
            return sendMessage;
        }
        log.info("Successfully deleted flow limit for user with id {}", telegramId);
        sendMessage.setText("Successfully deleted flow limit for user with id " + telegramId);
        return sendMessage;
    }

    @Override
    public String getHelp() {
        return String.join("|", getCommandNames().subList(0, 1))+ " {telegramId} \n -- delete flow for user with id {telegramId}";
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("delflow", "flowdel", "flowd", "dflow");
    }
}
