package org.Roclh.handlers.commands.manager;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.bot.TelegramBotProperties;
import org.Roclh.bot.TelegramBotStorage;
import org.Roclh.data.Role;
import org.Roclh.data.services.LocalizationService;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.messaging.CommandData;
import org.Roclh.handlers.messaging.MessageData;
import org.Roclh.utils.InlineUtils;
import org.Roclh.utils.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class SendNotificationCommand extends AbstractCommand<PartialBotApiMethod<? extends Serializable>> {
    private final TelegramBotStorage telegramBotStorage;
    private final LocalizationService localizationService;
    private final TelegramBotProperties telegramBotProperties;

    public SendNotificationCommand(TelegramUserService telegramUserService, TelegramBotStorage telegramBotStorage, LocalizationService localizationService, TelegramBotProperties telegramBotProperties) {
        super(telegramUserService);
        this.telegramBotStorage = telegramBotStorage;
        this.localizationService = localizationService;
        this.telegramBotProperties = telegramBotProperties;
    }

    // notify ROLE LANG Message
    @Override
    public PartialBotApiMethod<? extends Serializable> handle(CommandData commandData) {
        String[] words = commandData.getCommand().split(" ");
        if (words.length < 4) {
            return MessageUtils.sendMessage(commandData.getMessageData())
                    .text(i18N.get("common.validation.not.enough.argument", 4))
                    .build();
        }
        Role role;
        try {
            role = Role.valueOf(words[1]);
        } catch (IllegalArgumentException e) {
            return MessageUtils.sendMessage(commandData.getMessageData())
                    .text(i18N.get("command.manager.notify.validation.role.not.exists", words[1]))
                    .build();
        }
        String lang = words[2];
        if (!telegramBotProperties.getSupportedLocales().contains(lang)) {
            return MessageUtils.sendMessage(commandData.getMessageData())
                    .text(i18N.get("command.manager.notify.validation.lang.not.supported", lang))
                    .build();
        }
        Locale selectedLocale = Locale.forLanguageTag(lang);
        String message = String.join(" ", Arrays.copyOfRange(words, 3, words.length));
        AtomicInteger counter = new AtomicInteger(0);
        telegramUserService.getUsers(user -> user.getRole().prior >= role.prior &&
                        localizationService.matchesLang(user.getTelegramId(), lang)
                        && !user.getTelegramId().equals(commandData.getMessageData().getTelegramId()))
                .forEach(user -> {
                    MessageData messageData = MessageData.fromUser(user, selectedLocale);
                    telegramBotStorage.getTelegramBot().sendMessage(
                            MessageUtils.sendMessage(messageData)
                                    .text(message)
                                    .replyMarkup(InlineUtils.getDefaultNavigationMarkup(i18N.get("callback.default.navigation.data.back"), "start nl"))
                                    .build()
                    );
                    counter.incrementAndGet();
                });
        return MessageUtils.sendMessage(commandData.getMessageData())
                .text(i18N.get("command.manager.notify.success", counter.get()))
                .build();
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("notify");
    }
}
