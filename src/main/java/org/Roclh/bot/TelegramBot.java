package org.Roclh.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.handlers.CallbackHandler;
import org.Roclh.handlers.CommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final TelegramBotProperties telegramBotProperties;
    private final CallbackHandler callbackHandler;
    private final CommandHandler commandsHandler;

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        log.info("Received updates {}", updates);
        Update update = updates.get(0);
        update.setMessage(mergeMessages(updates.stream().map(Update::getMessage).toList()));
        update.setEditedMessage(mergeMessages(updates.stream().map(Update::getEditedMessage).toList()));
        onUpdateReceived(update);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            log.info("Received message {}", ("[id:\"" + update.getMessage().getFrom().getId() + "\"];" +
                    "[username:\"" + update.getMessage().getFrom().getUserName() + "\"];" +
                    "[text:\"" + update.getMessage().getText() + "\"]"));
            if (update.getMessage().hasText()) {
                sendMessage(commandsHandler.handleCommands(update));
            }
        } else if (update.hasCallbackQuery()) {
            sendMessage(callbackHandler.handleCallbacks(update));
        }
    }

    public <T extends Serializable> void sendMessage(PartialBotApiMethod<T> sendMessage) {
        log.info("Trying to send message to telegram client {}", sendMessage);
        if (sendMessage == null) {
            return;
        }
        try {
            if(sendMessage instanceof BotApiMethod<T>){
                execute((BotApiMethod<T>) sendMessage);
            }else if(sendMessage instanceof SendMediaGroup){
                execute((SendMediaGroup) sendMessage);
            }
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return telegramBotProperties.getName();
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getBotToken() {
        return telegramBotProperties.getToken();
    }

    private org.telegram.telegrambots.meta.api.objects.Message mergeMessages(List<org.telegram.telegrambots.meta.api.objects.Message> messages) {
        if (messages.stream().filter(Objects::nonNull).findAny().isEmpty()) {
            return null;
        }
        org.telegram.telegrambots.meta.api.objects.Message newMessage = messages.get(0);
        newMessage.setText(messages.stream().map(org.telegram.telegrambots.meta.api.objects.Message::getText).filter(Objects::nonNull).collect(Collectors.joining("\n")));
        newMessage.setCaption(messages.stream().map(org.telegram.telegrambots.meta.api.objects.Message::getCaption).filter(Objects::nonNull).collect(Collectors.joining("\n")));
        newMessage.setCaptionEntities(messages.stream().flatMap(s -> s.getCaptionEntities() != null ? s.getCaptionEntities().stream() : null)
                .filter(Objects::nonNull).toList());
        newMessage.setPhoto(messages.stream().map(org.telegram.telegrambots.meta.api.objects.Message::getPhoto)
                .filter(Objects::nonNull)
                .map(s -> s.get(s.size() - 1))
                .toList());
        return newMessage;
    }
}
