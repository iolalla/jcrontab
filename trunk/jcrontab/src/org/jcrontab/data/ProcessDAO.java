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


/***
 * This DAO Gives all the methods necesary to build Processes
 * This class is an abstraction to make esaier the integration of new
 * ProcessSources that help to access Processes in new ways
 * @author $Author: iolalla $
 * @version $Revision: 1.1 $
 */
public class ProcessDAO {
	/**
	 *This insntance grants only an instance of this DAO in
	 * every system 
	 */
	private static ProcessDAO instance;

	/** This ProcessSource is the reason of this class */
	private static ProcessSource dao = null;
	
	/**
	 * Default constructor This one initializes everything maybe 
	 * could use lazy inizialization
	 */
	private ProcessDAO() {
		   if ( dao == null) {
				try {
				dao = ProcessFactory.getInstance().getDAO();
				} catch (Exception e) {
					Log.error(e.toString(), e);
				}
		   }
	}	
    /**
	 *	This method returns the singleton is very important to grant
	 *  That only a Thread accesses at a time
	 */
	public synchronized static ProcessDAO getInstance() {
		if (instance == null) {
            instance = new ProcessDAO();
		}
		return instance;
	}
	/**
	 *	Gets all the Process from the ProcessSource
	 * @return Process[]
	 * @throws Exception
	 */
	public Process[] findAll() throws Exception {
		return dao.findAll();
	}
   	/**
	 *	searches  the Process from the ProcessSource
	 * @return Process
	 * @throws Exception
	 */
	public Process find(Process ps) throws Exception {
		return dao.find(ps);
	}
   	/**
	 *	stores Process in  the ProcessSource
	 * @param Process list
	 * @throws Exception
	 */
	public void store(Process[] list) throws Exception {
		dao.store(list);
	}
   	/**
	 * stores Process in  the ProcessSource
	 * @param Process
	 * @throws Exception
	 */
	public void store(Process bean) throws Exception {
        Process[] ps = {bean};
        store(ps);
	}
    /**
	 * removes only one Process Process in  the ProcessSource
	 * @param Process
	 * @throws Exception
	 */
    public void remove(Process bean) throws Exception {
        Process[] ps = {bean};
        remove(ps);
    }
   	/**
	 * removes Process from the ProcessSource
	 * @param Process[]
	 * @throws Exception
	 */
	public void remove(Process[] list) throws Exception {
		dao.remove(list);
	}
}
