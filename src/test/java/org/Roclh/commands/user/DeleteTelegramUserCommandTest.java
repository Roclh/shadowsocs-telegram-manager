package org.Roclh.commands.user;

import org.Roclh.data.entities.UserModel;
import org.Roclh.handlers.commands.user.DeleteUserCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
public class DeleteTelegramUserCommandTest extends CommonUserCommandTest {

    @Autowired
    private DeleteUserCommand deleteUserCommand;
    @BeforeEach
    public void init() {
        super.init();
    }
    @Test
    public void whenDelUser_thenUsersSizeReduced(){
        Mockito.when(commandData.getCommand()).thenReturn("del 0");
        List<UserModel> userModels = List.copyOf(userService.getAllUsers());
        UserModel deletedModel = userModels.get(0);
        SendMessage sendMessage = deleteUserCommand.handle(commandData);
        List<UserModel> trimmedUserModels = userService.getAllUsers();
        assertNotEquals(userModels.size(), trimmedUserModels.size());
        assertFalse(trimmedUserModels.contains(deletedModel));
        assertEquals("User with identifier " + deletedModel.getUserModel().getTelegramId() + " was deleted successfully!", sendMessage.getText());
    }
}
