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
 *  iolalla@yahoo.com
 *
 */

package org.jcrontab;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.resources.TurbineResources;
import org.jcrontab.log.Log;

/** This class integrates Jcrontab as sheduler for Turbine.
 * To get info from jakarta-turbine plz refer to http://jakarta.apache.org/turbine/
 * @author $Author: iolalla $ Eric Pugh
 * @version $Revision: 1.1 $
 */

public class TurbineJcrontabScheduler extends TurbineBaseService {

	private Crontab crontab = null;


	/**  Constructor for the TurbineJCrontabScheduler object */
	public TurbineJcrontabScheduler() { }


	/**
	 *  Gets the scheduler attribute of the JCrontabScheduler object
	 *
	 *@return    The scheduler value
	 */
	public Crontab getScheduler() {
		return crontab;
	}

	/**
	 *  Called the first time the Service is used.<br>
	 *  Load all the jobs from cold storage. Add jobs to the queue (sorted in
	 *  ascending order by runtime) and start the scheduler thread.
	 *
	 *@param  config                       A ServletConfig.
	 *@exception  InitializationException  Description of Exception
	 */
	public void init( ServletConfig config )
		throws InitializationException {
		try {
			Log.info( "TurbineJCrontabSchedulerService init()....starting!" );

			String crontabPropertyFile = config.getServletContext()
											   .getRealPath( 
											   TurbineResources.getString(
									"services.JCrontab.properties.file"));

			Log.info( "Starting Crontab with path:" + crontabPropertyFile );
			crontab = Crontab.getInstance();
			Properties propObj = new Properties();

			FileInputStream input = new FileInputStream( crontabPropertyFile );
			propObj.load( input );

			crontab.init( propObj );
			setInit( true );
			Log.info( "TurbineJCrontabSchedulerService init()....finished!" );
		} catch ( Exception e ) {
			throw new InitializationException("TurbineJCrontabSchedulerService "
                                              + " failed to initialize", e );
		}
	}

	/**  Called when the application is shutting down. */
	public void shutdown() {
		try {
			crontab.uninit( 200 );
		}
		catch ( Exception e ) {
			Log.error( "Cannot shutdown TurbineJCrontabSchedulerService!: " + 
                       e.toString(), e );
		}
	}
}

