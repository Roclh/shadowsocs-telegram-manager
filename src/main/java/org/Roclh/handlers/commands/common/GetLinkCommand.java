package org.Roclh.handlers.commands.common;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.Role;
import org.Roclh.data.entities.UserModel;
import org.Roclh.data.services.ServerSharingService;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.data.services.UserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.commands.CommandData;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

@Component
@Slf4j
public class GetLinkCommand extends AbstractCommand<PartialBotApiMethod<? extends Serializable>> {
    private final ServerSharingService serverSharingService;
    private final UserService userService;
    public GetLinkCommand(TelegramUserService telegramUserService, ServerSharingService serverSharingService, UserService userService) {
        super(telegramUserService);
        this.serverSharingService = serverSharingService;
        this.userService = userService;
    }

    @Override
    public PartialBotApiMethod<? extends Serializable> handle(CommandData commandData) {
        SendPhoto sendPhoto = new SendPhoto();
        SendMessage sendMessage = new SendMessage();
        sendPhoto.setChatId(commandData.getChatId());
        sendMessage.setChatId(commandData.getChatId());
        Long telegramId = commandData.getTelegramId();


        UserModel userModel = userService.getUser(telegramId).orElse(null);
        if(userModel == null || !userModel.isAdded()){
            log.error("Failed to generate link - user with id {} not exists or is not added", telegramId);
            sendMessage.setText("Failed to generate link - user with id " + telegramId + " not exists or is not added");
            return sendMessage;
        }
        String uri = serverSharingService.generateServerUrl(userModel);
        if(uri == null){
            log.error("Failed to generate link - failed to generate server url");
            sendMessage.setText("Failed to generate link - failed to generate server url");
            return sendMessage;
        }
        BufferedImage qrCode = serverSharingService.generateServerUrlQrCode(userModel);
        if(qrCode == null){
            log.error("Failed to generate link - failed to generate server QR code");
            sendMessage.setText("Failed to generate link - failed to generate server QR code");
            return sendMessage;
        }
        sendPhoto.setCaption(uri);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(qrCode, "jpeg", os);
        } catch (IOException e) {
            log.error("Failed to generate link - failed to parse qr code to output stream", e);
            sendMessage.setText("Failed to generate link - failed to parse qr code to output stream");
            return sendMessage;
        }
        sendPhoto.setPhoto(new InputFile().setMedia(new ByteArrayInputStream(os.toByteArray()), "QR.jpeg"));
        return sendPhoto;
    }


    @Override
    public boolean isManager(Long userId) {
        return telegramUserService.isAllowed(userId, Role.USER) && userService.getUser(userId).map(UserModel::isAdded).orElse(false);
    }

    @Override
    public String getHelp() {
        return getCommandNames().get(0) + "\n -- get qr to access server";
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("qr", "link");
    }
}
