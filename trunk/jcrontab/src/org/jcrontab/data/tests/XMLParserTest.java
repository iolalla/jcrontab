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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.io.StringReader;
import org.xml.sax.InputSource;
import org.jcrontab.Crontab;
import org.jcrontab.data.CrontabEntryBean;
import org.jcrontab.data.XMLParser;
import org.jcrontab.data.CrontabParser;
import org.jcrontab.data.DataNotFoundException;

/**
 * This class tests the XML parser...
 *
 */
public class XMLParserTest extends TestCase {
    
    private CrontabParser cp = new CrontabParser();
    
    static private Crontab crontab = null;
    
    private CrontabEntryBean[] ceb = new CrontabEntryBean[3];
    
    private String result = null;
    
    private XMLParser xmlParser = null;
    
    
	public XMLParserTest(String name) {
		super(name);
	}
	
    protected void setUp() throws Exception {

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
        
        xmlParser = new XMLParser();
	}
    
    public static Test suite() {
		return new TestSuite(XMLParserTest.class);
	}

    public static void main(String[] args ) throws Exception {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }

    public void testMarshall() throws Exception {
        result = xmlParser.marshall(ceb);
        assertFalse(result.equals(""));
        assertFalse(result.equals(null));
    }

    public void testUnMarshall() throws Exception {
        result = xmlParser.marshall(ceb);
        StringReader strReader = new StringReader(result);
        InputSource is = new InputSource(strReader);
        CrontabEntryBean[] cebs = xmlParser.unMarshall(is);
    }
    
   
    /** Uncomment this if you want to see the resulting xml
    public void testStringResult() throws Exception {
        result = xmlParser.marshall(ceb);
    }
    */
}
