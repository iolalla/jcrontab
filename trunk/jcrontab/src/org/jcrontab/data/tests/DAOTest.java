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
 
package org.jcrontab.data.tests;

import junit.framework.*;
import org.jcrontab.data.*;
import org.jcrontab.Crontab;

/**
 * In order to run this test you need a void Database and GenericSQL DataSource
 *
 */
public class DAOTest extends TestCase {
    
    private CrontabParser cp = new CrontabParser();
    
    private CrontabEntryBean ceb = new CrontabEntryBean();
    
	public DAOTest(String name) {
		super(name);
	}
	
    protected void setUp() throws Exception {
        
        ceb = cp.marshall("* * * * * org.jcrontab.tests.test testing");
        ceb.setYears("*");
        ceb.setSeconds("0");
        ceb.setBusinessDays(true);
        

        
	}
	
    
    public static Test suite() {
		return new TestSuite(DAOTest.class);
	}
    
    public static void main(String[] args ) throws Exception {
        Crontab crontab = Crontab.getInstance();
        crontab.getInstance().init();
       junit.textui.TestRunner.run(suite());
       System.exit(0);
    }
    
    public void testAdd() throws Exception {
        CrontabEntryDAO.getInstance().store(ceb);
    }
    
    public void testFind() throws Exception {
        CrontabEntryBean ceb2 = CrontabEntryDAO.getInstance().find(ceb);
        assertEquals(ceb, ceb2);

    }
    
    public void testFindAll() throws Exception {
        CrontabEntryBean[] ceb2 = CrontabEntryDAO.getInstance().findAll();
        assertEquals(ceb2.length, 1);
    }
    
    public void testRemove()  throws Exception {
        CrontabEntryBean[] ceb2 ={ceb};
         CrontabEntryDAO.getInstance().remove(ceb2);
         try {
         CrontabEntryBean[] ceb3 = CrontabEntryDAO.getInstance().findAll();
         } catch (DataNotFoundException dnfe) {
             assertEquals(dnfe.toString(), "Unable to find :" + ceb);
         }
    }

}
