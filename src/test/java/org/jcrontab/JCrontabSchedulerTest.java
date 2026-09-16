package org.jcrontab;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JCrontabScheduler Virtual Thread & Lifecycle Tests")
class JCrontabSchedulerTest {

    private JCrontabScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = JCrontabScheduler.create();
    }

    @AfterEach
    void tearDown() {
        if (scheduler != null) {
            scheduler.close();
        }
    }

    @Test
    @DisplayName("Starts and stops scheduler state correctly")
    void testStartStopLifecycle() {
        assertFalse(scheduler.isRunning());
        scheduler.start();
        assertTrue(scheduler.isRunning());
        scheduler.stop();
        assertFalse(scheduler.isRunning());
    }

    @Test
    @DisplayName("Registers tasks and reports them")
    void testRegisterTasks() {
        JCrontabScheduler.TaskHandle h1 = scheduler.schedule("* * * * *", () -> {});
        JCrontabScheduler.TaskHandle h2 = scheduler.schedule("0 0 1 * *", () -> {});

        assertEquals(2, scheduler.getTasks().size());
        assertFalse(h1.isCancelled());
        assertFalse(h2.isCancelled());

        h1.cancel();
        assertTrue(h1.isCancelled());
        assertEquals(1, scheduler.getTasks().size());
    }

    @Test
    @DisplayName("Executes scheduled lambda task using Java 21 Virtual Threads")
    void testExecutionOnVirtualThread() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean isVirtual = new AtomicBoolean(false);
        AtomicReference<String> threadName = new AtomicReference<>();

        // Schedule a task to run every second (* * * * * *)
        scheduler.schedule("* * * * * *", () -> {
            isVirtual.set(Thread.currentThread().isVirtual());
            threadName.set(Thread.currentThread().getName());
            latch.countDown();
        });

        scheduler.start();

        boolean executed = latch.await(3, TimeUnit.SECONDS);
        assertTrue(executed, "Task should execute within 3 seconds");
        assertTrue(isVirtual.get(), "Task must execute on a Java 21 Virtual Thread");
    }

    @Test
    @DisplayName("Notifies TaskListener on scheduling, start, and success")
    void testTaskListenerCallbacks() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger scheduledCount = new AtomicInteger(0);
        AtomicInteger startedCount = new AtomicInteger(0);
        AtomicInteger successCount = new AtomicInteger(0);

        scheduler.addListener(new TaskListener() {
            @Override
            public void onScheduled(CrontabEntry entry, ZonedDateTime nextTime) {
                scheduledCount.incrementAndGet();
            }

            @Override
            public void onStarted(CrontabEntry entry, ZonedDateTime executionTime) {
                startedCount.incrementAndGet();
            }

            @Override
            public void onSuccess(CrontabEntry entry, Duration duration) {
                successCount.incrementAndGet();
                latch.countDown();
            }
        });

        scheduler.schedule("* * * * * *", () -> {
            // fast no-op
        });

        scheduler.start();

        boolean finished = latch.await(3, TimeUnit.SECONDS);
        assertTrue(finished, "Task listener should receive success callback");
        assertTrue(scheduledCount.get() >= 1, "onScheduled must be called");
        assertTrue(startedCount.get() >= 1, "onStarted must be called");
        assertTrue(successCount.get() >= 1, "onSuccess must be called");
    }

    @Test
    @DisplayName("AutoCloseable stops running and terminates executor")
    void testAutoCloseable() {
        scheduler.start();
        assertTrue(scheduler.isRunning());
        scheduler.close();
        assertFalse(scheduler.isRunning());
    }

    @Test
    @DisplayName("Loads crontab definitions from Reader")
    void testLoadCrontabFromReader() throws Exception {
        String crontabContent = """
            # Comments and blank lines
            
            * * * * * org.jcrontab.tests.TaskTest
            0 12 * * 1-5 org.jcrontab.tests.TaskTest2#main hello world
            """;
        var handles = scheduler.loadCrontab(new java.io.StringReader(crontabContent));
        assertEquals(2, handles.size());
        assertEquals(2, scheduler.getTasks().size());
    }

    @Test
    @DisplayName("Dynamically schedule and cancel task while scheduler is running")
    void testDynamicScheduleAndCancel() {
        scheduler.start();
        assertTrue(scheduler.isRunning());

        var handle = scheduler.schedule("*/5 * * * *", () -> {});
        assertEquals(1, scheduler.getTasks().size());

        handle.cancel();
        assertEquals(0, scheduler.getTasks().size());
    }

    public static final class PrivateConstructorUtility {
        static final AtomicBoolean invoked = new AtomicBoolean(false);
        private PrivateConstructorUtility() {}
        public static void staticAction(String[] args) {
            invoked.set(true);
        }
    }

    @Test
    @DisplayName("Invokes static methods on utility classes with private constructors")
    void testStaticMethodOnPrivateConstructorClass() throws InterruptedException {
        PrivateConstructorUtility.invoked.set(false);
        CountDownLatch latch = new CountDownLatch(1);

        scheduler.addListener(new TaskListener() {
            @Override
            public void onSuccess(CrontabEntry entry, Duration duration) {
                latch.countDown();
            }
        });

        CrontabEntry entry = CrontabEntry.of(
                100,
                CronSchedule.parse("* * * * * *"),
                PrivateConstructorUtility.class.getName(),
                "staticAction",
                new String[]{"arg1"},
                false
        );
        scheduler.schedule(entry);
        scheduler.start();

        assertTrue(latch.await(3, TimeUnit.SECONDS), "Static method task should complete");
        assertTrue(PrivateConstructorUtility.invoked.get(), "Static method must be invoked without instantiating class");
    }

    @Test
    @DisplayName("Respects holidayPredicate for businessDaysOnly tasks and allows force triggerNow")
    void testHolidayPredicateAndTriggerNow() throws InterruptedException {
        AtomicInteger executionCount = new AtomicInteger(0);
        // Mark every day as a holiday
        scheduler.setHolidayPredicate(date -> true);

        CrontabEntry entry = new CrontabEntry(
                200,
                CronSchedule.parse("* * * * * *"),
                null,
                null,
                new String[0],
                true, // businessDaysOnly = true
                java.time.ZoneId.systemDefault(),
                executionCount::incrementAndGet
        );
        scheduler.schedule(entry);
        scheduler.start();

        // Wait briefly: scheduled executions should be skipped because all days are holidays
        Thread.sleep(1200);
        assertEquals(0, executionCount.get(), "Scheduled execution should be skipped on holiday");

        // Manual triggerNow(id) should force execution even on a holiday
        CountDownLatch latch = new CountDownLatch(1);
        scheduler.addListener(new TaskListener() {
            @Override
            public void onSuccess(CrontabEntry e, Duration d) {
                latch.countDown();
            }
        });
        assertTrue(scheduler.triggerNow(200), "triggerNow should find and trigger task 200");
        assertTrue(latch.await(2, TimeUnit.SECONDS), "Manual trigger should execute");
        assertEquals(1, executionCount.get(), "Task should execute once via triggerNow");
    }

    @Test
    @DisplayName("CrontabEntryBean toCrontabEntry and fromCrontabEntry preserve businessDays accurately")
    void testCrontabEntryBeanConversionPreservesBusinessDays() {
        org.jcrontab.data.CrontabEntryBean bean = new org.jcrontab.data.CrontabEntryBean();
        bean.setId(42);
        bean.setClassName("org.jcrontab.tests.TaskTest");
        bean.setMinutes("0");
        bean.setHours("9");
        bean.setDaysOfMonth("*");
        bean.setMonths("*");
        bean.setDaysOfWeek("1-5");
        bean.setBusinessDays(true);

        CrontabEntry entry = bean.toCrontabEntry();
        assertTrue(entry.businessDaysOnly(), "toCrontabEntry must preserve businessDays=true");

        org.jcrontab.data.CrontabEntryBean roundTrip = org.jcrontab.data.CrontabEntryBean.fromCrontabEntry(entry);
        assertTrue(roundTrip.getBusinessDays(), "fromCrontabEntry must preserve businessDays=true");
    }
}
