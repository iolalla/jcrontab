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
 
package org.jcrontab;


import java.io.PrintStream;
import java.util.Vector;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;


public class CrontabEntryDAO {

	private static CrontabEntryDAO instance;
        
        private static String default_file = "events.cfg";
        
        public static Vector crontabEntryList;

	private CrontabEntryDAO() {
	}	

	public static CrontabEntryDAO getInstance() {
		if (instance == null) {
		instance = new CrontabEntryDAO();
		}
		return instance;
	}

	public CrontabEntryBean[] findAll() throws 
               CrontabEntryException, FileNotFoundException, IOException {
            Vector listOfBeans = new Vector();
            crontabEntryList = Cron.readTimeTableFromFile(default_file);
            for (int i = 0 ; i < crontabEntryList.size(); i++) {
                
            CrontabEntryBean ceb = new CrontabEntryBean(
                String.valueOf(crontabEntryList.get(i)));
            listOfBeans.add(ceb);
            }
            
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

	public void storeAll(CrontabEntryBean[] list) throws 
               CrontabEntryException, FileNotFoundException, IOException {
        
        
        // BufferedReader input = new BufferedReader(new FileReader(strFileName));
	// This Line allows the events.cfg to be included in a jar file
	// and accessed from anywhere
        
        PrintStream out
            = new PrintStream(new FileOutputStream(default_file));
        
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
                 System.out.println(list[i].getClassName());
                 out.println(list[i].getLine());
            }
        
	}

	public void store(CrontabEntryBean bean){
	}
}
