package org.jcrontab;

import java.util.ArrayList;
import java.util.List;
import org.jcrontab.log.ConsoleLogger;
import org.jcrontab.log.Log;
import org.jcrontab.log.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogTest {

    @BeforeEach
    public void setUp() {
        Log.initLogging();
    }

    @AfterEach
    public void tearDown() {
        Log.setLogger(null);
        Log.setForceConsole(false);
    }

    @Test
    public void testCustomLoggerDelegation() {
        TestLogger custom = new TestLogger();
        Log.setLogger(custom);

        Log.info("Hello Info");
        Log.error("Hello Error", new RuntimeException("test error"));
        Log.debug("Hello Debug");

        assertEquals(1, custom.infoMessages.size());
        assertEquals("Hello Info", custom.infoMessages.get(0));

        assertEquals(1, custom.errorMessages.size());
        assertEquals("Hello Error", custom.errorMessages.get(0));

        assertEquals(1, custom.debugMessages.size());
        assertEquals("Hello Debug", custom.debugMessages.get(0));
    }

    @Test
    public void testConsoleLoggerDirect() {
        ConsoleLogger console = new ConsoleLogger("TestUnit");
        assertDoesNotThrow(() -> {
            console.init();
            console.info("Direct console info");
            console.error("Direct console error", null);
            console.debug("Direct console debug");
        });
    }

    @Test
    public void testForceConsoleLogging() {
        Log.setForceConsole(true);
        assertDoesNotThrow(() -> {
            Log.info("Forced console info message");
            Log.error("Forced console error message", new Exception("unit test"));
            Log.debug("Forced console debug message");
        });
    }

    private static class TestLogger implements Logger {
        final List<String> infoMessages = new ArrayList<>();
        final List<String> errorMessages = new ArrayList<>();
        final List<String> debugMessages = new ArrayList<>();

        @Override
        public void init() {}

        @Override
        public void info(String message) {
            infoMessages.add(message);
        }

        @Override
        public void error(String message, Throwable t) {
            errorMessages.add(message);
        }

        @Override
        public void debug(String message) {
            debugMessages.add(message);
        }
    }
}
