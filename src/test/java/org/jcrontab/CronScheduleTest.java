package org.jcrontab;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CronSchedule Specification & Analytical Calculation Tests")
class CronScheduleTest {

    private final ZoneId utc = ZoneId.of("UTC");

    @Test
    @DisplayName("Parses standard 5-field POSIX expressions")
    void testStandard5FieldParsing() {
        CronSchedule sched = CronSchedule.parse("15 4 1 10 *");
        assertNotNull(sched);
        assertEquals("15 4 1 10 *", sched.getRawExpression());
        assertFalse(sched.hasSeconds());
        assertFalse(sched.hasYears());

        assertTrue(sched.getMinutes().get(15));
        assertFalse(sched.getMinutes().get(14));
        assertTrue(sched.getHours().get(4));
        assertTrue(sched.getDaysOfMonth().get(1));
        assertTrue(sched.getMonths().get(10));
    }

    @Test
    @DisplayName("Parses 6-field expression with seconds")
    void test6FieldParsingWithSeconds() {
        CronSchedule sched = CronSchedule.parse("30 15 10 * * *");
        assertTrue(sched.hasSeconds());
        assertFalse(sched.hasYears());
        assertTrue(sched.getSeconds().get(30));
        assertTrue(sched.getMinutes().get(15));
        assertTrue(sched.getHours().get(10));
    }

    @Test
    @DisplayName("Parses 7-field expression with year")
    void test7FieldParsingWithYear() {
        CronSchedule sched = CronSchedule.parse("0 0 12 1 1 ? 2030");
        assertTrue(sched.hasSeconds());
        assertTrue(sched.hasYears());
        assertNotNull(sched.getYears());
        assertTrue(sched.getYears().get(2030));
        assertFalse(sched.getYears().get(2029));
    }

    @ParameterizedTest
    @ValueSource(strings = {"@hourly", "@daily", "@midnight", "@weekly", "@monthly", "@yearly", "@annually"})
    @DisplayName("Parses named shortcuts successfully")
    void testNamedShortcuts(String shortcut) {
        CronSchedule sched = CronSchedule.parse(shortcut);
        assertNotNull(sched);
        assertFalse(sched.hasSeconds());
    }

    @Test
    @DisplayName("Parses day and month names")
    void testDayAndMonthNames() {
        CronSchedule sched = CronSchedule.parse("0 9 * JAN,JUN,DEC MON-FRI");
        assertTrue(sched.getMonths().get(1));
        assertTrue(sched.getMonths().get(6));
        assertTrue(sched.getMonths().get(12));
        assertFalse(sched.getMonths().get(2));

        // Monday (1) through Friday (5)
        for (int i = 1; i <= 5; i++) {
            assertTrue(sched.getDaysOfWeek().get(i));
        }
        assertFalse(sched.getDaysOfWeek().get(0)); // Sunday
        assertFalse(sched.getDaysOfWeek().get(6)); // Saturday
    }

    @Test
    @DisplayName("Parses step syntax */15")
    void testStepSyntax() {
        CronSchedule sched = CronSchedule.parse("*/15 * * * *");
        assertTrue(sched.getMinutes().get(0));
        assertTrue(sched.getMinutes().get(15));
        assertTrue(sched.getMinutes().get(30));
        assertTrue(sched.getMinutes().get(45));
        assertFalse(sched.getMinutes().get(10));
    }

    @Test
    @DisplayName("Calculates exact next minute execution")
    void testNextExecutionSameDay() {
        CronSchedule sched = CronSchedule.parse("45 10 * * *");
        ZonedDateTime from = ZonedDateTime.of(2026, 9, 11, 10, 0, 0, 0, utc);

        Optional<ZonedDateTime> next = sched.nextExecution(from);
        assertTrue(next.isPresent());
        assertEquals(ZonedDateTime.of(2026, 9, 11, 10, 45, 0, 0, utc), next.get());
    }

    @Test
    @DisplayName("Calculates next execution rolling over midnight")
    void testNextExecutionNextDay() {
        CronSchedule sched = CronSchedule.parse("15 3 * * *");
        ZonedDateTime from = ZonedDateTime.of(2026, 9, 11, 23, 50, 0, 0, utc);

        Optional<ZonedDateTime> next = sched.nextExecution(from);
        assertTrue(next.isPresent());
        assertEquals(ZonedDateTime.of(2026, 9, 12, 3, 15, 0, 0, utc), next.get());
    }

    @Test
    @DisplayName("Calculates next execution rolling over year")
    void testNextExecutionNextYear() {
        CronSchedule sched = CronSchedule.parse("0 0 1 1 *");
        ZonedDateTime from = ZonedDateTime.of(2026, 6, 15, 12, 0, 0, 0, utc);

        Optional<ZonedDateTime> next = sched.nextExecution(from);
        assertTrue(next.isPresent());
        assertEquals(ZonedDateTime.of(2027, 1, 1, 0, 0, 0, 0, utc), next.get());
    }

    @Test
    @DisplayName("Calculates next execution for leap year Feb 29")
    void testLeapYearNextExecution() {
        CronSchedule sched = CronSchedule.parse("0 0 29 2 *");
        ZonedDateTime from = ZonedDateTime.of(2026, 1, 1, 0, 0, 0, 0, utc);

        Optional<ZonedDateTime> next = sched.nextExecution(from);
        assertTrue(next.isPresent());
        // Next leap year after 2026 is 2028
        assertEquals(ZonedDateTime.of(2028, 2, 29, 0, 0, 0, 0, utc), next.get());
    }

    @Test
    @DisplayName("Calculates next execution for day of week filter (e.g. MON)")
    void testDayOfWeekFilter() {
        CronSchedule sched = CronSchedule.parse("0 8 * * 1"); // Monday 8am
        // 2026-09-11 is a Friday
        ZonedDateTime from = ZonedDateTime.of(2026, 9, 11, 12, 0, 0, 0, utc);

        Optional<ZonedDateTime> next = sched.nextExecution(from);
        assertTrue(next.isPresent());
        // Following Monday is 2026-09-14
        assertEquals(ZonedDateTime.of(2026, 9, 14, 8, 0, 0, 0, utc), next.get());
    }

    @Test
    @DisplayName("Supports 6-field second-level next execution")
    void testSecondsNextExecution() {
        CronSchedule sched = CronSchedule.parse("*/15 * * * * *");
        ZonedDateTime from = ZonedDateTime.of(2026, 9, 11, 10, 0, 2, 0, utc);

        Optional<ZonedDateTime> next = sched.nextExecution(from);
        assertTrue(next.isPresent());
        assertEquals(ZonedDateTime.of(2026, 9, 11, 10, 0, 15, 0, utc), next.get());
    }

    @Test
    @DisplayName("Matches method correctly identifies active time")
    void testMatches() {
        CronSchedule sched = CronSchedule.parse("30 14 * * 5"); // Friday at 14:30
        ZonedDateTime matchingTime = ZonedDateTime.of(2026, 9, 11, 14, 30, 0, 0, utc); // 2026-09-11 is Friday
        ZonedDateTime nonMatchingTime = ZonedDateTime.of(2026, 9, 11, 14, 31, 0, 0, utc);

        assertTrue(sched.matches(matchingTime));
        assertFalse(sched.matches(nonMatchingTime));
    }

    @Test
    @DisplayName("Rejects malformed expressions")
    void testMalformedExpressions() {
        assertThrows(IllegalArgumentException.class, () -> CronSchedule.parse(""));
        assertThrows(IllegalArgumentException.class, () -> CronSchedule.parse("1 2 3"));
        assertThrows(IllegalArgumentException.class, () -> CronSchedule.parse("1 2 3 4 5 6 7 8"));
        assertThrows(IllegalArgumentException.class, () -> CronSchedule.parse("*/0 * * * *"));
        assertThrows(IllegalArgumentException.class, () -> CronSchedule.parse("10-5 * * * *"));
    }
}
