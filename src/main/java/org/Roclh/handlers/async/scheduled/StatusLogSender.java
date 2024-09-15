package org.Roclh.handlers.async.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.bot.TelegramBot;
import org.Roclh.data.model.manager.ManagerService;
import org.Roclh.data.model.user.UserModel;
import org.Roclh.data.model.user.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatusLogSender {

//    private final List<UserModel> allUsers = new ArrayList<>();
//    private final TelegramBot telegramBot;
//    private final ManagerService managerService;
//    private final UserService userService;
    @Scheduled(cron = "0 0 10-22 * * *")
    public void sendStatus(){
        log.info("Sending scheduled status to managers");
//        managerService.getManagers().stream().filter(
//                managerModel -> managerModel.getChatId() != null
//        ).forEach(managerModel -> {
//            final SendMessage sendMessage = new SendMessage();
//            final List<UserModel> currentUsers = userService.getAllUsers();
//            if(!allUsers.stream().sorted().toList().equals(currentUsers.stream().sorted().toList())){
//                allUsers.clear();
//                allUsers.addAll(currentUsers);
//                sendMessage.setChatId(managerModel.getChatId());
//                sendMessage.setText(allUsers.size() + " registered users: \n" +
//                        allUsers.stream().map(UserModel::toString)
//                                .collect(Collectors.joining("\n")));
//                telegramBot.sendMessage(sendMessage);
//            }
//        });
    }
}
