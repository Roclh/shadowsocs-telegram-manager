package org.Roclh.handlers.commands;

import lombok.AllArgsConstructor;
import org.Roclh.utils.PropertiesContainer;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public abstract class AbstractCommand implements Command {

    protected final PropertiesContainer propertiesContainer;

    public boolean isManager(String userId) {
        return propertiesContainer.getProperties(PropertiesContainer.MANAGERS_KEY).contains(userId);
    }
}
