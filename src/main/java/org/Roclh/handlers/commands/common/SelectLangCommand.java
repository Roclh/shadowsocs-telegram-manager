package org.Roclh.handlers.commands.common;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.bot.TelegramBotProperties;
import org.Roclh.data.Role;
import org.Roclh.data.services.LocalizationService;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.callbacks.CallbackData;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.commands.CommandData;
import org.Roclh.utils.InlineUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Slf4j
@Component
public class SelectLangCommand extends AbstractCommand<SendMessage> {
    private final LocalizationService localizationService;
    private final TelegramBotProperties telegramBotProperties;

    public SelectLangCommand(TelegramUserService telegramUserService, LocalizationService localizationService, TelegramBotProperties telegramBotProperties) {
        super(telegramUserService);
        this.localizationService = localizationService;
        this.telegramBotProperties = telegramBotProperties;
    }

    @Override
    public SendMessage handle(CommandData commandData) {
        String[] command = commandData.getCommand().split(" ");
        if (command.length < 2) {
            log.error("Failed to execute command - not enough arguments");
            return SendMessage.builder().text(i18N.get("common.validation.not.enough.argument", 2)).chatId(commandData.getChatId())
                    .replyMarkup(InlineUtils.getNavigationToStart(CallbackData.from(commandData).build())).build();
        }
        String locale = command[1];
        if (!telegramBotProperties.getSupportedLocales().contains(locale)) {
            log.error("Failed to execute command - not supported locale {}", locale);
            return SendMessage.builder().text(i18N.get("command.common.selectlang.validation.not.supported.locale", locale)).chatId(commandData.getChatId())
                    .replyMarkup(InlineUtils.getNavigationToStart(CallbackData.from(commandData).build())).build();
        }
        if (!localizationService.setLocale(commandData.getTelegramId(), locale)) {
            log.error("Failed to execute command - either failed for user with id {} to change locale to {}", commandData.getTelegramId(), locale);
            return SendMessage.builder().text(i18N.get("command.common.selectlang.validation.failed.to.change.locale", commandData.getTelegramId(), locale)).chatId(commandData.getChatId())
                    .replyMarkup(InlineUtils.getNavigationToStart(CallbackData.from(commandData).build())).build();
        }
        log.info("Successfully changed locale for user with id {}", commandData.getTelegramId());
        setI18N(localizationService.getOrCreate(commandData.getTelegramId()));
        return SendMessage.builder()
                .text(i18N.get("command.common.selectlang.success", i18N.get(locale)))
                .chatId(commandData.getChatId())
                .build();
    }

    @Override
    public boolean isAllowed(Long userId) {
        return telegramUserService.isAllowed(userId, Role.USER);
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("lang", "chlang", "locale", "chlocale");
    }
}
