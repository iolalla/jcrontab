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
package org.jcrontab.web;  

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.jcrontab.Crontab;
import org.jcrontab.log.Log;

/**
 * @author $Author: iolalla $
 * @version $Revision: 1.27 $
 */
public class loadCrontabServlet extends HttpServlet {
	
    private Crontab crontab = null;
        /** Refer to Servlet Javadoc
         * This method is invoked by the Servlet container
		 * When the app-server starts.
         * @param config The ServletConfig
         */ 
	public void init(ServletConfig config) throws ServletException{
		super.init(config);
		
		try {
			System.out.print("Working?...");
            		process();
			System.out.println("OK");
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	protected InputStream createPropertiesStream(String name)
		throws IOException {
		return new FileInputStream(name);
	}
	 /** 
	  * This method  starts the Crontab and lets the system
	  * Continue without wasting more resources.
	  * This method can receive the config File as a variable in web.xml
	  */        
	public void process() {

		String propz = "jcrontab.properties";
		//String path = getServletConfig().getServletContext()
		//								.getRealPath(".");
		//System.out.println("Real Path: " + path);
		String props = getServletConfig()
				.getInitParameter("PROPERTIES_FILE");
		if (props == null) {
			props = propz;
		}
		System.out.println(" ................ STARTING JCRONTAB ..........................");
		System.out.println("................ loading properties from :"+props);
		System.out.println("................ path :"+ (new File(props)).getAbsolutePath());
		
		// Load the servlet config parameters
		// and override the properties
		Properties propObj = new Properties();
		try {
		    InputStream input = createPropertiesStream(props);
		    propObj.load(input);
		} catch (IOException ioe) {
		    ioe.printStackTrace();
		}
		 ServletConfig c = getServletConfig();
		 Enumeration keys = c.getInitParameterNames();
		 while (keys.hasMoreElements()) {
		     String key = (String) keys.nextElement();
		     propObj.setProperty(key, c.getInitParameter(key));
		 }

		crontab = Crontab.getInstance();

		try {
			ShutdownHook();
			crontab.init(propObj);
		} catch (Exception e) {
			Log.error(e.toString(), e);
		}
	}
	
    /**
	 * This method seths a ShutdownHook to the system
	 *  This traps the CTRL+C or kill signal and shutdows 
	 * Correctly the system.
	 * @throws Exception
	 */ 
	 public void ShutdownHook() throws Exception {
             Runtime.getRuntime().addShutdownHook(new Thread() {         
	 	public void run() {
                doStop();
				}
			});
    }
     
    public void destroy() {
        doStop();
    }
    
    public void doStop() {
        	Log.info("Shutting down...");
			// stops the system in 100 miliseconds :-)
			crontab.uninit(100);
			Log.info("Stoped");
    }
}
