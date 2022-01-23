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

import org.apache.logging.log4j.LogManager;

/**
 * This is the Log4jLogger as an example about how to use Log4J to log in 
 * Jcrontab
 * @author $Author: iolalla $
 * @version $Revision: 1.5 $
 */
public class Log4JLogger implements org.jcrontab.log.Logger {
	
	private static org.apache.logging.log4j.Logger log = LogManager.getLogger("jcrontab");

	/**
	 *	This method does the basic initialization. 
	 */
	public void init(){
		//Just for 
	}
	/**
	 *	This method reports a message to the log 
	 */
	public void info(String message){
		log.info( message );
	}
	/**
	 *	This method reports a Exception or Error to the log  
	 */
	public void error(String message, Throwable t) {
		log.error( message, t );
	}
	/**
	 *	This method reports a debug level message to the log
	 */
	public void debug(String message){
		log.debug( message );
	}
}
