package org.Roclh.handlers.commands.common;

import org.Roclh.bot.TelegramBotStorage;
import org.Roclh.data.Role;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.commands.CommandData;
import org.Roclh.utils.InlineUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Component
public class RegisterCommand extends AbstractCommand<SendMessage> {
    public final TelegramBotStorage botStorage;

    public RegisterCommand(TelegramUserService telegramUserService, TelegramBotStorage botStorage) {
        super(telegramUserService);
        this.botStorage = botStorage;
    }

    @Override
    public SendMessage handle(CommandData commandData) {
        long chatId = commandData.getChatId();
        boolean isSaved = telegramUserService.saveUser(telegramUserService.getUser(commandData.getTelegramId())
                .map(user -> {
                    if (user.getRole().prior < Role.USER.prior) {
                        user.setRole(Role.USER);
                    }
                    return user;
                }).orElse(null));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        if (isSaved) {
            sendMessage.setText(i18N.get("command.common.register.successfully.registred"));
            sendMessage.setReplyMarkup(InlineUtils.getDefaultNavigationMarkup(i18N.get("command.common.register.manage.button"), "start"));
            telegramUserService.getUsers(user -> user.getRole().prior >= Role.MANAGER.prior)
                    .stream().filter(user -> user.getChatId() != null)
                    .forEach(user -> botStorage.getTelegramBot().sendMessage(SendMessage.builder()
                            .chatId(user.getChatId())
                            .text(i18N.get("command.common.register.notify.managers.message",
                                    commandData.getTelegramName(), commandData.getTelegramId()))
                            .replyMarkup(InlineUtils.getDefaultNavigationMarkup(i18N.get("command.common.register.start.managment.button"), "start"))
                            .build()));
        } else {
            sendMessage.setText(i18N.get("command.common.register.already.registred"));
            sendMessage.setReplyMarkup(InlineUtils.getDefaultNavigationMarkup(i18N.get("command.common.register.manage.button"), "start"));
        }
        return sendMessage;
    }


    @Override
    public boolean isAllowed(Long userId) {
        return true;
    }

    @Override
    public String getHelp() {
        return getCommandNames().get(0) + "\n" + i18N.get("command.common.register.help");
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("register");
    }
}
