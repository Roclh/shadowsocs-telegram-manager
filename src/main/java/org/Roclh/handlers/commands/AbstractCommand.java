package org.Roclh.handlers.commands;

import lombok.AllArgsConstructor;
import org.Roclh.data.model.manager.ManagerService;
import org.Roclh.utils.PropertiesContainer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.io.Serializable;

@Component
@AllArgsConstructor
public abstract class AbstractCommand<T extends BotApiMethod<? extends Serializable>> implements Command<T> {

    protected final PropertiesContainer propertiesContainer;
    protected final ManagerService managerService;

    public boolean isManager(String userId) {
        return propertiesContainer.getProperties(PropertiesContainer.MANAGERS_KEY).contains(userId)
                || managerService.getManager(userId) != null;
    }
}
