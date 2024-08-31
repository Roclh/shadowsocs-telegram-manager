package org.Roclh.commands;

import org.Roclh.handlers.commands.manager.DebugCommand;
import org.Roclh.utils.PropertiesContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
public class DebugCommandTest extends CommonCommandTest {
    private static final String positiveResponse = "Debug mod is enabled!";
    private static final String negativeResponse = "Debug mod is disabled!";
    @Autowired
    private DebugCommand debugCommand;
    @Autowired
    private PropertiesContainer propertiesContainer;


    @BeforeEach
    public void prepareMocks() {
        when(update.getMessage()).thenReturn(updateMessage);
        when(updateMessage.getChatId()).thenReturn(Long.valueOf(chatId));
        //set debug property to default
        propertiesContainer.setProperty(PropertiesContainer.DEBUG_KEY, false);
    }

    @Test
    public void whenDebugCommandSend_thenPositiveReturned() {
        SendMessage sendMessage = debugCommand.handle(update);
        commonSendMessageValidation(sendMessage, positiveResponse);
        assertEquals(propertiesContainer.getProperty(PropertiesContainer.DEBUG_KEY), String.valueOf(true));
    }

    @Test
    public void whenDebugCommandSendTwice_thenNegativeReturned() {
        debugCommand.handle(update);
        SendMessage sendMessage = debugCommand.handle(update);
        commonSendMessageValidation(sendMessage, negativeResponse);
        assertEquals(propertiesContainer.getProperty(PropertiesContainer.DEBUG_KEY), String.valueOf(false));
    }

    @Test
    public void whenDefault_thenDebugIsFalse() {
        assertEquals(propertiesContainer.getProperty(PropertiesContainer.DEBUG_KEY), String.valueOf(false));
    }

}
