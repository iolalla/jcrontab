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

import java.util.Properties;
import java.io.InputStream;

/**
 * This Factory initializes the system and loads the default config
 * if no Properties are passed the default properties file is used
 * @author iolalla
 * @version 0.01
 */

public class DataFactory {

    private static DataFactory instance;
    
    private static Properties prop = new Properties();
    
    private static String strConfigFileName = "properties.cfg";
    
    private static DataSource dao = null;
	
    private DataFactory() {
	   if ( dao == null) {
		try {
		init();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	   }
    }
    
    public static DataFactory getInstance() {
		if (instance == null) {
			instance = new DataFactory();
		}
		return instance;
    }

    public static DataSource getDAO() {
        return dao;
    }
    
    public static void init() throws Exception {
         Class cl = DataFactory.class;
         // Get the Params from the config File
         InputStream input =
            cl.getResourceAsStream(strConfigFileName);
         
         prop.load(input);
		 
         input.close();
		 
		 Class daocl = Class.forName(prop.getProperty("datasource_class"));
		 
		 dao = (DataSource)daocl.newInstance();
		 
		 dao.init(prop);
    }
    
    public static void init(Properties prop) throws Exception {
        prop = prop;
    }
    
    public static void init(String strConfigFileName) throws Exception {
        strConfigFileName = strConfigFileName;
        init();
    }   
}
