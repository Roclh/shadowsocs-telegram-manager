package org.Roclh.commands.manager;

import org.Roclh.commands.CommonCommandTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class ExportCsvCommandTest extends CommonCommandTest {
}
