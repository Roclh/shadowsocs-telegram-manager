package org.Roclh.handlers.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.bot.TelegramBot;
import org.Roclh.data.model.manager.ManagerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatusLogSender {

    private final TelegramBot telegramBot;
    private final ManagerService managerService;
    @Scheduled(fixedRate = 30000)
    public void sendStatus(){
        log.info("Sending scheduled status to managers");
        managerService.getManagers().stream().filter(
                managerModel -> managerModel.getChatId() != null
        ).forEach(managerModel -> {
            final SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(managerModel.getChatId());
            sendMessage.setText("Test scheduled async messages");
            telegramBot.sendMessage(sendMessage);
        });
    }
}
