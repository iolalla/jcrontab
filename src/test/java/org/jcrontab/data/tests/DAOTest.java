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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

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
        /**/
    	System.out.println("--------SETUP---------");
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
        CrontabEntryDAO instanceTmp = CrontabEntryDAO.getInstance();
		instanceTmp.store(ceb);
        System.out.println("......setup......");
	}
	protected void tearDown() throws Exception {
		System.out.println("--------tearDown---------");
        // clear all
        CrontabEntryDAO instance = CrontabEntryDAO.getInstance();
		CrontabEntryBean[] findAll = instance.findAll();
		instance.remove(findAll);		
		//this.setUp();
		System.out.println("......-tearDown----......");
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
    
    public void testCRONTAB() throws Exception {
        CrontabEntryDAO i = CrontabEntryDAO.getInstance();
		// open the file
		final InputStream fis = this.getClass().getClassLoader().getResourceAsStream("crontab");
		BufferedReader input = new BufferedReader( new InputStreamReader(fis));
		// read test cron
		String strLine;
		List<String> listOfLines = new ArrayList<String>();
		while ((strLine = input.readLine()) != null) {
			//System.out.println(strLine);
			strLine = strLine.trim();
			listOfLines.add(strLine);
		}
		input.close();
		fis.close();
		// parce test cron
		final CrontabEntryBean[] listOfBeansTypeDef = new CrontabEntryBean[] {};
		List<CrontabEntryBean> listOfBeans = new ArrayList<CrontabEntryBean>();
		StringBuffer sb = new  StringBuffer();
		for ( String lineTmp:listOfLines) {
			
			// Skips blank lines and comments
			if (lineTmp.equals("")  ) {
				 // skip
			} else if ( lineTmp.charAt(0) == '#') {
				sb.append(lineTmp);
				sb.append("\n");
			} else {
				//System.out.println(strLines);
				final boolean[] bSeconds = new boolean[60];
				final boolean[] bYears = new boolean[10];
				CrontabEntryBean entry = cp.marshall(lineTmp);
				entry.setHeader(sb.toString());
				sb =  new StringBuffer();
				entry.setId(listOfBeans.size());
				cp.parseToken("*", bYears, false);
				entry.setBYears(bYears);
				entry.setYears("*");

				cp.parseToken("0", bSeconds, false);
				entry.setBSeconds(bSeconds);
				entry.setSeconds("0");

				listOfBeans.add(entry);
			}
		}
		// store
		i.store(listOfBeans.toArray(listOfBeansTypeDef));
		// load back
		ArrayList <CrontabEntryBean> storedCrons = new ArrayList<CrontabEntryBean>();
		storedCrons.addAll(  Arrays.asList( i.findAll()) );
		for (CrontabEntryBean bean: listOfBeans ){
			try{
				assertTrue( "Bean "+bean+" not founded after store!", storedCrons .contains( bean ) );
			}catch(Throwable e){
				e.printStackTrace();
			}
		}
    }
    
    public void testAdd() throws Exception {
    	CrontabEntryBean[] cebTMP = new CrontabEntryBean[3];
        CrontabEntryDAO.getInstance().store(cebTMP);
    }
    
    public void testFind0() throws Exception {
        CrontabEntryBean ceb2 = CrontabEntryDAO.getInstance().find(ceb[0]);
        assertEquals(""+ceb[0]+" !="  +ceb2, true, ceb[0].equals(  ceb2) );
    }
    
    public void testFind1() throws Exception {
        CrontabEntryBean ceb2 = CrontabEntryDAO.getInstance().find(ceb[1]);
        assertEquals(""+ceb[1]+" !="  +ceb2, true, ceb[1].equals(  ceb2) ); 
    }
    
    public void testFind2() throws Exception {
        CrontabEntryBean ceb2 = CrontabEntryDAO.getInstance().find(ceb[2]);
        assertEquals(""+ceb[2]+" !="  +ceb2, true, ceb[2].equals(  ceb2) ); 
    }
    
    public void testFindAll() throws Exception {
        CrontabEntryBean[] ceb2 = CrontabEntryDAO.getInstance().findAll();
        assertEquals(ceb2.length, 17);
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
    	 CrontabEntryBean[] ceb3 = CrontabEntryDAO.getInstance().findAll();
    	 int sizeAtStart = ceb3.length;
	     CrontabEntryBean[] ceb2 = {ceb[2]};
         CrontabEntryDAO.getInstance().remove(ceb2);
         ceb3 = CrontabEntryDAO.getInstance().findAll();
         assertEquals(ceb3.length, sizeAtStart-1);
         for (CrontabEntryBean cTmp :ceb3){
        	 assertFalse( "ceb[2]!!!"+ceb[2],cTmp.equals( ceb[2] ) );
         }
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
