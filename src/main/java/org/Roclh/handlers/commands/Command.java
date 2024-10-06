package org.Roclh.handlers.commands;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public interface Command<T extends PartialBotApiMethod<? extends Serializable>> {
    T handle(CommandData commandData);

    default String getHelp() {
        return null;
    }
    List<String> getCommandNames();

    boolean isAllowed(Long userId);

    Command<T> setI18N(Locale locale);
}
