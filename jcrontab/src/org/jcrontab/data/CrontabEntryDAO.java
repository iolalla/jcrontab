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
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.Properties;
import org.jcrontab.Cron;

public class CrontabEntryDAO {

	private static CrontabEntryDAO instance;

	public static Vector crontabEntryList;

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

	public static CrontabEntryDAO getInstance() {
		if (instance == null) {
                        instance = new CrontabEntryDAO();
		}
		return instance;
	}
        
	public static void init() throws Exception {
		DataFactory.init();
		dao = DataFactory.getDAO();
	}
        
	public static void init(Properties prop) throws Exception {
		DataFactory.init(prop);
		dao = DataFactory.getDAO();
	}

	public CrontabEntryBean[] findAll() throws Exception {
		return dao.findAll();
	}
            

	public void store(CrontabEntryBean[] list) throws Exception {
		dao.store(list);
	}

        /**
         * @param bean
         * @throws Exception  */        
	public void store(CrontabEntryBean bean) throws Exception {
		dao.store(bean);
	}
}
