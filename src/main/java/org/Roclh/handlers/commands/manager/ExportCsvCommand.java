package org.Roclh.handlers.commands.manager;


import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.Role;
import org.Roclh.data.entities.TelegramUserModel;
import org.Roclh.data.entities.UserModel;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.data.services.UserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaDocument;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ExportCsvCommand extends AbstractCommand<SendMediaGroup> {


    private final UserService userManager;

    public ExportCsvCommand(TelegramUserService telegramUserService, UserService userManager) {
        super(telegramUserService);
        this.userManager = userManager;
    }

    @Override
    public boolean isManager(Long userId) {
        return telegramUserService.isAllowed(userId, Role.ROOT);
    }

    @Override
    public SendMediaGroup handle(Update update) {
        File fileUserModel = userModelToCsv(userManager.getAllUsers());
        File fileTelegramUserModel = telegramUserModelToCsv(telegramUserService.getUsers());
        List<File> attachments = new ArrayList<>();
        attachments.add(fileUserModel);
        attachments.add(fileTelegramUserModel);
        List<InputMedia> medias = new ArrayList<>(
                attachments.stream().map(file -> (InputMedia) InputMediaDocument.builder()
                                .media("attach://" + file.getName())
                                .mediaName(file.getName())
                                .isNewMedia(true)
                                .newMediaFile(file)
                                .build())
                        .toList());
        medias.get(0).setCaption("Take your data, sir.");

        return SendMediaGroup.builder()
                .chatId(update.getMessage().getChatId())
                .medias(medias)
                .build();

    }

    @Override
    public List<String> getCommandNames() {
        return List.of("csv", "exportcsv");
    }

    private File userModelToCsv(List<UserModel> users) {
        StringBuilder csvStringBuilder = new StringBuilder();
        csvStringBuilder.append("id,igid,password,port,isAdded\n");
        for (UserModel user : users) {
            Long id = user.getId();
            Long tgid = user.getUserModel().getId();
            String password = user.getPassword();
            Long port = user.getUsedPort();
            boolean isAdded = user.isAdded();
            csvStringBuilder.append(id).append(',').append(tgid).append(',').append(password).append(',').append(port).append(',').append(isAdded).append('\n');
        }
        String csvString = csvStringBuilder.toString();
        File file = new File("userModel.csv");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(csvString);
        } catch (IOException e) {
            log.error("Failed to export csv", e);
        }
        return file;
    }

    private File telegramUserModelToCsv(List<TelegramUserModel> users) {
        StringBuilder csvStringBuilder = new StringBuilder();
        csvStringBuilder.append("id,tgId,role,chatId,tgName\n");
        for (TelegramUserModel user : users) {
            Long id = user.getId();
            Long tgId = user.getTelegramId();
            Role role = user.getRole();
            Long chatId = user.getChatId();
            String tgName = user.getTelegramName();
            csvStringBuilder.append(id).append(',').append(tgId).append(',').append(role).append(',').append(chatId).append(',').append(tgName).append('\n');
        }
        String csvString = csvStringBuilder.toString();
        File file = new File("tgUserModel.csv");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(csvString);
        } catch (IOException e) {
            log.error("Failed to export csv", e);
        }
        return file;
    }

}
