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
package org.jcrontab.web;  



import javax.servlet.*;
import javax.servlet.http.*;
import org.jcrontab.CrontabEntryBean;
import org.jcrontab.CrontabEntryDAO;
import java.io.*;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;



public class CrontabServletXML extends HttpServlet {

	public void doPost(HttpServletRequest request,
		HttpServletResponse response) {
		process(request, response);
	}

	public void doGet (HttpServletRequest request,
    	       HttpServletResponse response) {
		show(request, response);
	}

	public void process(HttpServletRequest request,
		HttpServletResponse response) {
                    
                String Classname = request.getParameter("Classname").trim();
                if (Classname.length() > 0) {
                String Minutes = request.getParameter("Minutes").trim();
                String Hours = request.getParameter("Hours").trim();
                String Daysofmonth = request.getParameter("Daysofmonth").trim();
                String Month = request.getParameter("Month").trim();
                String Daysofweek = request.getParameter("Daysofweek").trim();
                String Priority = request.getParameter("Priority").trim();
                String Extrainfo = request.getParameter("Extrainfo").trim();              
                StringBuffer sb = new StringBuffer();
                sb.append(Minutes);
                sb.append(" ");
                sb.append(Hours);
                sb.append(" ");
                sb.append(Daysofmonth);
                sb.append(" ");
                sb.append(Month);
                sb.append(" ");
                sb.append(Daysofweek);
                sb.append(" ");
                sb.append(Classname);
                sb.append(" ");
                sb.append(Priority);
                sb.append(" ");
                sb.append(Extrainfo);
                try {
                CrontabEntryBean cb = new CrontabEntryBean(sb.toString());
                CrontabEntryDAO.getInstance().store(cb);
                show(request, response);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                } else {
                show(request, response);   
                }
        }
        public void show(HttpServletRequest request,
		HttpServletResponse response) {

       		try {
		PrintStream out = new PrintStream(response.getOutputStream()); 

			CrontabEntryBean[] listOfBeans= CrontabEntryDAO
                            .getInstance().findAll();

			StringBuffer sb = new StringBuffer();
			sb.append(printHeader());
                       for (int i = 0; i < listOfBeans.length; i++) {
			sb.append(listOfBeans[i].toXML());
		       }
		        sb.append(printFooter());
                                                
                        TransformerFactory tFactory = 
                            TransformerFactory.newInstance();

                         
                        Source xmlsource = new StreamSource(
                            new StringReader((String)sb.toString()));
                        
                        Source xslsource = new StreamSource(
                            new FileInputStream("view.xsl"));

                        Transformer transformer = 
                            tFactory.newTransformer(xslsource);
                        transformer.transform(xmlsource, new StreamResult(out));
		       // out.print(sb.toString());

		out.close();
  		} catch (Exception ex) {
	    		ex.printStackTrace ();
    		}
		}

	private static String printHeader() {
		StringBuffer sb = new StringBuffer(); 
		sb.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n");
	        sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"view.xsl\" ?> \n");
	        sb.append("<page xml:base=\"\"> \n");   
		return sb.toString();
	}

	public static String printFooter() {
		return "</page>";
	}
        
}