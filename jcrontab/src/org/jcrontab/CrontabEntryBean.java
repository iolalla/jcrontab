
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
import java.util.StringTokenizer;
import java.io.Serializable;

public class CrontabEntryBean implements Serializable {

    
        private String hours;
	private String minutes;
	private String months;
	private String daysOfWeek;
	private String daysOfMonth;
	
	private String className;
	private String methodName;
        private String[] extraInfo;
        private boolean bextraInfo = false;
        
        private String entry;

        private int priority;

        public CrontabEntryBean(){   
        }
        
        public CrontabEntryBean(String entry) 
                throws CrontabEntryException {
            setLine(entry);
        }

	public void setClassName(String className){
		this.className = className;
	}
	public void setMethodName(String methodName){
		this.methodName = methodName;
	}
	public void setExtraInfo(String[] extraInfo){
		this.extraInfo = extraInfo;
	}
	public void setPriority(int priority){
		this.priority = priority;
	}	
        public void setHours(String hours){
		this.hours = hours;
	}	
        public void setMinutes(String minutes){
		this.minutes = minutes;
	}	
        public void setMonths(String months){
		this.months = months;
	}
        public void setDaysOfWeek(String daysOfWeek){
		this.daysOfWeek = daysOfWeek;
	}
        public void setDaysOfMonth(String daysOfMonth){
		this.daysOfMonth = daysOfMonth;
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
	public int getPriority(){
		return priority;
	}
        
        public String getHours(){
		return hours;
	}	
        public String getMinutes(){
		return minutes;
	}	
        public String getMonths(){
		return months;
	}
        public String getDaysOfWeek(){
		return daysOfWeek;
	}
        public String getDaysOfMonth(){
		return daysOfMonth;
	}

        
    /** 
     * Parses a string describing this time table entry
     * @param strEntry String describing the time table entry
     * @throws CrontabEntryException Error parsing the string
     */    
    public void setLine(String entry) throws CrontabEntryException {
        this.entry = entry;
        StringTokenizer tokenizer = new StringTokenizer(entry);
        
        int numTokens = tokenizer.countTokens();
        
        for(int i = 0; tokenizer.hasMoreElements(); i++) {
            String token = tokenizer.nextToken();
            switch(i)
            {
                case 0:     // Minutes
                    minutes = token;
                    break;
                case 1:     // Hours
                    hours = token;
                    break;
                case 2:     // Days of month
                    daysOfMonth = token;
                    break;
                case 3:     // Months
                    months = token;
                    break;
                case 4:     // Days of week
                    daysOfWeek = token;
                    break;
                case 5:     // Name of the class
	            try {
                        int index = token.indexOf("#");
                        if(index > 0)
                        {
                        StringTokenizer tokenize = new StringTokenizer(token, "#");
                        className = tokenize.nextToken();
                        methodName = tokenize.nextToken();
                        return;
                        } else {
                            className=token;
                            methodName="NULL";
                        }
                    } catch (Exception e) {
                        throw new CrontabEntryException();
                    }
                    break;
                case 6:     // Priority
                    try {
                        priority = Integer.parseInt(token);
                    } catch(NumberFormatException e) { 
                        throw new CrontabEntryException();
                    }
                    break;
                case 7:     // Extra Information
                    extraInfo = new String[numTokens - 7];
                    bextraInfo = true;
                    for(extraInfo[i - 7] = token; tokenizer.hasMoreElements(); 
                        extraInfo[i - 7] = tokenizer.nextToken()) {
                        i++;
                    }
                    break;
                default:
                    break;
            }
        }
        
        // At least 7 token
        if(numTokens<7) {
            throw new CrontabEntryException();
        }
    }
        
        /** 
     * Parses a string describing this time table entry
     * @param strEntry String describing the time table entry
     * @throws CrontabEntryException Error parsing the string
     */    
    public String getLine() throws CrontabEntryException {
        final StringBuffer sb = new StringBuffer();
        sb.append(minutes + " ");
        sb.append(hours + " ");
        sb.append(daysOfMonth + " ");
        sb.append(months + " ");
        sb.append(daysOfWeek + " ");
        if (methodName.equals("NULL")){
            sb.append(className + " ");
        } else {
            sb.append(className + "#" + methodName + " ");
        }
        return sb.toString();
        
    }
        
	public String toString() {
		final StringBuffer sb = new StringBuffer();
                
		sb.append("\n [ ClassName:" + className  + "]");
		sb.append("\n [ MethodName :" + methodName  + "]");
		if (bextraInfo) { 
			for (int i = 0; i < extraInfo.length ; i++) {
			sb.append("\n [ Parameter " + i + " : " + extraInfo[i]  + " ]"); 
			}
		}
		sb.append("\n [ Hours:" + hours  + "]");
		sb.append("\n [ Minutes:" + minutes  + "]");
		sb.append("\n [ Month:" + months + "]");
		sb.append("\n [ daysOfWeek:" + daysOfWeek + "]");
		sb.append("\n [ daysOfMonth:" + daysOfMonth + "]");
                sb.append("\n [ Priority:" + priority + "]");
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
		pw.print("<crontabentry>");
		pw.println("<classname>" + className + "</classname> ");
		pw.println("<methodname>" + methodName + "</methodname> ");
 		if (bextraInfo) {               
			for (int i = 0; i < extraInfo.length ; i++) {
			pw.println("<extrainfo parameter = " + i + " >");
			pw.println(extraInfo[i] + " </extrainfo>");
			}
                } 
                pw.println("<hours>" + hours + "</hours> ");
		pw.println("<minutes>" + minutes + "</minutes> ");
		pw.println("<month>" + months + "</month> ");
		pw.println("<daysofweek>" + daysOfWeek + "</daysofweek> ");
		pw.println("<daysofmonth>" + daysOfMonth + "</daysofmonth> ");
		pw.println("<priority>" + priority + "</priority> ");
		pw.println("</crontabebtry>");
	}
	public boolean equals(Object object) {
	return false;
	}
 }
