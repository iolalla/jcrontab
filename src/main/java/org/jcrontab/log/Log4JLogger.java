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

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.jcrontab.Crontab;

/**
 * Log4j 2 Logger adapter for Jcrontab.
 * 
 * Automatically configures Log4j 2 if a custom configuration file is defined
 * via org.jcrontab.log.log4J.Properties in jcrontab properties.
 * 
 * @author $Author: iolalla $
 * @version $Revision: 2.0 $
 */
public class Log4JLogger implements org.jcrontab.log.Logger {
	
	private org.apache.logging.log4j.Logger log;

	/**
	 * Initializes Log4j 2 configuration.
	 */
	@Override
	public void init() {
		try {
			String propFile = Crontab.getInstance().getProperty("org.jcrontab.log.log4J.Properties");
			if (propFile != null && !propFile.trim().isEmpty()) {
				File f = new File(propFile.trim());
				if (f.exists()) {
					org.apache.logging.log4j.core.config.Configurator.initialize(null, f.getAbsolutePath());
				}
			}
		} catch (Throwable ignored) {
			// Log4j core or custom configurator might be optional
		}
		try {
			log = LogManager.getLogger("jcrontab");
		} catch (Throwable ignored) {
		}
	}

	@Override
	public void info(String message) {
		if (log != null) {
			log.info(message);
		}
	}

	@Override
	public void warn(String message) {
		if (log != null) {
			log.warn(message);
		}
	}

	@Override
	public void warn(String message, Throwable t) {
		if (log != null) {
			log.warn(message, t);
		}
	}

	@Override
	public void error(String message) {
		if (log != null) {
			log.error(message);
		}
	}

	@Override
	public void error(String message, Throwable t) {
		if (log != null) {
			log.error(message, t);
		}
	}

	@Override
	public void debug(String message) {
		if (log != null) {
			log.debug(message);
		}
	}
}
