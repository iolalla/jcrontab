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

    private Properties prop;
    private String strConfigFileName = "properties.cfg";
    
    public DataFactory() {
    }

    public DataSource getDAO() {
        return FileSource.getInstance();
    }
    
    void init() throws Exception {          
         Class cl = DataFactory.class;
         // Get the Params from the config File
         InputStream input = 
            cl.getResourceAsStream(strConfigFileName);
         prop.load(input);
         input.close();
    }
    
    void init(Properties prop) throws Exception {
        this.prop = prop;
    }
    
    void init(String strConfigFileName) throws Exception {
        this.strConfigFileName = strConfigFileName;
        init();
    }   
}
