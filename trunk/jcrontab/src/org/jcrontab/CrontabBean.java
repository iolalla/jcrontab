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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.io.Serializable;

 public class CrontabBean implements Serializable {


	public Calendar cal;
	
	public long timeMillis;
        
	public int id;
	public String className;
	public String methodName;
	public boolean bextraInfo = false;
	public String[] extraInfo;

        
        public CrontabBean(){
        }
        
        public void setId(int id){
		this.id = id;
	}
       	public void setTime(long timeMillis){
		this.timeMillis = timeMillis;
	}
	public void setClassName(String className){
		this.className = className;
	}
	public void setMethodName(String methodName){
		this.methodName = methodName;
	}
	public void setExtraInfo(String[] extraInfo){
		this.extraInfo = extraInfo;
                this.bextraInfo = true;
	}

        public void setCalendar(Calendar cal){
		this.cal = cal;
	}
        
        public int getId(){
		return id;
	}
        public long getTime(){
                return timeMillis;
        }
        public Calendar getCalendar(){
		return cal;
	}
	public String getClassName(){
		return className;
	}
	public String getMethodName(){
		return methodName;
	}
	public String[] getExtraInfo(){
		return extraInfo;
	}
				

	public String toString() {
		final StringBuffer sb = new StringBuffer();
                sb.append("\n [ Id: " + id  + " ]");
                sb.append("\n [ ClassName: " + className  + " ]");
		sb.append("\n [ MethodName : " + methodName  + " ]");
		if (bextraInfo) {
			for (int i = 0; i < extraInfo.length ; i++) {
			sb.append("\n [ Parameter " + i + " : " + extraInfo[i] 
                            + " ]"); 
			}
		}
       		sb.append("\n [ Calendar: " + cal  + " ]");
                sb.append("\n [ TimeMillis: " + timeMillis + " ] ");
		sb.append("\n ");
		return sb.toString();
	}

	public String toXML(){
	        StringWriter stringWriter = new StringWriter();
       		PrintWriter printWriter = new PrintWriter(stringWriter, true);
        	toXML(printWriter);
        	return stringWriter.toString();
	}

	public void toXML(PrintWriter pw) {
            	pw.println("<crontabentry>");
                pw.println("<id>" + id + "</id> ");                
                pw.println("<classname>" + className + "</classname> ");
		pw.println("<methodname>" + methodName + "</methodname> ");
 		if (bextraInfo) {               
			for (int i = 0; i < extraInfo.length ; i++) {
			pw.println("<extrainfo parameter = \"" + i + "\" >");
			pw.println(extraInfo[i] + " </extrainfo>");
			}
                }
                pw.println("<calendar>" + cal + " </calendar>");
                pw.println("<timemillis>" + timeMillis + "</timemillis> ");
		pw.println("</crontabentry>");
	}
        
        /**
         * @param object
         * @return  */        
	public boolean equals(Object object) {
	return false;
	}
 }
