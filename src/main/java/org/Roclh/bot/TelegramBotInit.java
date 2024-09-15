package org.Roclh.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.utils.PropertiesContainer;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBotInit {
    private final TelegramBot telegramBot;
    private final TelegramBotProperties telegramBotProperties;
    private final TelegramBotStorage telegramBotStorage;
    private final PropertiesContainer propertiesContainer;

    @Async
    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        log.info("Starting initialization of telegram bot");
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBot.getOptions().setMaxThreads(telegramBotProperties.getMaxThreads());
            telegramBotsApi.registerBot(telegramBot);
            telegramBotStorage.setTelegramBot(telegramBot);
            propertiesContainer.addProperty(PropertiesContainer.MANAGERS_KEY, telegramBotProperties.getDefaultManagerId());
            propertiesContainer.addProperty(PropertiesContainer.BOT_TOKEN_KEY, telegramBotProperties.getToken());
            log.info("Registered bot successfully");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
