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
    
    static private Crontab crontab = null;
    
    private CrontabEntryBean[] ceb = new CrontabEntryBean[3];
    
	public DAOTest(String name) {
		super(name);
	}
	
    protected void setUp() throws Exception {
        /**
         crontab = Crontab.getInstance();
         crontab.getInstance().init();
         */
        ceb[0] = cp.marshall("* * * * * org.jcrontab.tests.test testing");
        ceb[0].setYears("*");
        ceb[0].setSeconds("0");
        ceb[0].setBusinessDays(true);
        ceb[0].setId(0);
        
        ceb[1] = cp.marshall("* * * * * org.jcrontab.tests.test testing 2");
        ceb[1].setYears("*");
        ceb[1].setSeconds("0");
        ceb[1].setBusinessDays(true);
        ceb[1].setId(1);
        
        ceb[2] = cp.marshall("* * * * * org.jcrontab.tests.test testing 3");
        ceb[2].setYears("*");
        ceb[2].setSeconds("0");
        ceb[2].setBusinessDays(true);
        ceb[2].setId(2);
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
    
    public void testFind0() throws Exception {
        CrontabEntryBean ceb2 = CrontabEntryDAO.getInstance().find(ceb[0]);
        assertEquals(ceb[0], ceb2);
    }
    
    public void testFind1() throws Exception {
        CrontabEntryBean ceb2 = CrontabEntryDAO.getInstance().find(ceb[1]);
        assertEquals(ceb[1], ceb2);
    }
    
    public void testFind2() throws Exception {
        CrontabEntryBean ceb2 = CrontabEntryDAO.getInstance().find(ceb[2]);
        assertEquals(ceb[2], ceb2);
    }
    
    public void testFindAll() throws Exception {
        CrontabEntryBean[] ceb2 = CrontabEntryDAO.getInstance().findAll();
        assertEquals(ceb2.length, 3);
    }
    /**
    public void testRemove()  throws Exception {
         CrontabEntryDAO.getInstance().remove(ceb);
         try {
         CrontabEntryBean[] ceb3 = CrontabEntryDAO.getInstance().findAll();
         } catch (DataNotFoundException dnfe) {
             assertEquals(dnfe.toString(), "org.jcrontab.data.DataNotFoundException: "
             + "No CrontabEntries available");
         }
    }
    */
    public void testRemove1()  throws Exception {
	     CrontabEntryBean[] ceb2 = {ceb[2]};
         CrontabEntryDAO.getInstance().remove(ceb2);
         CrontabEntryBean[] ceb3 = CrontabEntryDAO.getInstance().findAll();
         assertEquals(ceb3.length, 2);
    }

    public void testRemove2()  throws Exception {
	     CrontabEntryBean[] ceb2 = {ceb[0],ceb[1]};
         CrontabEntryDAO.getInstance().remove(ceb2);
         try {
         CrontabEntryBean[] ceb3 = CrontabEntryDAO.getInstance().findAll();
         } catch (DataNotFoundException dnfe) {
             assertEquals(dnfe.toString(), "org.jcrontab.data.DataNotFoundException: "
             + "No CrontabEntries available");
         }
    }
}
