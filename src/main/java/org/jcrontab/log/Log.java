/**
 *  This file is part of the jcrontab package
 *  Copyright (C) 2001-2003 Israel Olalla
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
 *
 *  iolalla@yahoo.com
 *
 */
package org.jcrontab.log;

import org.jcrontab.Crontab;
import org.slf4j.LoggerFactory;

/**
 * This class helps the testing process to make easier testing
 * @author $Author: iolalla $
 * @version $Revision: 1.4 $
 */
public class Log {
	
	public static final org.slf4j.Logger logger = LoggerFactory.getLogger("Cron4Web");

	/**
	 *	This method reports a info level message to the log
	 */
	public static void info(String message){
		logger.info(message);
	}
	/**
	 *	This method reports an Exception or Error  message to the log
	 */
	public static void error(String message, Throwable t){
		logger.error(message, t);
	}
	/**
	 *	This method reports a debug level message to the log
	 */
	public static void debug(String message){
		logger.debug(message);
	}
}
