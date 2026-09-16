/**
 *  This file is part of the jcrontab package
 *  Copyright (C) 2001-2026 Israel Olalla
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free
 *  Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA 02111-1307, USA
 *
 *  For questions, suggestions:
 *  iolalla@gmail.com
 */
package org.jcrontab.log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Built-in Console Logger that outputs formatted messages to standard output and error streams.
 * Used as the default fallback when no external logging framework (SLF4J provider / Log4j) is configured.
 */
public class ConsoleLogger implements Logger {

    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private final String name;

    public ConsoleLogger() {
        this("jcrontab");
    }

    public ConsoleLogger(String name) {
        this.name = name;
    }

    @Override
    public void init() {
        // No initialization required
    }

    @Override
    public void info(String message) {
        System.out.println(FORMATTER.format(LocalDateTime.now()) + " [" + Thread.currentThread().getName() + "] INFO  " + name + " - " + message);
    }

    @Override
    public void warn(String message) {
        System.out.println(FORMATTER.format(LocalDateTime.now()) + " [" + Thread.currentThread().getName() + "] WARN  " + name + " - " + message);
    }

    @Override
    public void warn(String message, Throwable t) {
        System.out.println(FORMATTER.format(LocalDateTime.now()) + " [" + Thread.currentThread().getName() + "] WARN  " + name + " - " + message);
        if (t != null) {
            t.printStackTrace(System.out);
        }
    }

    @Override
    public void error(String message) {
        error(message, null);
    }

    @Override
    public void error(String message, Throwable t) {
        System.err.println(FORMATTER.format(LocalDateTime.now()) + " [" + Thread.currentThread().getName() + "] ERROR " + name + " - " + message);
        if (t != null) {
            t.printStackTrace(System.err);
        }
    }

    @Override
    public void debug(String message) {
        if ("true".equalsIgnoreCase(System.getProperty("jcrontab.debug", "false"))) {
            System.out.println(FORMATTER.format(LocalDateTime.now()) + " [" + Thread.currentThread().getName() + "] DEBUG " + name + " - " + message);
        }
    }
}
