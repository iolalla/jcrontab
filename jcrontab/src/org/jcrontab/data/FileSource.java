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
 * @version 
 */
public class FileSource implements DataSource {

    private static FileSource instance;
    
    private static Properties props = new Properties();
	
    private static String config_file = "events.cfg";
    private static String store_file = 
            "war/WEB-INF/classes/org/jcrontab/events.cfg";
        
    
   // public static CrontabEntryBean[] crontabEntryList;
    
    /** Creates new FileSource */
	
    public FileSource() {
    }	

    public DataSource getInstance() {
		if (instance == null) {
		instance = new FileSource();
		}
		return instance;
    }
    
    public void init(Properties props) throws Exception {
	
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
    
    public CrontabEntryBean[] find(String cl) throws Exception {
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
            CrontabEntryBean entry = new CrontabEntryBean();
            entry.setLine(strLine);
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
    
    public CrontabEntryBean[] findAll() throws Exception {
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
    }
    
    public void storeAll(CrontabEntryBean[] list) throws 
               CrontabEntryException, FileNotFoundException, IOException {
        
       
        // BufferedReader input = new BufferedReader(new FileReader(strFileName));
	// This Line allows the events.cfg to be included in a jar file
	// and accessed from anywhere
        //Class cl = CrontabEntryDAO.class;
	//BufferedWriter out = new BufferedWriter(
        //    new OutputStreamWriter(cl.getResourceAsStream(default_file)));;
 

        File fl = new File(props.getProperty("store_file"));
        PrintStream out
           = new PrintStream(new FileOutputStream(fl));
        
        // BufferedReader input = new BufferedReader(
        // new InputStreamReader(cl.getResourceAsStream(strFileName)));	
        
        // FileWriter fw = new FileWriter(default_file);
        // BufferedWriter bw = new BufferedWriter(fw);
        // PrintWriter out = new PrintWriter(bw);
        
        /* PrintWriter out
         = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(default_file))));
         */
         
            for (int i = 0; i < list.length; i++){
                 out.println(list[i].getLine());
            }
        
	}

	public void store(CrontabEntryBean bean) throws Exception, DataNotFoundException {
            
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
