/*
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
 
package org.jcrontab.data;


import java.io.PrintStream;
import java.util.Vector;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.Properties;
import org.jcrontab.Cron;
/***
 * @author $Author: iolalla $
 * @version $Revision: 1.11 $
 */
public class CrontabEntryDAO {

	private static CrontabEntryDAO instance;

	private static Vector crontabEntryList;

	private static DataSource dao = null;

	
	private CrontabEntryDAO() {
		   if ( dao == null) {
				try {
				init();
				} catch (Exception e) {
					e.printStackTrace();
				}
		   }
	}	

	public synchronized static CrontabEntryDAO getInstance() {
		if (instance == null) {
                        instance = new CrontabEntryDAO();
		}
		return instance;
	}
        
	public static void init() throws Exception {
		DataFactory.init();
		dao = DataFactory.getInstance().getDAO();
	}
        
	public static void init(Properties prop) throws Exception {
		DataFactory.init(prop);
		dao = DataFactory.getInstance().getDAO();
	}
	
	public static void init(String strConfigFileName) throws Exception {
		DataFactory.init(strConfigFileName);
		dao = DataFactory.getInstance().getDAO();
	}

	public CrontabEntryBean[] findAll() throws Exception {
		return dao.findAll();
	}
            
	public CrontabEntryBean find(CrontabEntryBean ceb) throws Exception {
		return dao.find(ceb);
	}

	public void store(CrontabEntryBean[] list) throws Exception {
		dao.store(list);
	}
      
	public void store(CrontabEntryBean bean) throws Exception {
		dao.store(bean);
	}
	
	public void remove(CrontabEntryBean[] list) throws Exception {
		dao.remove(list);
	}
}
