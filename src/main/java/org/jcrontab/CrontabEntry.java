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

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Objects;

/**
 * Immutable domain record representing an entry in the crontab table.
 * Supports execution via direct Runnable lambdas as well as class/method reflection.
 *
 * @param id Unique identifier of the entry
 * @param schedule The parsed CronSchedule
 * @param className Target class name (when using reflection)
 * @param methodName Target method name (when using reflection)
 * @param extraInfo Arguments passed to the target method
 * @param businessDaysOnly Whether this task should run only on business days
 * @param zoneId Timezone for evaluating this task's schedule (defaults to system default)
 * @param taskAction Direct Runnable action (optional, for programmatic lambda scheduling)
 *
 * @author iolalla
 */
public record CrontabEntry(
    int id,
    CronSchedule schedule,
    String className,
    String methodName,
    String[] extraInfo,
    boolean businessDaysOnly,
    ZoneId zoneId,
    Runnable taskAction
) {
    public CrontabEntry {
        Objects.requireNonNull(schedule, "schedule cannot be null");
        zoneId = (zoneId != null) ? zoneId : ZoneId.systemDefault();
        extraInfo = (extraInfo != null) ? extraInfo.clone() : new String[0];
    }

    public static CrontabEntry of(int id, CronSchedule schedule, Runnable action) {
        return new CrontabEntry(id, schedule, null, null, new String[0], false, ZoneId.systemDefault(), action);
    }

    public static CrontabEntry of(int id, CronSchedule schedule, ZoneId zoneId, Runnable action) {
        return new CrontabEntry(id, schedule, null, null, new String[0], false, zoneId, action);
    }

    public static CrontabEntry of(int id, CronSchedule schedule, String className, String methodName, String[] extraInfo, boolean businessDaysOnly) {
        return new CrontabEntry(id, schedule, className, methodName, extraInfo, businessDaysOnly, ZoneId.systemDefault(), null);
    }

    @Override
    public String[] extraInfo() {
        return extraInfo.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CrontabEntry that)) return false;
        return id == that.id &&
               businessDaysOnly == that.businessDaysOnly &&
               Objects.equals(schedule, that.schedule) &&
               Objects.equals(className, that.className) &&
               Objects.equals(methodName, that.methodName) &&
               Arrays.equals(extraInfo, that.extraInfo) &&
               Objects.equals(zoneId, that.zoneId) &&
               Objects.equals(taskAction, that.taskAction);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, schedule, className, methodName, businessDaysOnly, zoneId, taskAction);
        result = 31 * result + Arrays.hashCode(extraInfo);
        return result;
    }
}
