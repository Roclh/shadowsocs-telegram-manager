package org.Roclh.handlers.commands.user;

import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.utils.PropertiesContainer;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
public class LimitFlowCommand extends AbstractCommand {
    public LimitFlowCommand(PropertiesContainer propertiesContainer) {
        super(propertiesContainer);
    }

    @Override
    public SendMessage handle(Update update) {
        return null;
    }

    @Override
    public String getHelp() {
        return super.getHelp();
    }

    @Override
    public List<String> getCommandNames() {
        return null;
    }
}
