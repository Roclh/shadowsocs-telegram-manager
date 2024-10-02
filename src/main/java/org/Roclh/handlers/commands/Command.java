package org.Roclh.handlers.commands;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.List;

public interface Command<T extends PartialBotApiMethod<? extends Serializable>> {
    T handle(CommandData commandData);

    default String getHelp() {
        return null;
    }
    List<String> getCommandNames();

    boolean isManager(Long userId);
}
