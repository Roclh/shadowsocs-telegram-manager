package org.Roclh.commands;

import org.Roclh.data.model.user.UserModel;
import org.Roclh.data.model.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
public class DeleteUserCommandTest extends CommonCommandTest {
    private final List<UserModel> users = new ArrayList<>();

    @MockBean
    private UserService userService;

    @BeforeEach
    public void init() {
        Mockito.when(userService.getAllUsers()).thenReturn(users);
        Mockito.when(userService.delUser(any(UserModel.class))).then((invocationOnMock) -> users.remove(invocationOnMock.getArgument(0, UserModel.class)));
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
    public void whenDelUser_thenUsersSizeReduced(){
        List<UserModel> userModels = List.copyOf(userService.getAllUsers());
        UserModel deletedModel = userModels.get(0);
        boolean isRemoved = userService.delUser(deletedModel);
        List<UserModel> trimmedUserModels = userService.getAllUsers();
        assertTrue(isRemoved);
        assertNotEquals(userModels.size(), trimmedUserModels.size());
        assertFalse(trimmedUserModels.contains(deletedModel));
    }
}
