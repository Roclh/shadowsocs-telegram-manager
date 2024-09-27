package org.Roclh.commands.user;

import org.Roclh.commands.CommonCommandTest;
import org.Roclh.data.Role;
import org.Roclh.data.entities.BandwidthModel;
import org.Roclh.data.entities.TelegramUserModel;
import org.Roclh.data.entities.UserModel;
import org.Roclh.data.services.BandwidthService;
import org.Roclh.data.services.UserService;
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
    protected List<BandwidthModel> bandwidthModels = new ArrayList<>();
    @MockBean
    protected UserService userService;
    @MockBean
    protected BandwidthService bandwidthService;

    @BeforeEach
    public void init() {
        Mockito.when(bandwidthService.getRule(any(Long.class))).then(mock ->
                bandwidthModels.stream().filter(b -> b.getUserModel().getUserModel().getTelegramId().equals(mock.getArgument(0, Long.class))).findFirst());
        Mockito.when(userService.getAllUsers()).thenReturn(users);
        Mockito.when(userService.changePassword(any(Long.class), any(String.class))).then(mock ->
                users.stream().filter(u -> u.getUserModel().getTelegramId().equals(mock.getArgument(0, Long.class))).findFirst()
                        .map(u -> {
                            u.setPassword(mock.getArgument(1, String.class));
                            return true;
                        }).orElse(false));
        Mockito.when(userService.delUser(any(UserModel.class))).then((invocationOnMock) -> users.remove(invocationOnMock.getArgument(0, UserModel.class)));
        Mockito.when(userService.getUser(any(Long.class))).then((invocationOnMock ->
                users.stream().filter(u -> u.getUserModel().getTelegramId().equals(invocationOnMock.getArgument(0, Long.class)))
                        .findFirst()));
        users.clear();
        users.addAll(List.of(
                UserModel.builder().id(0L).userModel(TelegramUserModel.builder()
                        .id(0L).role(Role.USER).telegramId(0L).telegramName("TestUser1").build()).usedPort(10000L).isAdded(true).build(),
                UserModel.builder().id(1L).userModel(TelegramUserModel.builder()
                        .id(1L).role(Role.GUEST).telegramId(1L).telegramName("TestUser2").build()).usedPort(null).isAdded(false).build(),
                UserModel.builder().id(2L).userModel(TelegramUserModel.builder()
                        .id(2L).role(Role.GUEST).telegramId(2L).telegramName("TestUser3").build()).usedPort(null).isAdded(false).build(),
                UserModel.builder().id(3L).userModel(TelegramUserModel.builder()
                        .id(3L).role(Role.MANAGER).telegramId(3L).telegramName("TestUser4").build()).usedPort(10001L).isAdded(true).build(),
                UserModel.builder().id(4L).userModel(TelegramUserModel.builder()
                        .id(4L).role(Role.ROOT).telegramId(4L).telegramName("TestUser5").build()).usedPort(10001L).isAdded(true).build()
        ));
        bandwidthModels.clear();
        bandwidthModels.addAll(List.of(
                BandwidthModel.builder().id(0L).bandwidth(BandwidthModel.Bandwidth.MB8).userModel(users.get(0)).build(),
                BandwidthModel.builder().id(1L).bandwidth(BandwidthModel.Bandwidth.MB32).userModel(users.get(4)).build()
        ));
    }


    protected void assertUsersWasNotChanged(List<UserModel> usersCopy) {
        assertEquals(users.size(), usersCopy.size());
        assertEquals(users.stream().collect(groupingBy(k -> k, counting())), usersCopy.stream().collect(groupingBy(k -> k, counting())));
    }
}
