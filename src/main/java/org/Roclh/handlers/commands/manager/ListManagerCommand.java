package org.Roclh.handlers.commands.manager;

import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.utils.PropertiesContainer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class ListManagerCommand extends AbstractCommand {
    private final PropertiesContainer propertiesContainer;

    public ListManagerCommand(PropertiesContainer propertiesContainer, PropertiesContainer propertiesContainer1) {
        super(propertiesContainer);
        this.propertiesContainer = propertiesContainer1;
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        List<String> managers = propertiesContainer.getProperties(PropertiesContainer.MANAGERS_KEY);
        sendMessage.setText(managers.size() + " managers that exists:\n" +
                String.join("\n", managers));
        return sendMessage;
    }

    @Override
    public String inlineName() {
        return "Список менеджеров";
    }

    @Override
    public String getHelp() {
        return String.join("|", getCommandNames().subList(0, 2)) + "\n -- display all existing managers";
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("listmanager", "listman", "manlist", "managerlist", inlineName().replace(' ', '_').toLowerCase());
    }
}
