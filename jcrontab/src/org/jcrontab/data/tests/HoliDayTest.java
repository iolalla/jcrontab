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
import org.jcrontab.data.HoliDay;
/**
 * Some simple tests.
 *
 */
public class HoliDayTest extends TestCase {

	public HoliDayTest(String name) {
		super(name);
	}
	
    protected void setUp() throws Exception {
	}
	
    
    public static Test suite() {
		return new TestSuite(HoliDayTest.class);
	}
    
    public static void main(String[] args ) {
       
       junit.textui.TestRunner.run(suite());
       System.exit(0);
    }
    
    public void testSet() {
        HoliDay holiday = new HoliDay();
        int id = 0;
        holiday.setId(id);
        assertEquals(id, holiday.getId());
    }
    
    public void testGet() {
        HoliDay holiday = new HoliDay();
        int id = 0;
        holiday.setId(id);
        assertEquals(id, holiday.getId());
    }
    
    public void testEquals() {
        HoliDay holiday = new HoliDay();
        int id = 0;
        holiday.setId(id);
        HoliDay holiday2 = new HoliDay();
        holiday2.setId(id);
        assertEquals(holiday2, holiday);
    }
    
    public void testToString() {
        HoliDay holiday = new HoliDay();
        int id = 0;
        holiday.setId(id);
        System.out.println(holiday);
    }
    
}
