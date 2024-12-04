package org.Roclh.handlers.commands.common;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.services.TelegramUserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.handlers.messaging.CommandData;
import org.Roclh.utils.MessageUtils;
import org.Roclh.utils.i18n.I18N;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.List;

@Slf4j
@Component
public class GuideCommand extends AbstractCommand<PartialBotApiMethod<? extends Serializable>> {
    public GuideCommand(TelegramUserService telegramUserService) {
        super(telegramUserService);
    }

    @Override
    public PartialBotApiMethod<? extends Serializable> handle(CommandData commandData) {
        String[] words = commandData.getCommand().split(" ");
        if(words.length < 2){
            return MessageUtils.sendMessage(commandData.getMessageData())
                    .text(i18N.get("common.validation.not.enough.argument", 2))
                    .build();
        }
        Type guideType;
        try {
            guideType = Type.valueOf(words[1]);
        }catch (IllegalArgumentException e){
            return MessageUtils.sendMessage(commandData.getMessageData())
                    .text("Failed to execute command - unkown guide type " + words[1])
                    .build();
        }
        return switch (guideType){
            case PC -> MessageUtils.sendMessage(commandData.getMessageData())
                        .text(i18N.get("command.common.guide.pc"))
                        .build();
            case IOS -> MessageUtils.sendMessage(commandData.getMessageData())
                    .text(i18N.get("command.common.guide.ios"))
                    .build();
            case ANDROID -> MessageUtils.sendMessage(commandData.getMessageData())
                    .text(i18N.get("command.common.guide.android"))
                    .build();
        };
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("guide");
    }

    @Override
    public boolean isAllowed(Long userId) {
        return true;
    }

    public enum Type{
        PC("command.common.guide.pc.alias"), IOS("command.common.guide.ios.alias"), ANDROID("command.common.guide.android.alias");

        private final String localized;

        public String localize(I18N i18N){
            return i18N.get(localized);
        }

        Type(String localized) {
            this.localized = localized;
        }
    }
}
