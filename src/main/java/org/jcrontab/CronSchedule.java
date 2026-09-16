/**
 * This file is part of the jcrontab package
 * Copyright (C) 2001-2026 Israel Olalla
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 */
package org.jcrontab;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.BitSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Modern, immutable cron schedule representation supporting standard POSIX
 * 5-field cron, 6-field (with seconds), and 7-field (with years), as well as
 * named shortcuts (@hourly, @daily, etc.) and direct next-execution-time calculations.
 *
 * Thread-safe and timezone-aware using java.time (JSR-310).
 *
 * @author iolalla
 */
public final class CronSchedule {

    private final String rawExpression;
    private final boolean hasSeconds;
    private final boolean hasYears;
    private final boolean domWildcard;
    private final boolean dowWildcard;

    private final BitSet seconds;
    private final BitSet minutes;
    private final BitSet hours;
    private final BitSet daysOfMonth;
    private final BitSet months;
    private final BitSet daysOfWeek; // 0-7, where 0 and 7 = Sunday
    private final BitSet years;

    private static final Map<String, Integer> MONTH_NAMES = Map.ofEntries(
        Map.entry("JAN", 1), Map.entry("FEB", 2), Map.entry("MAR", 3),
        Map.entry("APR", 4), Map.entry("MAY", 5), Map.entry("JUN", 6),
        Map.entry("JUL", 7), Map.entry("AUG", 8), Map.entry("SEP", 9),
        Map.entry("OCT", 10), Map.entry("NOV", 11), Map.entry("DEC", 12)
    );

    private static final Map<String, Integer> DAY_NAMES = Map.ofEntries(
        Map.entry("SUN", 0), Map.entry("MON", 1), Map.entry("TUE", 2),
        Map.entry("WED", 3), Map.entry("THU", 4), Map.entry("FRI", 5),
        Map.entry("SAT", 6)
    );

    private static final Map<String, String> SHORTCUTS = Map.of(
        "@yearly", "0 0 1 1 *",
        "@annually", "0 0 1 1 *",
        "@monthly", "0 0 1 * *",
        "@weekly", "0 0 * * 0",
        "@daily", "0 0 * * *",
        "@midnight", "0 0 * * *",
        "@hourly", "0 * * * *"
    );

    private CronSchedule(String rawExpression, boolean hasSeconds, boolean hasYears,
                         boolean domWildcard, boolean dowWildcard,
                         BitSet seconds, BitSet minutes, BitSet hours,
                         BitSet daysOfMonth, BitSet months, BitSet daysOfWeek, BitSet years) {
        this.rawExpression = rawExpression;
        this.hasSeconds = hasSeconds;
        this.hasYears = hasYears;
        this.domWildcard = domWildcard;
        this.dowWildcard = dowWildcard;
        this.seconds = seconds;
        this.minutes = minutes;
        this.hours = hours;
        this.daysOfMonth = daysOfMonth;
        this.months = months;
        this.daysOfWeek = daysOfWeek;
        this.years = years;
    }

    /**
     * Parses a cron expression string.
     * Supports named shortcuts (@daily, etc.), standard POSIX 5-field ("* * * * *"),
     * 6-field with seconds ("0 * * * * *"), or 7-field with year ("0 * * * * * *").
     */
    public static CronSchedule parse(String expression) {
        Objects.requireNonNull(expression, "Expression cannot be null");
        String trimmed = expression.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Cron expression cannot be empty");
        }

        String resolved = SHORTCUTS.getOrDefault(trimmed.toLowerCase(Locale.ROOT), trimmed);
        String[] tokens = resolved.split("\\s+");

        boolean hasSec = false;
        boolean hasYr = false;
        String secToken = "0";
        String minToken;
        String hourToken;
        String domToken;
        String monToken;
        String dowToken;
        String yrToken = "*";

        if (tokens.length == 5) {
            // standard POSIX: min hour dom mon dow
            minToken = tokens[0];
            hourToken = tokens[1];
            domToken = tokens[2];
            monToken = tokens[3];
            dowToken = tokens[4];
        } else if (tokens.length == 6) {
            // 6-field: sec min hour dom mon dow
            hasSec = true;
            secToken = tokens[0];
            minToken = tokens[1];
            hourToken = tokens[2];
            domToken = tokens[3];
            monToken = tokens[4];
            dowToken = tokens[5];
        } else if (tokens.length == 7) {
            // 7-field: sec min hour dom mon dow year
            hasSec = true;
            hasYr = true;
            secToken = tokens[0];
            minToken = tokens[1];
            hourToken = tokens[2];
            domToken = tokens[3];
            monToken = tokens[4];
            dowToken = tokens[5];
            yrToken = tokens[6];
        } else {
            throw new IllegalArgumentException("Invalid cron expression '" + expression + "': expected 5, 6, or 7 fields but got " + tokens.length);
        }

        BitSet secBits = parseField(secToken, 0, 59, null);
        BitSet minBits = parseField(minToken, 0, 59, null);
        BitSet hourBits = parseField(hourToken, 0, 23, null);
        BitSet domBits = parseField(domToken, 1, 31, null);
        BitSet monBits = parseField(monToken, 1, 12, MONTH_NAMES);
        BitSet dowBits = parseField(dowToken, 0, 7, DAY_NAMES);
        // Normalize 7 (Sunday) to 0
        if (dowBits.get(7)) {
            dowBits.set(0);
        }

        BitSet yrBits = hasYr ? parseField(yrToken, 1970, 2099, null) : null;

        boolean domWild = domToken.equals("*") || domToken.equals("?");
        boolean dowWild = dowToken.equals("*") || dowToken.equals("?");

        return new CronSchedule(trimmed, hasSec, hasYr, domWild, dowWild,
                secBits, minBits, hourBits, domBits, monBits, dowBits, yrBits);
    }

    private static BitSet parseField(String token, int min, int max, Map<String, Integer> names) {
        BitSet bits = new BitSet(max + 1);
        if (token.equals("*") || token.equals("?")) {
            bits.set(min, max + 1);
            return bits;
        }

        String[] parts = token.split(",");
        for (String part : parts) {
            parsePart(part.trim(), min, max, names, bits);
        }
        return bits;
    }

    private static void parsePart(String part, int min, int max, Map<String, Integer> names, BitSet bits) {
        int step = 1;
        String rangePart = part;
        int slashIdx = part.indexOf('/');
        if (slashIdx >= 0) {
            step = Integer.parseInt(part.substring(slashIdx + 1));
            if (step <= 0) {
                throw new IllegalArgumentException("Step must be positive in '" + part + "'");
            }
            rangePart = part.substring(0, slashIdx);
        }

        if (rangePart.equals("*") || rangePart.equals("?")) {
            for (int i = min; i <= max; i += step) {
                bits.set(i);
            }
            return;
        }

        int dashIdx = rangePart.indexOf('-');
        if (dashIdx >= 0) {
            int start = parseValue(rangePart.substring(0, dashIdx), names);
            int end = parseValue(rangePart.substring(dashIdx + 1), names);
            if (start > end) {
                throw new IllegalArgumentException("Invalid range in '" + part + "': start " + start + " > end " + end);
            }
            for (int i = start; i <= end; i += step) {
                bits.set(i);
            }
        } else {
            int val = parseValue(rangePart, names);
            if (slashIdx >= 0) {
                for (int i = val; i <= max; i += step) {
                    bits.set(i);
                }
            } else {
                bits.set(val);
            }
        }
    }

    private static int parseValue(String val, Map<String, Integer> names) {
        if (names != null) {
            String upper = val.toUpperCase(Locale.ROOT);
            if (names.containsKey(upper)) {
                return names.get(upper);
            }
        }
        return Integer.parseInt(val);
    }

    /**
     * Calculates the exact next execution time after the provided timestamp.
     *
     * @param from ZonedDateTime starting point
     * @return Optional containing the next execution time, or empty if none within 5 years
     */
    public Optional<ZonedDateTime> nextExecution(ZonedDateTime from) {
        Objects.requireNonNull(from, "Starting time cannot be null");

        // Advance by at least 1 unit from reference
        ZonedDateTime candidate = hasSeconds
                ? from.plusSeconds(1).truncatedTo(ChronoUnit.SECONDS)
                : from.plusMinutes(1).truncatedTo(ChronoUnit.MINUTES);

        int maxYear = candidate.getYear() + 5;

        while (candidate.getYear() <= maxYear) {
            // 1. Year check
            if (hasYears && years != null && !years.get(candidate.getYear())) {
                candidate = candidate.plusYears(1).withDayOfYear(1).truncatedTo(ChronoUnit.DAYS);
                continue;
            }

            // 2. Month check
            if (!months.get(candidate.getMonthValue())) {
                candidate = candidate.plusMonths(1).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
                continue;
            }

            // 3. Day check (day of month & day of week)
            if (!matchesDay(candidate.toLocalDate())) {
                candidate = candidate.plusDays(1).truncatedTo(ChronoUnit.DAYS);
                continue;
            }

            // 4. Hour check
            if (!hours.get(candidate.getHour())) {
                candidate = candidate.plusHours(1).truncatedTo(ChronoUnit.HOURS);
                continue;
            }

            // 5. Minute check
            if (!minutes.get(candidate.getMinute())) {
                candidate = candidate.plusMinutes(1).truncatedTo(ChronoUnit.MINUTES);
                continue;
            }

            // 6. Second check
            if (hasSeconds && !seconds.get(candidate.getSecond())) {
                candidate = candidate.plusSeconds(1).truncatedTo(ChronoUnit.SECONDS);
                continue;
            }

            return Optional.of(candidate);
        }

        return Optional.empty();
    }

    /**
     * Checks if the given ZonedDateTime matches this cron schedule.
     */
    public boolean matches(ZonedDateTime time) {
        if (hasYears && years != null && !years.get(time.getYear())) {
            return false;
        }
        if (!months.get(time.getMonthValue())) {
            return false;
        }
        if (!matchesDay(time.toLocalDate())) {
            return false;
        }
        if (!hours.get(time.getHour())) {
            return false;
        }
        if (!minutes.get(time.getMinute())) {
            return false;
        }
        if (hasSeconds && !seconds.get(time.getSecond())) {
            return false;
        }
        return true;
    }

    private boolean matchesDay(LocalDate date) {
        boolean domMatch = daysOfMonth.get(date.getDayOfMonth());
        int dow = date.getDayOfWeek().getValue() % 7; // Sunday = 0
        boolean dowMatch = daysOfWeek.get(dow);

        // Standard POSIX rule: if both are restricted, match if EITHER matches (OR).
        // If either is wildcard, match if BOTH match (effectively checking the non-wildcard one).
        if (!domWildcard && !dowWildcard) {
            return domMatch || dowMatch;
        }
        return domMatch && dowMatch;
    }

    public String getRawExpression() {
        return rawExpression;
    }

    public boolean hasSeconds() {
        return hasSeconds;
    }

    public boolean hasYears() {
        return hasYears;
    }

    public BitSet getSeconds() {
        return (BitSet) seconds.clone();
    }

    public BitSet getMinutes() {
        return (BitSet) minutes.clone();
    }

    public BitSet getHours() {
        return (BitSet) hours.clone();
    }

    public BitSet getDaysOfMonth() {
        return (BitSet) daysOfMonth.clone();
    }

    public BitSet getMonths() {
        return (BitSet) months.clone();
    }

    public BitSet getDaysOfWeek() {
        return (BitSet) daysOfWeek.clone();
    }

    public BitSet getYears() {
        return years == null ? null : (BitSet) years.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CronSchedule that)) return false;
        return hasSeconds == that.hasSeconds &&
               hasYears == that.hasYears &&
               domWildcard == that.domWildcard &&
               dowWildcard == that.dowWildcard &&
               Objects.equals(seconds, that.seconds) &&
               Objects.equals(minutes, that.minutes) &&
               Objects.equals(hours, that.hours) &&
               Objects.equals(daysOfMonth, that.daysOfMonth) &&
               Objects.equals(months, that.months) &&
               Objects.equals(daysOfWeek, that.daysOfWeek) &&
               Objects.equals(years, that.years);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hasSeconds, hasYears, seconds, minutes, hours, daysOfMonth, months, daysOfWeek, years);
    }

    @Override
    public String toString() {
        return "CronSchedule[" + rawExpression + "]";
    }
}
