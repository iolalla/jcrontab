/**
 *  This file is part of the jcrontab package
 *  Copyright (C) 2001-2004 Israel Olalla
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

import org.jcrontab.log.Log;

/**
 * This class is a proxy to make it easier to access the DataSources from,
 * anywhere. This class should be used only to manage the DAOs from the diferent
 * Jcrontab's guis, but who knows maybe in there's people that want to use 
 * this proxy to access to the CrontabEntryBeans.
 * I've choosen a Proxy to make the DAOs accesible withou having to change 
 * @author $Author: iolalla $
 * @version $Revision: 1.1 $
 */

public class DataSourceProxy {
    
    private DataSource dataSource = null;
    /**
	 *	returns the only valid DataSourceProxy of this kind
	 * @return DataSource
	 */
     public DataSourceProxy(String DAOClassName) {
         try {
		 dataSource = ((DataSource)Class.forName(DAOClassName)
                                    .newInstance())
                                    .getInstance();
		} catch (Exception e) {
		    Log.error(e.toString(), e);
		}
     }
     /**
	 *	returns a valid DataSource
	 * @return DataSource
	 */
      public DataSource getDataSource() {
          return dataSource;
      }
}


