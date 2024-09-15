package org.Roclh.handlers.commands.manager;

import org.Roclh.data.model.manager.ManagerService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.utils.PropertiesContainer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class DebugCommand extends AbstractCommand<SendMessage> {


    public DebugCommand(PropertiesContainer propertiesContainer, ManagerService managerService) {
        super(propertiesContainer, managerService);
    }

    @Override
    public SendMessage handle(Update update) {
        propertiesContainer.setProperty(PropertiesContainer.DEBUG_KEY, !propertiesContainer.getBoolProperty(PropertiesContainer.DEBUG_KEY));
        return new SendMessage(String.valueOf(update.getMessage().getChatId()),
                this.propertiesContainer.getBoolProperty(PropertiesContainer.DEBUG_KEY) ? "Debug mod is enabled!" : "Debug mod is disabled!");
    }

    @Override
    public String getHelp() {
        return "debug\n -- enables debug mod";
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("debug");
    }
}
