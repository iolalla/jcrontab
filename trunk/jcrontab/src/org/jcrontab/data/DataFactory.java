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

import java.util.Properties;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
/**
 * This Factory builds a dao using teh given information.
 * Initializes the system with the given properties or 
 * loads the default config
 * @author $Author: iolalla $
 * @version $Revision: 1.12 $
 */

public class DataFactory {

	
    private static DataFactory instance;
    
    private static Properties prop = new Properties();
    
    private static String strConfigFileName = "properties.cfg";
    
    private static DataSource dao = null;
	
	/**
	 *	Default Constructor private
	 */
    private DataFactory() {
	   if ( dao == null) {
		try {
		init();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	   }
    }
    /** 
	 * This method returns the DataFactory of the System This method
	 * grants the Singleton pattern
	 * @return DataSource I have a lot of doubts about how this method 
	 * is done.
 	 */
    public synchronized static DataFactory getInstance() {
		if (instance == null) {
			instance = new DataFactory();
		}
		return instance;
    }
	
	/** 
	 * This method returns the DataSource of the System
	 * @return DataSource I have a lot of doubts about how this method 
	 * is done.
 	 */

    public static DataSource getDAO() {
        return dao;
    }
     /**
	 *	This method really initializes the DataFactory
	 * @throws Exception
	 */
    public static void init() throws Exception {
         Class cl = DataFactory.class;
         // Get the Params from the config File
		 File filez = new File(strConfigFileName);
         FileInputStream input =
           new FileInputStream(filez);
         
         prop.load(input);
		 
         input.close();
		 
		 Class daocl = Class.forName(prop.getProperty("datasource_class"));
		 
		 dao = (DataSource)daocl.newInstance();
		 
		 dao.init(prop);
    }
    /**
	 *	This method initializes the DataFactory with the given properties
	 *  @param prop Properties The properties that defines the Factory 
	 *  configuration
	 * @throws Exception
	 */
    public static void init(Properties _prop) throws Exception {
        prop = _prop;
    }
    /**
	 *	This method initializes the DataFactory
	 *  @param strConfigFileName The file containing the properties
	 * @throws Exception
	 */
    public static void init(String _strConfigFileName) throws Exception {
        strConfigFileName = _strConfigFileName;
        init();
    }   
}
