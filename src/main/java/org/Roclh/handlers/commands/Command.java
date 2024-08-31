package org.Roclh.handlers.commands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface Command {
    SendMessage handle(Update update);

    default String getHelp() {
        return null;
    }

    List<String> getCommandNames();

    boolean isManager(String userId);

    default String inlineName() {
        return null;
    }
}
