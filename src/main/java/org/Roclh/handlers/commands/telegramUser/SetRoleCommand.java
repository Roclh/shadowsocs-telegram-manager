package org.Roclh.handlers.commands.telegramUser;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.bot.TelegramBotStorage;
import org.Roclh.data.Role;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.messaging.CommandData;
import org.Roclh.utils.InlineUtils;
import org.Roclh.utils.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Slf4j
@Component
public class SetRoleCommand extends AbstractCommand<SendMessage> {
    private final TelegramBotStorage botStorage;

    public SetRoleCommand(TelegramUserService telegramUserService, TelegramBotStorage botStorage) {
        super(telegramUserService);
        this.botStorage = botStorage;
    }

    @Override
    public SendMessage handle(CommandData commandData) {
        String[] words = commandData.getCommand().split(" ");
        if (words.length < 3) {
            return MessageUtils.sendMessage(commandData.getMessageData()).text("Failed to execute command - not enough arguments").build();
        }
        SendMessage.SendMessageBuilder sendMessage = MessageUtils.sendMessage(commandData.getMessageData());
        sendMessage.replyMarkup(InlineUtils.getDefaultNavigationMarkup(i18N.get("callback.default.navigation.data.back"), "start"));
        long telegramUserId;
        Role role;
        try {
            telegramUserId = Long.parseLong(words[1]);
            role = Role.valueOf(words[2]);
        } catch (NumberFormatException e) {
            log.error("Failed to execute command - wrong number format for id {}", words[1], e);
            sendMessage.text(i18N.get("command.manager.setrole.validation.wrong.telegram.id", words[1]));
            return sendMessage.build();
        } catch (IllegalArgumentException e) {
            log.error("Failed to execute command - unknown role {}", words[2], e);
            sendMessage.text(i18N.get("command.manager.setrole.validation.wrong.role", words[2]));
            return sendMessage.build();
        }
        if (!telegramUserService.exists(telegramUserId)) {
            log.error("Failed to execute command - user with id {} does not exists!", telegramUserId);
            sendMessage.text(i18N.get("command.manager.setrole.validation.id.not.exists", words[2]));
            return sendMessage.build();
        }
        if (!telegramUserService.isAllowed(commandData.getMessageData().getTelegramId(), role)) {
            log.error("Failed to execute command - not enough rights");
            sendMessage.text(i18N.get("command.manager.setrole.validation.is.not.allowed"));
            return sendMessage.build();
        }
        if (!telegramUserService.getUser(telegramUserId).map(user -> {
            user.setRole(role);
            return telegramUserService.saveUser(user);
        }).orElse(false)) {
            log.error("Failed to execute command - failed to update telegram user model");
            sendMessage.text(i18N.get("command.manager.setrole.validation.failed.to.update"));
            return sendMessage.build();
        }
        log.info("Successfully changed user role for user with id {}", telegramUserId);
        sendMessage.text(i18N.get("command.manager.setrole.success", telegramUserId));
        if (role.prior >= Role.MANAGER.prior) {
            if (telegramUserService.getUser(telegramUserId)
                    .map(user -> {
                        if (user.getChatId() != null) {
                            botStorage.getTelegramBot()
                                    .sendMessage(MessageUtils.sendMessage(commandData.getMessageData())
                                            .text("Your role was changed to " + role)
                                            .replyMarkup(InlineUtils.getDefaultNavigationMarkup(i18N.get("command.common.register.start.managment.button"), "start"))
                                            .build());
                            return true;
                        } else {
                            return false;
                        }
                    }).orElse(false)) {
                log.info("User with id {} was notified about role change!", telegramUserId);
            }
        }
        return sendMessage.build();
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("setrole", "role", "man");
    }
}
