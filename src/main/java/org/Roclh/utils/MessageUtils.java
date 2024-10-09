package org.Roclh.utils;

import org.Roclh.handlers.messaging.MessageData;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

/**
 * Class to populate common fields for messages
 */
public class MessageUtils {

    public static SendMessage.SendMessageBuilder sendMessage(MessageData messageData){
        return SendMessage.builder()
                .chatId(messageData.getChatId())
                .parseMode("HTML");
    }

    public static EditMessageText.EditMessageTextBuilder editMessage(MessageData messageData){
        return EditMessageText.builder()
                .messageId(messageData.getMessageId())
                .chatId(messageData.getChatId())
                .parseMode("HTML");
    }

    public static SendPhoto.SendPhotoBuilder sendPhoto(MessageData messageData){
        return SendPhoto.builder()
                .chatId(messageData.getChatId())
                .parseMode("HTML");
    }

    public static SendDocument.SendDocumentBuilder sendDocument(MessageData messageData){
        return SendDocument.builder()
                .chatId(messageData.getChatId())
                .parseMode("HTML");
    }

    public static SendMediaGroup.SendMediaGroupBuilder sendMediaGroup(MessageData messageData){
        return SendMediaGroup.builder()
                .chatId(messageData.getChatId());
    }
}
