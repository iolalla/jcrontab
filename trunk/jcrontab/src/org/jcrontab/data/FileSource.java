/*
 *  This file is part of the jcrontab package
 *  Copyright (C) 2001 Israel Olalla
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

import java.io.PrintStream;
import java.util.Vector;
import java.util.Properties;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;

import org.jcrontab.Cron;


/**
 *
 * @author  iolalla
 */
public class FileSource implements DataSource {

    private static FileSource instance;
    
    private static Properties props = new Properties();
	
    private static String config_file = "events.cfg";
    
	private static String store_file = 
            "war/WEB-INF/classes/org/jcrontab/events.cfg";
    
    /** Creates new FileSource */
	
    public FileSource() {
    }	

    public DataSource getInstance() {
		if (instance == null) {
		instance = new FileSource();
		}
		return instance;
    }
    
    public void init(Properties props) {
	
		this.props = props;
		/*
		 *	Those lines are added to grant default values 
		 *  and to avoid anoying Exceptions and errors in 
		 *  properties Files
		 */
		if (props.getProperty("config_file") == null) 
				props.setProperty("config_file", config_file);
		if (props.getProperty("store_file") == null) 
				props.setProperty("store_file", store_file);
    }

    
    public CrontabEntryBean find(CrontabEntryBean ceb) throws CrontabEntryException, 
			IOException, DataNotFoundException {
        	CrontabEntryBean[] cebra = findAll();
	                for (int i = 0; i < cebra.length ; i++) {
			    if (cebra[i].equals(ceb)) {
			         return cebra[i];
	      		}
	 	} 
		throw new DataNotFoundException( " Unable to find : "  + ceb.getId());
    }
    
    public CrontabEntryBean[] findAll() throws CrontabEntryException, 
			IOException {
            Vector listOfBeans = new Vector();
            Class cla = FileSource.class;
            // BufferedReader input = new BufferedReader(new FileReader(strFileName));
            // This Line allows the events.cfg to be included in a jar file
            // and accessed from anywhere
            BufferedReader input = new BufferedReader(
            new InputStreamReader(cla.getResourceAsStream(
				props.getProperty("config_file"))));
            
            String strLine;
            
            while((strLine = input.readLine()) != null){
            strLine = strLine.trim();
            // Skips blank lines and comments
            if(strLine.equals("") || strLine.charAt(0) == '#')
                continue;
            CrontabEntryBean entry = new CrontabEntryBean(strLine);
            listOfBeans.add(entry);
            }
            input.close();
            
            int sizeOfBeans = listOfBeans.size();
            if ( sizeOfBeans == 0 ){
                throw(new CrontabEntryException() );
            }
            else{
                CrontabEntryBean[] finalBeans = 
                    new CrontabEntryBean[sizeOfBeans];
                for (int i = 0; i < sizeOfBeans; i++)
                {
                    //Added to have different Beans identified
                    finalBeans[i] = (CrontabEntryBean)listOfBeans.get(i);
                    finalBeans[i].setId(i);
                }
                return finalBeans;
            }
            
    	}
    
    	public void remove(CrontabEntryBean[] ceb) throws Exception {
    	
            CrontabEntryBean[] thelist = findAll();
	    CrontabEntryBean[] result = new CrontabEntryBean[thelist.length -  ceb.length];
	    CrontabEntryBean nullCeb = new CrontabEntryBean();
	    nullCeb.setId(0);
	    for (int i = 0; i < thelist.length ; i++) {
		    for (int y = 0; y < ceb.length ; y++) {
			    if (thelist[i].equals(ceb[y])) {
				    thelist[i] = nullCeb;
			    } 
		    } 
	    }
	    int resultCounter = 0;
	    for (int i = 0; i < thelist.length ; i++) {
		    if(!thelist[i].equals(nullCeb)) {
			result[resultCounter] = thelist[i];
			resultCounter++;
	            }
	    }
            storeAll(result);	
	}
    
	/**
	 *	This private method serves to store the information of all the
	 * CrontabEntryBeans. This method solves the problem of accessing from
	 * different medthods to he file saves to repeat the same logic all the time.
	 * And saves time  to write to file
	 */
    	private void storeAll(CrontabEntryBean[] list) throws 
               CrontabEntryException, FileNotFoundException, IOException {

		    File fl = new File(props.getProperty("store_file"));
		    PrintStream out = new PrintStream(new FileOutputStream(fl));
	    	    CrontabEntryBean nullCeb = new CrontabEntryBean();
	            nullCeb.setId(0);

            for (int i = 0; i < list.length; i++){
		    	if (!list.equals(nullCeb)) 
                 	out.println(list[i].getLine());
            }
        
	}
	/**
	 *	This method saves the CrontabEntryBean the actual problem with this
	 *  method is that doesn´t store comments and blank lines from the original
	 *  file any ideas?
	 *  @param CrontabEntryBean bean this method only lets store an entryBean
	 *  each time.
	 *  @throws CrontabEntryException when it can't parse the line correctly
	 */
	public void store(CrontabEntryBean[] beans) throws CrontabEntryException, 
			IOException, DataNotFoundException {
            
            CrontabEntryBean[] thelist = findAll();
            int size = (thelist.length +1 );
            
            CrontabEntryBean[] resultlist = new CrontabEntryBean[size];
            Vector ve = new Vector();
            for (int i = 0; i < thelist.length; i++){
                ve.add(thelist[i]);
            }
			for (int i = 0; i < beans.length; i++) {
				ve.add(beans[i]);
			}
            for (int i = 0; i < ve.size(); i++){
                resultlist[i] = (CrontabEntryBean)ve.get(i);
            }
            storeAll(resultlist);
	}
	
	/**
	 *	This method saves the CrontabEntryBean the actual problem with this
	 *  method is that doesn´t store comments and blank lines from the original
	 *  file any ideas?
	 *  @param CrontabEntryBean bean this method only lets store an entryBean
	 *  each time.
	 *  @throws E
	 */
	public void store(CrontabEntryBean bean) throws CrontabEntryException, 
			IOException, DataNotFoundException {
            
            CrontabEntryBean[] thelist = findAll();
            int size = (thelist.length +1 );
            
            CrontabEntryBean[] resultlist = new CrontabEntryBean[size];
            Vector ve = new Vector();
            for (int i = 0; i < thelist.length; i++){
                ve.add(thelist[i]);
            }
            ve.add(bean);
            for (int i = 0; i < ve.size(); i++){
                resultlist[i] = (CrontabEntryBean)ve.get(i);
            }
            storeAll(resultlist);
	}
}
