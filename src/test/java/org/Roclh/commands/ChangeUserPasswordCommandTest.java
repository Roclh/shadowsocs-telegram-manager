package org.Roclh.commands;

import net.minidev.json.writer.UpdaterMapper;
import org.Roclh.data.model.user.UserModel;
import org.Roclh.data.model.user.UserService;
import org.Roclh.handlers.commands.user.ChangeUserEnabledCommand;
import org.Roclh.handlers.commands.user.ChangeUserPasswordCommand;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class ChangeUserPasswordCommandTest extends CommonCommandTest {
    private final List<UserModel> users = new ArrayList<>();
    @Autowired
    private ChangeUserPasswordCommand changeUserPasswordCommand;
    @MockBean
    private UserService userService;

    @BeforeEach
    public void init() {
        Mockito.when(userService.getAllUsers()).thenReturn(users);
        Mockito.when(userService.changePassword(any(String.class), any(String.class))).then(mock ->
                users.stream().filter(u->u.getTelegramId().equals(mock.getArgument(0, String.class))).findFirst()
                        .map(u -> {
                            u.setPassword(mock.getArgument(1, String.class));
                            return true;
                        }).orElse(false));
        Mockito.when(userService.getUser(any(String.class))).then(mock -> users.stream().filter(u -> u.getTelegramId().equals(mock.getArgument(0, String.class)))
            .findFirst());
        Mockito.when(userService.saveUser(any(UserModel.class))).thenReturn(true);
        Mockito.when(userService.changeUserEnabled(any(String.class), any(Boolean.class))).thenReturn(true); //в очко засунуть
        Mockito.when(userService.executeShScriptChangePassword(any(UserModel.class))).thenReturn(true);
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
    public void whenCorrectPassword_thenPasswordIsChanged() {
        String expectedPassword = "qwertyui";
        Mockito.when(updateMessage.getText()).thenReturn("chpwdw 3 " + expectedPassword);
        Mockito.when(userService.changePassword("3", expectedPassword)).thenReturn(true);
        SendMessage sendMessage = changeUserPasswordCommand.handle(update);
        commonSendMessageValidation(sendMessage, "Successfully changed password for user with id 3");
        assertEquals(expectedPassword, users.get(2).getPassword());
    }
}
