/**
 *  This file is part of the jcrontab package
 *  Copyright (C) 2001-2002 Israel Olalla
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

import org.gjt.sp.util.Log;
import org.jcrontab.Crontab;
/**
 * This class helps the testing process to make easier testing
 * @author $Author: iolalla $
 * @version $Revision: 1.1 $
 */
public class jEditLogger implements Logger {
	
	/**
	 *	This method does the basic initialization. 
	 */
	public void init(){}
	/**
	 *	This method reports a message to the log 
	 */
	public void info(String message){
		org.gjt.sp.util.Log.log(Log.MESSAGE, Thread.currentThread().getClass(), message);
	}
	/**
	 *	This method reports a Exception or Error to the log  
	 */
	public void error(String message, Throwable t) {
		org.gjt.sp.util.Log.log(Log.ERROR, Thread.currentThread().getClass(), message);
		org.gjt.sp.util.Log.log(Log.ERROR, Thread.currentThread().getClass(), t);
	}
	/**
	 *	This method reports a debug level message to the log
	 */
	public void debug(String message){
		org.gjt.sp.util.Log.log(Log.DEBUG, Thread.currentThread().getClass(), message);
	}
}
