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

import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.jcrontab.Crontab;

/** This class integrates Jcrontab as scheduler for Turbine.
 * To get info from jakarta-turbine plz refer to http://jakarta.apache.org/turbine/
 *@author     Eric Pugh  epugh@upstate.com
 *@created    May 3, 2002
 */
public class TurbineJcrontabScheduler extends TurbineBaseService {

	private Crontab crontab = null;
	private static Log log = LogFactory.getLog(TurbineJcrontabScheduler.class);
	
	static final String SERVICE_NAME = "JCrontab";


	/**  Constructor for the TurbineJcrontabScheduler object */
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
	 *  It expects a property "services.JCrontab.properties.file" property to 
	 *  be loaded by Turbine that points to the jcrontab-properties.cfg file.
	 *  Ie: 
	 * services.JCrontab.classname=org.jcrontab.TurbineJcrontabScheduler
	 * services.JCrontab.properties.file= /WEB-INF/conf/jcrontab-properties.cfg
     * services.JCrontab.earlyInit=true
     * 
     * In your jcrontab-properties.cfg file, any relative paths should be like
     * this: org.jcrontab.data.file = ${applicationRoot}/web-inf/conf/jcrontab-events.cfg
     * 
     * ${applicationRoot} is replaced with Turbines application root!
	 *
	 *@param  config                       A ServletConfig.
	 *@exception  InitializationException  Description of Exception
	 */
	public void init(  )
		throws InitializationException {
		try {
			log.info( "TurbineJcrontabSchedulerService init()....starting!" );

			String crontabPropertyFile = Turbine.getRealPath( Turbine.getConfiguration().getString( "services.JCrontab.properties.file" ) );
			Configuration configuration = (Configuration) new PropertiesConfiguration(crontabPropertyFile);

			// We want to set a few values in the configuration so
			// that ${variable} interpolation will work for us
			//
			// ${applicationRoot}
			configuration.setProperty(TurbineConstants.APPLICATION_ROOT_KEY, Turbine.getApplicationRoot());
			

			log.info( "Starting Crontab with path:" + crontabPropertyFile );
			crontab = Crontab.getInstance();
			
			Properties propObj = ConfigurationConverter.getProperties(configuration);
						
			crontab.init( propObj );
			setInit( true );
			log.info( "TurbineJcrontabSchedulerService init()....finished!" );
		}
		catch ( Exception e ) {
			throw new InitializationException( "TurbineJcrontabSchedulerService failed to initialize", e );
		}

	}


	/**  Called when the application is shutting down. */
	public void shutdown() {
		try {
			crontab.uninit( 10 );
            log.info( "TurbineJcrontabSchedulerService shutdown()....finished!" );

		}
		catch ( Exception e ) {
			log.error( "Cannot shutdown TurbineJcrontabSchedulerService!: " + e );
		}

	}

}

