package org.Roclh.commands.user;

import org.Roclh.data.Role;
import org.Roclh.data.entities.TelegramUserModel;
import org.Roclh.data.entities.UserModel;
import org.Roclh.handlers.commands.user.AddUserCommand;
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

import static java.util.stream.Collectors.groupingBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class AddUserCommandTest extends CommonUserCommandTest {

    @Autowired
    private AddUserCommand addUserCommand;

    @BeforeEach
    public void init() {
        super.init();
        Mockito.when(userService.saveUser(any(UserModel.class))).thenReturn(true);
        Mockito.when(userService.executeShScriptAddUser(any())).thenReturn(true);
    }

    @Test
    public void whenAddCorrectUser_thenUserIsAdded() {
        UserModel expected = UserModel.builder()
                .isAdded(true)
                .userModel(
                        TelegramUserModel.builder()
                                .role(Role.GUEST)
                                .id(3L)
                                .telegramId(3L)
                                .chatId(null)
                                .telegramName("TestUser3")
                                .build()
                )
                .id(3L)
                .usedPort(10000L)
                .password("qwertyui")
                .build();
        Mockito.when(updateMessage.getText()).thenReturn("add 3 10000 qwertyui");
        SendMessage sendMessage = addUserCommand.handle(update);
        commonSendMessageValidation(sendMessage, "User with id 3 was added successfully!");
        assertEquals(5, users.size());
        assertTrue(users.stream().anyMatch(u -> u.equals(expected)));
    }

    @Test
    public void whenAddIncorrectUser_thenUserIsNotChanged() {
        Mockito.when(updateMessage.getText()).thenReturn("add 8 10000 qwertyui");
        List<UserModel> copy = List.copyOf(users);
        SendMessage sendMessage = addUserCommand.handle(update);
        commonSendMessageValidation(sendMessage, "User with id 8 was not added! Either it exists or failed to add");
        assertUsersWasNotChanged(copy);
    }

    @Test
    public void whenAddNotEnoughArguments_thenReturnWithoutChanges() {
        Mockito.when(updateMessage.getText()).thenReturn("add 1203");
        List<UserModel> copy = List.copyOf(users);
        SendMessage sendMessage = addUserCommand.handle(update);
        commonSendMessageValidation(sendMessage, "Failed to execute command - not enough arguments");
        assertUsersWasNotChanged(copy);
    }

    @Test
    public void whenAddScriptWasNotSuccessfull_thenReturnWithoutChanges() {
        Mockito.when(updateMessage.getText()).thenReturn("add 3 10000 qwertyui");
        Mockito.when(userService.executeShScriptAddUser(any())).thenReturn(false);
        List<UserModel> copy = List.copyOf(users);
        SendMessage sendMessage = addUserCommand.handle(update);
        commonSendMessageValidation(sendMessage, "User with id 3 was not added! Either it exists or failed to add");
        assertUsersWasNotChanged(copy);
    }
}
