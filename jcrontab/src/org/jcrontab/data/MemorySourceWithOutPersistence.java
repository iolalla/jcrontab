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

import java.util.*;
/**
 * This class is here to make easier to use Jcrontab in enviroments without HD
 * or programmatic enviroments like testing. Basically is here to make easier to 
 * test the new guis
 * @author $Author: iolalla $
 * @version $Revision: 1.1 $
 */
public class MemorySourceWithOutPersistence implements DataSource {
    
    /** This variable is the basic structure to store the beans in RAM 
     * Maybe in the future will save this in disk
     */
    private Map beans;
    /** This variable is here to get the singleton working */
    private MemorySourceWithOutPersistence instance = null;
    /** Private constructor to get the singleton working */
    private MemorySourceWithOutPersistence() {
        beans = Collections.synchronizedMap(new HashMap());
    }
    /** Private constructor to get the singleton working */
    public synchronized DataSource getInstance() {
        if (instance == null) {
		instance = new MemorySourceWithOutPersistence();
		}
		return instance;
    }
    /**
	 *	searches  the CrontabEntryBean from the DataSource
	 * @return CrontabEntryBean
	 * @throws Exception
	 */
    public CrontabEntryBean find(CrontabEntryBean ceb) throws Exception {
        return (CrontabEntryBean)beans.get(ceb);
    }
    /**
	 *	Gets all the CrontabEntryBean from the DataSource
	 * @return CrontabEntryBean[]
	 * @throws Exception
	 */
    public CrontabEntryBean[] findAll() throws Exception {
        CrontabEntryBean[] results = new CrontabEntryBean[beans.size()];
        Collection coll = beans.values();
        return(CrontabEntryBean[]) coll.toArray(new CrontabEntryBean[0]);
    }
    /**
	 *	stores CrontabEntryBean in  the DataSource
	 * @param CrontabEntryBean
	 * @throws Exception
	 */
    public void store(CrontabEntryBean ceb) throws Exception {
        beans.put(ceb, ceb);
    }
     /**
	 *	stores a CrontabEntryBean array in  the RAM
	 * @param CrontabEntryBean[]
	 * @throws Exception
	 */
    public void store(CrontabEntryBean[] ceb) throws Exception {
        for (int i = 0; i < ceb.length; i++) {
            beans.put(ceb[i], ceb[i]);
        }
    }
    /**
	 * removes a CrontabEntryBean array from  the RAM
	 * @param CrontabEntryBean[]
	 * @throws Exception
	 */
    public void remove(CrontabEntryBean[] ceb) throws Exception {
        for (int i = 0; i < ceb.length; i++) {
            beans.remove(ceb[i]);
        }
    }
}
