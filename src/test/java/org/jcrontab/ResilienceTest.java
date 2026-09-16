package org.jcrontab;

import org.jcrontab.data.CrontabEntryBean;
import org.jcrontab.data.CrontabEntryDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ResilienceTest {

    private Path tempCrontab;
    private Path tempProperties;

    @BeforeEach
    public void setUp() throws Exception {
        tempCrontab = Files.createTempFile("resilience-crontab", ".txt");
        tempProperties = Files.createTempFile("resilience-jcrontab", ".properties");

        // Write crontab containing comments with spaces, blank lines, invalid lines, and valid lines
        String crontabContent = """
            # Valid comment
              # Comment with leading spaces
            
            * * * * * org.jcrontab.tests.TaskTest1 valid1
            INVALID CRON LINE HERE THAT SHOULD NOT CRASH
            99 99 99 99 99 org.jcrontab.tests.TaskTest1 invalid_numbers
            * * * * * org.jcrontab.tests.TaskTest2 valid2
            """;
        Files.writeString(tempCrontab, crontabContent);

        Files.writeString(tempProperties,
                "org.jcrontab.data.datasource = org.jcrontab.data.FileSource\n" +
                "org.jcrontab.data.file = " + tempCrontab.toAbsolutePath() + "\n");

        Crontab.getInstance().init(tempProperties.toAbsolutePath().toString());
    }

    @AfterEach
    public void tearDown() {
        Crontab.getInstance().uninit(100);
        try { Files.deleteIfExists(tempCrontab); } catch (Exception ignored) {}
        try { Files.deleteIfExists(tempProperties); } catch (Exception ignored) {}
    }

    @Test
    public void testFileSourceResilienceWithMalformedLines() throws Exception {
        CrontabEntryBean[] beans = CrontabEntryDAO.getInstance().findAll();
        assertNotNull(beans, "Beans array should not be null");
        // Only valid lines should be retained (the 2 valid tasks)
        assertEquals(2, beans.length, "Should successfully parse the 2 valid tasks despite malformed lines");
        assertEquals("org.jcrontab.tests.TaskTest1", beans[0].getClassName());
        assertEquals("org.jcrontab.tests.TaskTest2", beans[1].getClassName());
    }

    @Test
    public void testNativeExecWithoutArgsDoesNotExit() {
        // NativeExec used to invoke System.exit(1) if no args were given
        assertDoesNotThrow(() -> {
            NativeExec.main(new String[0]);
        }, "NativeExec with no arguments must log an error and return, not call System.exit");
    }
}
