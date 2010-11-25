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
package org.jcrontab.tests;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import junit.framework.*;
import org.jcrontab.data.*;
import org.jcrontab.Crontab;
/**
 * Some simple tests.
 *
 */
public class SimpleTest extends TestCase {
    
     private CrontabParser cp = new CrontabParser();
    
    static private Crontab crontab = null;

    private CrontabEntryBean[] ceb = new CrontabEntryBean[3];

    public SimpleTest(String name) {
	super(name);
    }

    protected void setUp() throws Exception {
        /**/
         crontab = Crontab.getInstance();
         
        Properties props = new Properties();
         
		 
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("jcrontab.test.properties");
		Reader inStream = new InputStreamReader(in );
		props.load(inStream );
		crontab.init(props);
         
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
        // clear all
        //CrontabEntryBean[] findAll = CrontabEntryDAO.getInstance().findAll();
		//CrontabEntryDAO.getInstance().remove(findAll);
        // init 3 tasks
        CrontabEntryDAO.getInstance().store(ceb);
        
	}


    
    
    
    public static Test suite() {
		return new TestSuite(SimpleTest.class);

	}

    

    public static void main(String[] args ) {
       junit.textui.TestRunner.run(suite());
       System.exit(0);
    }

    

	public void testDAOAdd() throws Exception {
        CrontabEntryDAO.getInstance().store(ceb);   
	}

    public void testDAOFindAll() throws Exception {
        CrontabEntryBean[] listOfBeans= CrontabEntryDAO.getInstance().findAll();
        assertEquals(listOfBeans.length, 17);
	}
    

    public void testNextBeanToExecute() throws Exception  {
        CrontabEntryBean[] listOfBeans= CrontabEntryDAO.getInstance().findAll(); 
        CalendarBuilder calb = new CalendarBuilder();
        CrontabEntryBean nextb = calb.getNextCrontabEntry(listOfBeans);
        System.out.println("this is the next Bean \n" + nextb.toXML());
    }

    

    public void testCrontabParser() throws Exception {
        CrontabParser cp = new CrontabParser();
        CrontabEntryBean ceb = cp.marshall("* * * * * org.jcrontab.tests.TaskTest");
        System.out.println("this is the bean resulting from " +
                           " * * * * * org.jcrontab.tests.TaskTest \n" + 
                           ceb.toXML());
    }

	protected void tearDown() throws Exception {
        // clear all
        CrontabEntryDAO instance = CrontabEntryDAO.getInstance();
		CrontabEntryBean[] findAll = instance.findAll();
		instance.remove(findAll);		
		//this.setUp();
	}
}
