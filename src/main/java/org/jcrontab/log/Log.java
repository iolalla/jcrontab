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

import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

/**
 * Main Logging gateway for Jcrontab.
 * 
 * Logging resolution order:
 * 1. An explicitly configured {@link Logger} (e.g. {@link Log4JLogger} or custom logger set via {@link #setLogger(Logger)}).
 * 2. An active SLF4J provider if present and not a No-Operation (NOP) logger.
 * 3. Default fallback: {@link ConsoleLogger}, displaying all messages directly to console (System.out/System.err).
 * 
 * @author $Author: iolalla $
 * @version $Revision: 2.0 $
 */
public class Log {
	
	private static final ConsoleLogger CONSOLE_LOGGER = new ConsoleLogger("Cron4Web");
	public static org.slf4j.Logger logger = LoggerFactory.getLogger("Cron4Web");
	private static Logger customLogger = null;
	private static boolean forceConsole = false;

	static {
		initLogging();
	}

	/**
	 * Initializes logging and determines if a fallback to console logging is needed.
	 */
	public static synchronized void initLogging() {
		try {
			logger = LoggerFactory.getLogger("Cron4Web");
			org.slf4j.ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();
			if (iLoggerFactory == null 
				|| iLoggerFactory instanceof org.slf4j.helpers.NOPLoggerFactory 
				|| logger instanceof NOPLogger) {
				forceConsole = true;
			} else {
				forceConsole = false;
			}
		} catch (Throwable t) {
			forceConsole = true;
		}

		String prop = System.getProperty("jcrontab.log.console");
		if (prop != null) {
			forceConsole = "true".equalsIgnoreCase(prop);
		}
	}

	/**
	 * Sets a custom Logger implementation (e.g., Log4JLogger).
	 * @param cl The custom Logger instance
	 */
	public static void setLogger(Logger cl) {
		customLogger = cl;
	}

	/**
	 * Returns the currently active custom logger, or null if using default SLF4J / Console.
	 * @return Logger instance
	 */
	public static Logger getLogger() {
		return customLogger;
	}

	/**
	 * Forces console output regardless of SLF4J provider presence.
	 * @param force true to force console output
	 */
	public static void setForceConsole(boolean force) {
		forceConsole = force;
	}

	/**
	 * Reports an info level message to the log.
	 * @param message The message to log
	 */
	public static void info(String message) {
		if (customLogger != null) {
			customLogger.info(message);
			return;
		}
		if (forceConsole || logger == null || logger instanceof NOPLogger) {
			CONSOLE_LOGGER.info(message);
		} else {
			logger.info(message);
		}
	}

	/**
	 * Reports a warning level message to the log.
	 * @param message The warning message
	 */
	public static void warn(String message) {
		if (customLogger != null) {
			customLogger.warn(message);
			return;
		}
		if (forceConsole || logger == null || logger instanceof NOPLogger) {
			CONSOLE_LOGGER.warn(message);
		} else {
			logger.warn(message);
		}
	}

	/**
	 * Reports a warning level message and exception to the log.
	 * @param message The warning message
	 * @param t The throwable to log
	 */
	public static void warn(String message, Throwable t) {
		if (customLogger != null) {
			customLogger.warn(message, t);
			return;
		}
		if (forceConsole || logger == null || logger instanceof NOPLogger) {
			CONSOLE_LOGGER.warn(message, t);
		} else {
			logger.warn(message, t);
		}
	}

	/**
	 * Reports an error message to the log.
	 * @param message The error message
	 */
	public static void error(String message) {
		error(message, null);
	}

	/**
	 * Reports an Exception or Error message to the log.
	 * @param message The error message
	 * @param t The throwable to log (may be null)
	 */
	public static void error(String message, Throwable t) {
		if (customLogger != null) {
			customLogger.error(message, t);
			return;
		}
		if (forceConsole || logger == null || logger instanceof NOPLogger) {
			CONSOLE_LOGGER.error(message, t);
		} else {
			logger.error(message, t);
		}
	}

	/**
	 * Reports a debug level message to the log.
	 * @param message The debug message
	 */
	public static void debug(String message) {
		if (customLogger != null) {
			customLogger.debug(message);
			return;
		}
		if (forceConsole || logger == null || logger instanceof NOPLogger) {
			CONSOLE_LOGGER.debug(message);
		} else {
			logger.debug(message);
		}
	}
}
