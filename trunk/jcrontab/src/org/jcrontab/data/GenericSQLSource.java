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
 * This class is only a generic example and doesn't aim to solve all the needs
 * for the differents system's. if you want to make this class to fit your needs
 * feel free to do it and remember the license.
 * On of the things this class does is to open a connection to the database
 * , this is nasty and very expensive, y you want to integrate jcrontab with a 
 * pool like poolman or jboss it's quite easy, shoul substitute connection logic
 * with particular one.
 * @author  iolalla@yahoo.com
 * @version 0.01
 */
public class GenericSQLSource implements DataSource {

    private static GenericSQLSource instance;
    
    private static Properties props = new Properties();
	
    /** Creates new GenericSQLSource */
	
    public GenericSQLSource() {
    }	

    public DataSource getInstance() {
		if (instance == null) {
		instance = new GenericSQLSource();
		}
		return instance;
    }
    
    public void init(Properties props) {
	
		this.props = props;
		/*
		 *	Those lines are added to grant default values 
		 *  and to avoid anoying Exceptions and errors in 
		 *  properties Files
		 
		if (props.getProperty("config_file") == null) 
				props.setProperty("config_file", config_file);
		if (props.getProperty("store_file") == null) 
				props.setProperty("store_file", store_file);
	     */
    }
    
    public CrontabEntryBean[] find(String cl) throws CrontabEntryException
			 {
            CrontabEntryBean[] test = {new CrontabEntryBean()}; 
            return test;
    }
    
    public CrontabEntryBean[] findAll() throws CrontabEntryException
			{
			CrontabEntryBean[] test = {new CrontabEntryBean()}; 
            return test;
    }
    
    public void remove(CrontabEntryBean[] ceb) throws Exception {
    
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
	}
}
