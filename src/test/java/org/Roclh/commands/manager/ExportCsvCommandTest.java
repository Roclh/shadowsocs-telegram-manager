package org.Roclh.commands.manager;

import org.Roclh.commands.CommonCommandTest;
import org.Roclh.handlers.commands.manager.ExportCsvCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class ExportCsvCommandTest extends CommonCommandTest {

    @Autowired
    private ExportCsvCommand exportCsvCommand;

    @BeforeEach
    public void init() {}

    @Test
    public void whenFilesContainsRequiredData() {
        String fileContent = "id,tgId,password,port,isAdded\n1,1,qwertyui,true\n";

    }
}
