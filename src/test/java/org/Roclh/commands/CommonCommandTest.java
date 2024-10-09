package org.Roclh.commands;

import org.Roclh.handlers.messaging.CommandData;
import org.Roclh.handlers.messaging.MessageData;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class CommonCommandTest {
    protected final String chatId = "12345";
    @Mock
    protected Update update = Mockito.mock(Update.class);
    protected CommandData commandData = Mockito.mock(CommandData.class);
    protected MessageData messageData = Mockito.mock(MessageData.class);
    @Mock
    protected Message updateMessage = Mockito.mock(Message.class);

    @BeforeEach
    public void prepareMocks(){
        when(messageData.getChatId()).thenReturn(Long.valueOf(chatId));
        when(commandData.getMessageData()).thenReturn(messageData);
        when(update.getMessage()).thenReturn(updateMessage);
        when(updateMessage.getChatId()).thenReturn(Long.valueOf(chatId));
        //set debug property to default
    }

    protected void commonSendMessageValidation(SendMessage sendMessage, String expectedResultMessage){
        assertEquals(chatId, sendMessage.getChatId());
        assertEquals(expectedResultMessage, sendMessage.getText());
    }

    protected void commonSendMessageValidation(SendMessage sendMessage){
        assertEquals(chatId, sendMessage.getChatId());
    }
}
