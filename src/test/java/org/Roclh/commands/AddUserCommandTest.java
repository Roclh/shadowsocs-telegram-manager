package org.Roclh.commands;

import org.Roclh.data.model.user.UserModel;
import org.Roclh.data.model.user.UserService;
import org.Roclh.handlers.commands.user.AddUserCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class AddUserCommandTest extends CommonCommandTest {
    private final List<UserModel> users = new ArrayList<>();

    @Autowired
    private AddUserCommand addUserCommand;
    @MockBean
    private UserService userService;

    @BeforeEach
    public void init() {
        Mockito.when(userService.getAllUsers()).thenReturn(users);
        Mockito.when(userService.saveUser(any(UserModel.class))).thenReturn(true);
        Mockito.when(userService.getUser(any(String.class))).then((invocationOnMock ->
                users.stream().filter(u -> u.getTelegramId().equals(invocationOnMock.getArgument(0, String.class)))
                        .findFirst()));
        Mockito.when(userService.executeShScriptAddUser(any())).thenReturn(true);
        //init users before each test
        users.clear();
        users.addAll(List.of(
                UserModel.builder().id(1L).telegramId("1").telegramName("TestUser1").usedPort("10000").isAdded(true).build(),
                UserModel.builder().id(2L).telegramId("2").telegramName("TestUser2").usedPort(null).isAdded(false).build(),
                UserModel.builder().id(3L).telegramId("3").telegramName("TestUser3").usedPort(null).isAdded(false).build(),
                UserModel.builder().id(4L).telegramId("4").telegramName("TestUser4").usedPort("10003").isAdded(true).build(),
                UserModel.builder().id(5L).telegramId("5").telegramName("TestUser5").usedPort("10003").isAdded(true).build()
        ));
    }

    @Test
    public void whenAddCorrectUser_thenUserIsAdded() {
        UserModel expected = UserModel.builder()
                .isAdded(true)
                .chatId(null)
                .id(3L)
                .telegramId("3")
                .telegramName("TestUser3")
                .usedPort("10000")
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

    private void assertUsersWasNotChanged(List<UserModel> usersCopy){
        assertEquals(users.size(), usersCopy.size());
        assertEquals(users.stream().collect(groupingBy(k -> k, counting())), usersCopy.stream().collect(groupingBy(k -> k, counting())));
    }
}
