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
package org.jcrontab.data;

import org.jcrontab.data.XMLParser;
import org.jcrontab.Crontab;
import java.io.*;

/**
 * This class Is the implementation of DataSource to access 
 * Info in a XML files format
 * @author $Author: iolalla $
 * @version $Revision: 1.3 $
 */
public class XMLSource implements DataSource {
	
	private CrontabParser cp = new CrontabParser();

	private static XMLSource instance;
    
	private CrontabEntryBean[] cachedBeans = null;
	
	protected long lastModified;
	
	private String crontab_xml_file = "crontab.xml";
    
    private XMLParser xmlParser = new XMLParser();
    
    /** 
    * java -cp d:\jcrontab\jar\xerces.jar;d:\jcrontab\jar\jcrontab.jar;. *-Dorg.xml.sax.driver=org.apache.xerces.parsers.SAXParser -Xdebug -Xnoagent *-Djava.compiler=NO
	* Creates new XMLSource and does the logic necesary to parse the files
	*/
    protected XMLSource() {
			if (Crontab.getInstance().getProperty(
								"org.jcrontab.data.file") == null) {
				Crontab.getInstance().setProperty(
								"org.jcrontab.data.file", crontab_xml_file);
           } else {
               crontab_xml_file = Crontab.getInstance().getProperty(
								"org.jcrontab.data.file");
           }
           if (System.getProperty("org.xml.sax.driver") == null)  {
               String parser = "org.apache.xerces.parsers.SAXParser";
               if (Crontab.getInstance().getProperty("org.xml.sax.driver") != null) {
				System.setProperty("org.xml.sax.driver", Crontab.getInstance().getProperty("org.xml.sax.driver"));
               } else {
                System.setProperty("org.xml.sax.driver", parser);
               }
           }
    }
    /**
	 *	This method returns the singleton is very important to grant
	 *  That only a Thread accesses at a time
	 */
    public synchronized DataSource getInstance() {
		if (instance == null) {
            instance = new XMLSource();
		}
		return instance;
    }
    /**
	 *	This method searches the given Bean  from the File
	 *  @return CrontabEntryBean beans Array the result of the search
	 *  @param CrontabEntryBean the CrontabEntryBean you want to search
	 *  @throws CrontabEntryException when it can't parse the line correctly
	 *  @throws IOException If it can't access correctly to the File
	 *  @throws DataNotFoundException whe it can't find nothing in the file 
	 */
    public synchronized CrontabEntryBean find(CrontabEntryBean ceb) 
    	throws Exception {
            CrontabEntryBean[] cebra = xmlParser.unMarshall(crontab_xml_file);
            for (int i = 0; i < cebra.length ; i++) {
                if (cebra[i].equals(ceb)) {
                    return cebra[i];
                }
            }
            throw new DataNotFoundException("Unable to find " + ceb);
    }
   	/**
	 *	This method searches all the CrontabEntryBean from the File
	 *  @return CrontabEntryBean beans Array the result of the search
	 *  @throws CrontabEntryException when it can't parse the line correctly
	 *  @throws IOException If it can't access correctly to the File
	 *  @throws DataNotFoundException whe it can't find nothing in the file 
	 */
    public synchronized CrontabEntryBean[] findAll() throws Exception {
                return xmlParser.unMarshall(crontab_xml_file);
    }
    /**
	 *	This method removes the CrontabEntryBean array from the File
	 *  @param CrontabEntryBean bean teh array of beans to remove
	 *  @throws Exception 
	 */
    public synchronized void remove(CrontabEntryBean[] ceb) throws Exception {
        return;
	}
	/**
	 *	This method saves the CrontabEntryBean array the actual problem with this
	 *  method is that doesn�t store comments and blank lines from the original
	 *  file any ideas?
	 *  @param CrontabEntryBean bean this method stores the array of beans
	 *  @throws CrontabEntryException when it can't parse the line correctly
	 *  @throws IOException If it can't access correctly to the File
	 *  @throws DataNotFoundException whe it can't find nothing in the file usually 
	 *  Exception should'nt this 
	 */
    public synchronized void storeAll(CrontabEntryBean[] list) throws 
               Exception {
           String result = xmlParser.marshall(list);
           File fl = new File(crontab_xml_file);
		    PrintStream out = new PrintStream(new FileOutputStream(fl));
            out.print(result);
	}
	/**
	 *  This method saves the CrontabEntryBean array the actual problem with this
	 *  method is that doesn�t store comments and blank lines from the original
	 *  file any ideas?
	 *  @param CrontabEntryBean bean this method stores the array of beans
	 *  @throws CrontabEntryException when it can't parse the line correctly
	 *  @throws IOException If it can't access correctly to the File
	 *  @throws DataNotFoundException whe it can't find nothing in the file usually 
	 *  Exception should'nt this 
	 */
	public synchronized void store(CrontabEntryBean[] beans) throws Exception {
            CrontabEntryBean[] cebra = findAll();
            CrontabEntryBean[] results = new CrontabEntryBean[cebra.length + beans.length];
            for (int i = 0; i < results.length ; i++) {
                for (int y = 0; y < cebra.length ; y++)
                    results[i] = cebra[y];
                for (int j = 0; j < beans.length ; j++)
                    results[i] = beans[j];
            }
            storeAll(results);
	}
}
