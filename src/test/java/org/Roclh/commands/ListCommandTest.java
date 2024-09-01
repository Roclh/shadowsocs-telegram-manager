package org.Roclh.commands;

import org.Roclh.data.model.user.UserModel;
import org.Roclh.data.model.user.UserService;
import org.Roclh.handlers.commands.user.ListCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class ListCommandTest extends CommonCommandTest {

    @Autowired
    private ListCommand listCommand;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void init() {
        Mockito.when(userService.getAllUsers()).thenReturn(List.of(
                        UserModel.builder().id(1L).telegramId("1").telegramName("TestUser1").usedPort("10000").isAdded(true).build(),
                        UserModel.builder().id(2L).telegramId("2").telegramName("TestUser2").usedPort(null).isAdded(false).build(),
                        UserModel.builder().id(3L).telegramId("3").telegramName("TestUser3").usedPort(null).isAdded(false).build(),
                        UserModel.builder().id(4L).telegramId("4").telegramName("TestUser4").usedPort("10003").isAdded(true).build(),
                        UserModel.builder().id(5L).telegramId("5").telegramName("TestUser5").usedPort("10003").isAdded(true).build()
                )
        );
    }

    @Test
    public void whenListCommand_thenListOfUsers() {
        SendMessage sendMessage = listCommand.handle(update);
        List<UserModel> allUsers = userService.getAllUsers();
        commonSendMessageValidation(sendMessage, allUsers.size() + " registered users:\n" +
                allUsers.stream().map(UserModel::toString)
                        .collect(Collectors.joining("\n")));
    }

}
