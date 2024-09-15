package org.Roclh.commands.user;

import org.Roclh.data.entities.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
public class DeleteTelegramUserCommandTest extends CommonUserCommandTest {

    @BeforeEach
    public void init() {
        super.init();
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
