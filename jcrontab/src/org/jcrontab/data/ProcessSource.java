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

/**
 * This interface says which methods a ProcessSource should have in
 * order to be compatible with the ProcessFactory
 * @author $Author: iolalla $
 * @version $Revision: 1.1 $
 */

public interface ProcessSource {
         /**
	 * Returns the only valid ProcessSource of this kind
	 * @return ProcessSource
	 */
        abstract ProcessSource getInstance();
	/**
	 * Searches  the Process from the ProcessSource
	 * @return CrontabEntryBean
	 * @throws Exception
	 */
    	abstract Process find(Process ceb) throws Exception;
	/**
	 * Gets all the Process from the ProcessSource
	 * @return Process[]
	 * @throws Exception
	 */
	 Process[] findAll() throws Exception;
	 /**
	 * Stores Process in  the ProcessSource
	 * @param Process list
	 * @throws Exception
	 */
    	abstract void store(Process[] ceb) throws Exception;
	/**
	 * removes Process from the ProcessSource
	 * @param Process
	 * @throws Exception
	 */
    	abstract void remove(Process[] ceb) throws Exception;
}
