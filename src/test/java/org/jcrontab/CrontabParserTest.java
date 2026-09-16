package org.jcrontab;

import org.jcrontab.data.CrontabParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CrontabParser Modern Extension Tests")
class CrontabParserTest {

    @Test
    @DisplayName("Parses CronSchedule directly from schedule string")
    void testParseSchedule() {
        CronSchedule schedule = CrontabParser.parseSchedule("*/10 9-17 * * 1-5");
        assertNotNull(schedule);
        assertTrue(schedule.getMinutes().get(0));
        assertTrue(schedule.getMinutes().get(10));
        assertTrue(schedule.getHours().get(9));
        assertTrue(schedule.getHours().get(17));
        assertFalse(schedule.getHours().get(18));
    }

    @Test
    @DisplayName("Parses CrontabEntry with id and command")
    void testParseEntry() throws Exception {
        CrontabEntry entry = CrontabParser.parseEntry("0 12 * * * com.example.MyService#runNow param1 param2", 42);
        assertNotNull(entry);
        assertEquals(42, entry.id());
        assertEquals("0 12 * * *", entry.schedule().getRawExpression());
        assertEquals("com.example.MyService", entry.className());
        assertEquals("runNow", entry.methodName());
        assertNotNull(entry.extraInfo());
        assertEquals(2, entry.extraInfo().length);
        assertEquals("param1", entry.extraInfo()[0]);
        assertEquals("param2", entry.extraInfo()[1]);
    }

    @Test
    @DisplayName("Parses CrontabEntry with default main method when omitted")
    void testParseEntryDefaultMain() throws Exception {
        CrontabEntry entry = CrontabParser.parseEntry("*/5 * * * * com.example.SimpleApp", 1);
        assertNotNull(entry);
        assertEquals(1, entry.id());
        assertEquals("com.example.SimpleApp", entry.className());
        assertTrue(entry.methodName() == null || entry.methodName().isEmpty());
        assertEquals(0, entry.extraInfo().length);
    }
}
