/**
 *  This file is part of the jcrontab package
 *  Copyright (C) 2001-2002 Israel Olalla
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

import junit.framework.*;
import org.jcrontab.data.*;
/**
 * Some simple tests.
 *
 */
public class SimpleTest extends TestCase {
	public SimpleTest(String name) {
		super(name);
	}
	protected void setUp() {
	}
	public static Test suite() {
		/*
		 * the type safe way
		 *
		TestSuite suite= new TestSuite();
		suite.addTest(
			new SimpleTest("add") {
				 protected void runTest() { testAdd(); }
			}
		);

		suite.addTest(
			new SimpleTest("testDivideByZero") {
				 protected void runTest() { testDivideByZero(); }
			}
		);
		return suite;
		*/

		/*
		 * the dynamic way
		 */
		return new TestSuite(SimpleTest.class);
	}
    
	public void testDAO() throws Exception {
        CrontabEntryBean[] listOfBeans= CrontabEntryDAO.getInstance().findAll();   
        System.out.println("You have "+listOfBeans.length+"in your crontab");
        CrontabEntryDAO.getInstance().store(listOfBeans);
        System.out.println("You have stored "+listOfBeans.length+"in your crontab");
    }

    
    public void testCalendarBuilder() throws Exception  {
        CrontabEntryBean[] listOfBeans= CrontabEntryDAO.getInstance().findAll(); 
        CalendarBuilder calb = new CalendarBuilder();
        CrontabEntryBean nextb = calb.getNextEvent(listOfBeans);
        System.out.println("this is the next Bean" + nextb);
    }
}