package org.Roclh.commands.user;

import org.Roclh.commands.CommonCommandTest;
import org.Roclh.data.model.user.UserModel;
import org.Roclh.data.model.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class CommonUserCommandTest extends CommonCommandTest {
    protected List<UserModel> users = new ArrayList<>();
    @MockBean
    protected UserService userService;

    @BeforeEach
    public void init() {
        Mockito.when(userService.getAllUsers()).thenReturn(users);
        Mockito.when(userService.changePassword(any(String.class), any(String.class))).then(mock ->
                users.stream().filter(u -> u.getTelegramId().equals(mock.getArgument(0, String.class))).findFirst()
                        .map(u -> {
                            u.setPassword(mock.getArgument(1, String.class));
                            return true;
                        }).orElse(false));
        Mockito.when(userService.delUser(any(UserModel.class))).then((invocationOnMock) -> users.remove(invocationOnMock.getArgument(0, UserModel.class)));
        Mockito.when(userService.getUser(any(String.class))).then((invocationOnMock ->
                users.stream().filter(u -> u.getTelegramId().equals(invocationOnMock.getArgument(0, String.class)))
                        .findFirst()));
        users.clear();
        users.addAll(List.of(
                UserModel.builder().id(1L).telegramId("1").telegramName("TestUser1").usedPort("10000").isAdded(true).build(),
                UserModel.builder().id(2L).telegramId("2").telegramName("TestUser2").usedPort(null).isAdded(false).build(),
                UserModel.builder().id(3L).telegramId("3").telegramName("TestUser3").usedPort(null).isAdded(false).build(),
                UserModel.builder().id(4L).telegramId("4").telegramName("TestUser4").usedPort("10003").isAdded(true).build(),
                UserModel.builder().id(5L).telegramId("5").telegramName("TestUser5").usedPort("10003").isAdded(true).build()
        ));
    }


    protected void assertUsersWasNotChanged(List<UserModel> usersCopy) {
        assertEquals(users.size(), usersCopy.size());
        assertEquals(users.stream().collect(groupingBy(k -> k, counting())), usersCopy.stream().collect(groupingBy(k -> k, counting())));
    }
}
