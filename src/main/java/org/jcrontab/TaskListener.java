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

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * Listener interface for lifecycle events during task scheduling and execution.
 * Replaces legacy System.out capturing and ad-hoc email hooks with structured callbacks.
 *
 * @author iolalla
 */
public interface TaskListener {

    /**
     * Called when a task has been scheduled for a future execution time.
     */
    default void onScheduled(CrontabEntry entry, ZonedDateTime nextTime) {}

    /**
     * Called immediately before a task begins execution.
     */
    default void onStarted(CrontabEntry entry, ZonedDateTime executionTime) {}

    /**
     * Called when a task completes successfully.
     */
    default void onSuccess(CrontabEntry entry, Duration duration) {}

    /**
     * Called when a task encounters an unhandled exception or error.
     */
    default void onFailure(CrontabEntry entry, Throwable error, Duration duration) {}
}
