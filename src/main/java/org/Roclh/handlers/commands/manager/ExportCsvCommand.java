package org.Roclh.handlers.commands.manager;


import org.Roclh.data.model.manager.ManagerService;
import org.Roclh.data.model.user.UserService;
import org.Roclh.handlers.commands.AbstractCommand;
import org.Roclh.utils.PropertiesContainer;
import org.aspectj.weaver.tools.cache.AsynchronousFileCacheBacking;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.*;
import java.util.List;
import java.util.stream.Stream;

@Component
public class ExportCsvCommand extends AbstractCommand<SendMediaGroup> {

    public ExportCsvCommand(PropertiesContainer propertiesContainer, ManagerService managerService) {
        super(propertiesContainer, managerService);
    }

    @Override
    public SendMediaGroup handle(Update update) {
        return null;
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("csv", "exportcsv");
    }

    private File listToCsv(List<String> list) throws IOException {
        StringBuilder csvStringBuilder = new StringBuilder();
        for (String s : list) {
            csvStringBuilder.append(s).append(',');
        }
        String csvString = csvStringBuilder.toString();
        File file = new File("data.csv");
        FileWriter writer = new FileWriter(file);
        writer.write(csvString);
        return file;
    }
}
