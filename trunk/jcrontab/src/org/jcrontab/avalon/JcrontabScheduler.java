package org.jcrontab.avalon;

import org.jcrontab.Crontab;
import org.jcrontab.data.CrontabEntryDAO;

/**
 *  This file is part of the jcrontab package
 *  Copyright (C) 2001-2002 Eric Pugh and Israel Olalla
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
 *  epugh@upstate.com
 *
 */

/** 
 * This interface integrates Jcrontab as an Avalon component.  This can be easily used
 * in the Turbine environment as an Avalon Component.
 * To get info on Turbine please refer to http://jakarta.apache.org/turbine/
 * To get info on Avalon please refer to http://avalon.apache.org/
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 */
public interface JcrontabScheduler {

	/** Avalon Role Name */
	String ROLE = JcrontabScheduler.class.getName();
	
	/** The Key to use in your .properties file for relative urls.
	 *  This will be replaced with the actual application root.
	 * 
	 */
	public static final String APPLICATION_ROOT_KEY="applicationRoot";
	
	/**
	 *  Gets the scheduler attribute of the JCrontabScheduler object
	 *
	 *@return    The scheduler value
	 */
	public Crontab getCrontab();
	
	/** 
	 * Get the CrontabEntryDAO for looking at tasks	 
	 *
	 *@return    The CrontabEntryDAO.
	 */
	public CrontabEntryDAO getContrabEntryDAO();
	
	/** 
	 * Report whether JCrontab instance is actually
	 * running or not.	 
	 *
	 *@return    Whether it is running or not.
	 */
	public boolean isRunning();
}
