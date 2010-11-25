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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jcrontab.data.CrontabEntryBean;
import org.jcrontab.data.CrontabEntryDAO;

/**
 * This servlet is designed to put in the context the CrontabEntryBean
 * Necesary for the view.
 *  That's done that way cause it follow the MVC paradigm
 * @author $Author: iolalla $
 * @version $Revision: 1.10 $
 */


public class CrontabViewServlet extends HttpServlet {
        /** Refer to Servlet Javadoc
         * @param request This is the servlet request. 
         * refer to the Servlet JavaDoc
         * @param response This is the servlet response 
         * refer to the Servlet JavaDoc
         */    
		public void doPost(HttpServletRequest request,
			HttpServletResponse response) {
			doGet(request, response);
		}
        /** Refer to Servlet Javadoc
         * @param request This is the servlet request. 
         * refer to the Servlet JavaDoc
         * @param response This is the servlet response 
         * refer to the Servlet JavaDoc
         */
		public void doGet (HttpServletRequest request,
    		       HttpServletResponse response) {
       		try {
		
			CrontabEntryBean[] listOfBeans= CrontabEntryDAO
											.getInstance()
											.findAll();
			
    			request.setAttribute ("listOfBeans", listOfBeans);
    			getServletConfig().getServletContext()
				.getRequestDispatcher("/CrontabView.jsp")
				.forward(request, response);
  		} catch (Exception ex) {
	    		ex.printStackTrace ();
    		}
		}
}
