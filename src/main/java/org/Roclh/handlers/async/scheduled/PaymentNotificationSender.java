package org.Roclh.handlers.async.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.bot.TelegramBotStorage;
import org.Roclh.data.services.ContractService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentNotificationSender {
    private final HashMap<Long, Timer> scheduledNotifications = new HashMap<>();


    private final TelegramBotStorage telegramBotStorage;
    private final ContractService contractService;

    @Scheduled(cron = "0 */5 * * * *")
    public void setupNotificationsAboutPayments() {
        log.info("Setting up notifications about payments");
        contractService.getAllContracts().stream()
                .filter(contractModel -> !scheduledNotifications.containsKey(contractModel.getUserModel().getUserModel().getChatId())).forEach(
                contractModel -> {
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            SendMessage sendMessage = new SendMessage();
                            log.info("Notifying user with id {} about ending of the contract", contractModel.getUserModel().getUserModel().getTelegramId());
                            sendMessage.setText("Пришло время платить денюжку! Наш договор заканчивается " + contractModel.getEndDate());
                            telegramBotStorage.getTelegramBot().sendMessage(sendMessage);
                        }
                    }, Date.from(contractModel.getEndDate().atZone(ZoneId.systemDefault()).toInstant()));
                    log.info("Scheduled a notification for user with id {} at {}", contractModel.getUserModel().getUserModel().getTelegramId(), contractModel.getEndDate());
                    scheduledNotifications.put(contractModel.getUserModel().getUserModel().getChatId(), timer);
                }
        );
    }
}
