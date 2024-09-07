package org.Roclh.commands.user;

import org.Roclh.data.model.user.UserModel;
import org.Roclh.handlers.commands.user.ChangeUserPasswordCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class ChangeUserPasswordCommandTest extends CommonUserCommandTest {
    @Autowired
    private ChangeUserPasswordCommand changeUserPasswordCommand;

    @BeforeEach
    public void init() {
        super.init();
        Mockito.when(userService.saveUser(any(UserModel.class))).thenReturn(true);
        Mockito.when(userService.changeUserEnabled(any(String.class), any(Boolean.class))).thenReturn(true);
        Mockito.when(userService.executeShScriptChangePassword(any(UserModel.class))).thenReturn(true);
    }

    @Test
    public void whenChangeCorrectPassword_thenPasswordIsChanged() {
        String expectedPassword = "qwertyui";
        Mockito.when(updateMessage.getText()).thenReturn("chpwdw 3 " + expectedPassword);
        SendMessage sendMessage = changeUserPasswordCommand.handle(update);
        commonSendMessageValidation(sendMessage, "Successfully changed password for user with id 3");
        assertEquals(expectedPassword, users.get(2).getPassword());
    }

    @Test
    public void whenAddNotEnoughArguments_thenReturnWithoutChanges() {
        Mockito.when(updateMessage.getText()).thenReturn("chpwdw 3");
        List<UserModel> copy = List.copyOf(users);
        SendMessage sendMessage = changeUserPasswordCommand.handle(update);
        commonSendMessageValidation(sendMessage, "Failed to execute command - not enough arguments");
        assertUsersWasNotChanged(copy);
    }

    @Test
    public void whenChangePasswordWithIncorrectId_thenReturnWithoutChanges() {
        Mockito.when(updateMessage.getText()).thenReturn("chpwdw 10 qwertyui");
        List<UserModel> copy = List.copyOf(users);
        SendMessage sendMessage = changeUserPasswordCommand.handle(update);
        commonSendMessageValidation(sendMessage, "Failed to change password for user with id 10");
        assertUsersWasNotChanged(copy);
    }

    @Test
    public void whenShScriptReturnFalse_thenReturnWithoutChanges() {
        Mockito.when(userService.executeShScriptChangePassword(any(UserModel.class))).thenReturn(false);
        Mockito.when(updateMessage.getText()).thenReturn("chpwdw 3 qwertyui");
        List<UserModel> copy = List.copyOf(users);
        SendMessage sendMessage = changeUserPasswordCommand.handle(update);
        commonSendMessageValidation(sendMessage, "Failed to change password for user with id 3");
        assertUsersWasNotChanged(copy);
    }
}