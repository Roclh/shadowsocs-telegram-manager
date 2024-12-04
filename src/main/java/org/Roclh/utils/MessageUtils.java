package org.Roclh.utils;

import lombok.NonNull;
import org.Roclh.data.entities.TelegramUserModel;
import org.Roclh.handlers.messaging.MessageData;
import org.springframework.util.Assert;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

/**
 * Class to populate common fields for messages
 */
public class MessageUtils {


    @NonNull
    public static SendMessage.SendMessageBuilder sendMessage(@NonNull TelegramUserModel userModel){
        Assert.notNull(userModel, "User model can't be null!");
        Assert.notNull(userModel.getChatId(), "Telegram user chat id can't be null!");
        return SendMessage.builder()
                .chatId(userModel.getChatId())
                .parseMode("HTML");
    }
    @NonNull
    public static SendMessage.SendMessageBuilder sendMessage(@NonNull MessageData messageData){
        Assert.notNull(messageData, "Message data can't be null!");
        return SendMessage.builder()
                .chatId(messageData.getChatId())
                .parseMode("HTML");
    }

    @NonNull
    public static DeleteMessage deleteMessage(@NonNull MessageData messageData){
        Assert.notNull(messageData, "Message data can't be null!");
        Assert.notNull(messageData.getMessageId(), "Message id is expected not to be null for deletion");
        return DeleteMessage.builder()
                .chatId(messageData.getChatId())
                .messageId(messageData.getMessageId())
                .build();
    }
    @NonNull
    public static EditMessageText.EditMessageTextBuilder editMessage(@NonNull MessageData messageData){
        Assert.notNull(messageData, "Message data can't be null!");
        return EditMessageText.builder()
                .messageId(messageData.getMessageId())
                .chatId(messageData.getChatId())
                .parseMode("HTML");
    }

    @NonNull
    public static SendPhoto.SendPhotoBuilder sendPhoto(@NonNull MessageData messageData){
        Assert.notNull(messageData, "Message data can't be null!");
        return SendPhoto.builder()
                .chatId(messageData.getChatId())
                .parseMode("HTML");
    }

    @NonNull
    public static SendDocument.SendDocumentBuilder sendDocument(@NonNull MessageData messageData){
        Assert.notNull(messageData, "Message data can't be null!");
        return SendDocument.builder()
                .chatId(messageData.getChatId())
                .parseMode("HTML");
    }

    @NonNull
    public static SendMediaGroup.SendMediaGroupBuilder sendMediaGroup(@NonNull MessageData messageData){
        Assert.notNull(messageData, "Message data can't be null!");
        return SendMediaGroup.builder()
                .chatId(messageData.getChatId());
    }
}
