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

package org.jcrontab.data;

import org.jcrontab.Crontab;
import org.jcrontab.log.Log;

/**
 * This Factory builds a dao using teh given information.
 * Initializes the system with the given properties or 
 * loads the default config
 * @author $Author: iolalla $
 * @version $Revision: 1.1 $
 */

public class ProcessFactory {
	
    private static ProcessFactory instance;
    
    private static ProcessSource dao = null;
	
	/**
	 *	Default Constructor private
	 */
    private ProcessFactory() {
	   if ( dao == null) {
		try {
		 dao = ((ProcessSource)Class.forName(Crontab.getInstance()
                                    .getProperty("org.jcrontab.data.processsource"))
                                    .newInstance())
                                    .getInstance();
		} catch (Exception e) {
		    Log.error(e.toString(), e);
		}
	   }
    }
    	/** 
	 * This method returns the DataFactory of the System This method
	 * grants the Singleton pattern
	 * @return DataSource
 	 */
    public synchronized static ProcessFactory getInstance() {
		if (instance == null) {
			instance = new ProcessFactory();
		}
		return instance;
    }
	
	/** 
	 * This method returns the ProcessSource of the System
	 * @return ProcessSource
	 */

    public static ProcessSource getDAO() {
        return dao;
    }
}
