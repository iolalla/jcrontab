/**
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

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.StringTokenizer;
import org.jcrontab.CrontabBean;

/** CrontabEntryBeans represents each entry into
 * crontab "DataSource" usually a file.
 * This Bean allows jcrontab to interact with
 * the information from CrontabEntry
 * @author $Author: iolalla $
 * @version $Revision: 1.19 $
 */
public class CrontabEntryBean implements Serializable {


    
    private int id;
    
    private CrontabBean[] cb;
    private String hours;
    private String minutes;
    private String months;
    private String daysOfWeek;
    private String daysOfMonth;
	
    private String className;
    private String methodName = "NULL";
    private String[] extraInfo;
    private boolean bextraInfo = false;
    private String description;
        
        
    private boolean[] bHours;
    private boolean[] bMinutes;
    private boolean[] bMonths;
    private boolean[] bDaysOfWeek;
    private boolean[] bDaysOfMonth;
    
    private String entry;

        /** Default constructor
         */        
    public CrontabEntryBean(){
        bHours = new boolean[24];
        bMinutes = new boolean[60];
        bMonths = new boolean[12];
        bDaysOfWeek = new boolean[7];
        bDaysOfMonth = new boolean[31];
        
        // Initializes all arrays to false
        for(int i=0; i<60; i++) {
            if(i<24)
                bHours[i] = false;
            if(i<60)
                bMinutes[i] = false;
            if(i<12)
                bMonths[i] = false;
            if(i<7)
                bDaysOfWeek[i] = false;
            if(i<31)
                bDaysOfMonth[i] = false;
        }
        }
        
	/** This constructor builds a CrontabEntryBean from a
	 * CrontabBean
	 * @param cb bean to build a CrontabEntry from CrontabBean
	 *
	 */        
	public CrontabEntryBean(CrontabBean cb){
        bHours = new boolean[24];
        bMinutes = new boolean[60];
        bMonths = new boolean[12];
        bDaysOfWeek = new boolean[7];
        bDaysOfMonth = new boolean[31];
        
        // Initializes all arrays to false
        for(int i=0; i<60; i++) {
            if(i<24)
                bHours[i] = false;
            if(i<60)
                bMinutes[i] = false;
            if(i<12)
                bMonths[i] = false;
            if(i<7)
                bDaysOfWeek[i] = false;
            if(i<31)
                bDaysOfMonth[i] = false;
        }
            this.cb[0] = cb;
     }
        
        /** This constructor builds a CrontabEntryBean from a
         * Crontab Line usually something similar to:
         * * * * * * org.jcrontab.jcrontab
         * @param entry this line is the Crontab Line
         * @throws CrontabEntryException this excption is thrown if the format of the
         * line is not valid with POSIX estandar
         */        
        public CrontabEntryBean(String entry) 
                throws CrontabEntryException {
        bHours = new boolean[24];
        bMinutes = new boolean[60];
        bMonths = new boolean[12];
        bDaysOfWeek = new boolean[7];
        bDaysOfMonth = new boolean[31];
        
        // Initializes all arrays to false
        for(int i=0; i<60; i++) {
            if(i<24)
                bHours[i] = false;
            if(i<60)
                bMinutes[i] = false;
            if(i<12)
                bMonths[i] = false;
            if(i<7)
                bDaysOfWeek[i] = false;
            if(i<31)
                bDaysOfMonth[i] = false;
        }
            setLine(entry);
        }

	/** Id setter
	 * @param id this integer identifies the CrontabEntryBean
	 */        
	public void setId(int id){
		this.id = id;
	}
	/** ClassName Setter
	 * @param className This is the name of the class to execute
	 */        
	public void setClassName(String className){
		this.className = className;
	}
	/** MethodName setter
	 * @param methodName the name of the method to execute
	 */        
	public void setMethodName(String methodName){
		this.methodName = methodName;
	}
	/** Extra info setter
	 * @param extraInfo this array represents the parameters passed to the
	 * task
	 */        
	public void setExtraInfo(String[] extraInfo){
		this.extraInfo = extraInfo;
	}	
	/** Hours setter
	 * @param hours The hours to execute the Class,
	 * the values can take are [ * , 2-4 , 2,3,4,5 , 3/5]
	 */        
	public void setHours(String hours){
		this.hours = hours;
	}
	/** Minutes setter
	 * @param minutes The minutes to execute the Class,
	 * the values can take are [ * , 2-4 , 2,3,4,5 , 3/5]
	 */      
	public void setMinutes(String minutes){
		this.minutes = minutes;
	}
	/** Months setter
	 * @param months The Monts to execute the Class,
	 * the values can take are [ * , 2-4 , 2,3,4,5 , 3/5]
	 */  
	public void setMonths(String months){
		this.months = months;
	}
	/** Days of Week
	 * @param daysOfWeek The days of the week
	 */  
    public void setDaysOfWeek(String daysOfWeek){
		this.daysOfWeek = daysOfWeek;
	}
	/** Days of Month setter
	 * @param daysOfMonth The days of the month
	 */  
	public void setDaysOfMonth(String daysOfMonth){
		this.daysOfMonth = daysOfMonth;
	}
	/**Description setter
	 * @param description The desciption 
	 */
	public void setDescription(String description) {
		this.description = description;
	}
    /** Id getter
    * @return the Id of this CrontabBean
    */        
    public int getId(){
		return id;
	} 
    /** Class Name getter
     * @return the Class's Name of this CrontabBean
     */      
	public String getClassName(){
		return className;
	}
    /** Method Name getter
     * @return the Method's Name of this CrontabBean
     */      
	public String getMethodName(){
		return methodName;
	}
    /** Extra Info getter
     * @return the extraInfo of this CrontabBean
     */      
	public String[] getExtraInfo(){
		return extraInfo;
	}
    /** Hours getter
     * @return the hours of this CrontabBean
     */      
    public String getHours(){
		return hours;
	}
	/** Minutes getter
	 * @return the minutes of this CrontabBean
	 */      
    public String getMinutes(){
		return minutes;
	}
	/** Months getter
	 * @return the months of this CrontabBean
	 */      
    public String getMonths(){
		return months;
	}
	/** Days of week getter
	 * @return the Days of week of this CrontabBean
	 */      
    public String getDaysOfWeek(){
		return daysOfWeek;
	}
	/** Days of Month getter
	 * @return the Id of this CrontabBean
	 */      
    public String getDaysOfMonth(){
		return daysOfMonth;
	}
	/** Description getter
	 * @return the Description of this CrontabBean 
	 */      
    public String getDescription(){
		return description;
	}
	/**
	 * Parses a string describing this time table entry and sets the 
	 * neded variables in order to build a CrontabEntry.
	 * Crontab Line usually something similar to:
	 * * * * * * org.jcrontab.jcrontab
	 * @param entry the line to parse
	 * @throws CrontabEntryException Error parsing the string
	 */    
    public void setLine(String entry) throws CrontabEntryException {
        this.entry = entry;
        
        StringTokenizer tokenizer = new StringTokenizer(entry);
        
        int numTokens = tokenizer.countTokens();
        for(int i = 0; tokenizer.hasMoreElements(); i++) {
            String token = tokenizer.nextToken();
            switch(i) {
                case 0:     // Minutes
                    minutes = token;
                    parseToken(token,bMinutes,false);
                    break;
                case 1:     // Hours
                    hours = token;
                    parseToken(token,bHours,false);
                    break;
                case 2:     // Days of month
                    daysOfMonth = token;
                    parseToken(token,bDaysOfMonth,true);
                    break;
                case 3:     // Months
                    months = token;
                    parseToken(token,bMonths,true);
                    break;
                case 4:     // Days of week
                    daysOfWeek = token;
                    parseToken(token,bDaysOfWeek,false);
                    break;
                case 5:     // Name of the class
                    try {
                        int index = token.indexOf("#");
                        if(index > 0) {
                        StringTokenizer tokenize = new StringTokenizer(token, "#");
                        className = tokenize.nextToken();
                        methodName = tokenize.nextToken();
                        break;
                        } else {
                            className=token;
                            methodName="NULL";
							break;
                        }
                    } catch (Exception e) {
                        throw new CrontabEntryException(entry);
                    } finally{
			break;
		    }
                case 6:     // Extra Information
                    extraInfo = new String[numTokens - 6];
                    bextraInfo = true;
                    for(extraInfo[i - 6] = token; tokenizer.hasMoreElements(); 
                        extraInfo[i - 6] = tokenizer.nextToken()) {
                        i++;
                    }
                    for (int y = 0; y < extraInfo.length ; y++) {
                    }
                    break;
                default:
                    break;
            }
        }      
        // At least 6 token
        if(numTokens<6) {
            throw new CrontabEntryException("The number of items is < 6 at " + entry);
        }
    }
        
    /** 
     * Parses a string describing this time table entry
     * @return String describing the time table entry usuarlly something like:
     * * * * * * org.jcrontab.jcrontab
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
        if (bextraInfo) {               
            for (int i = 0; i < extraInfo.length ; i++) {
		sb.append(extraInfo[i] + " ");
	     }
        }
        return sb.toString();
        
    }
        
    /** 
     * Parses a token and fills the array of booleans that represents this 
     * CrontabEntryBean
     * @param token String to parser usually smth like [ * , 2-3 , 2,3,4 ,4/5 ]
     * @param arrayBool this array is the most efficient way to compare entries
     * @bBeginInOne says if the array begins in 0 or in 1  
     * @throws CrontabEntryException Error parsing the string
     */    

    private void parseToken(String token, boolean[] arrayBool, 
    	boolean bBeginInOne) 
                    throws CrontabEntryException {
        int i;
        try
        {
            if(token.equals("*")) {
                for(i=0; i<arrayBool.length; i++) {
                    arrayBool[i] = true;
                }
                return;
            }

            int index = token.indexOf(",");
            if(index > 0)
            {
                StringTokenizer tokenizer = new StringTokenizer(token, ",");
                while(tokenizer.hasMoreTokens()) {
                    parseToken(tokenizer.nextToken(), arrayBool, bBeginInOne);
                }
                return;
            }
            
            index = token.indexOf("-");
            if(index > 0)
            {
                int start = Integer.parseInt(token.substring(0, index));
                int end = Integer.parseInt(token.substring(index + 1));

                if(bBeginInOne) {
                    start--;
                    end--;
                }

                for(int j=start; j<=end; j++)
                    arrayBool[j] = true;
                return;
            }
            
            index = token.indexOf("/");
            if(index > 0)
            {
                int each = Integer.parseInt(token.substring(index + 1));
                for(int j=0; j<arrayBool.length; j+= each)
                    arrayBool[j] = true;
                return;
            }

            else
            {
                int iValue = Integer.parseInt(token);
                if(bBeginInOne) {
                    iValue--;
                }
                arrayBool[iValue] = true;
                return;
            }
        }
        catch(Exception e)
        {
            throw new CrontabEntryException( "Smth was wrong with " + token );
        }
    }
    /** The simplest representation of a CrontabBean in a String
     * @return the resulting String
     */    
    public String toString() {
		final StringBuffer sb = new StringBuffer();
		sb.append("\n [ description: " + description + "]");
                sb.append("\n [ Id:" + id  + "]");
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
		sb.append("\n ");
		return sb.toString();
	}

   /** Represents the CrotnabEntryBean in XML format
    * @return the returning XML
    */        
	public String toXML(){
	        StringWriter stringWriter = new StringWriter();
       		PrintWriter printWriter = new PrintWriter(stringWriter, true);
        	toXML(printWriter);
        	return stringWriter.toString();
	}

   /** Returns the XML that represents this Crontab EntryBean
    * @param pw The printWritter to write the XML
    */        
	public void toXML(PrintWriter pw) {
		pw.println("<crontabentry>");
                pw.println("<id>" + id + "</id> ");
                pw.println("<hours>" + hours + "</hours> ");
		pw.println("<minutes>" + minutes + "</minutes> ");
		pw.println("<month>" + months + "</month> ");
		pw.println("<daysofweek>" + daysOfWeek + "</daysofweek> ");
		pw.println("<daysofmonth>" + daysOfMonth + "</daysofmonth> ");
                pw.println("<classname>" + className + "</classname> ");
		pw.println("<methodname>" + methodName + "</methodname> ");
 		if (bextraInfo) {               
			for (int i = 0; i < extraInfo.length ; i++) {
			pw.println("<extrainfo parameter = \"" + i + "\" >");
			pw.println(extraInfo[i] + " </extrainfo>");
			}
        	}
                pw.println("<description>" + description + "</description> ");
		pw.println("</crontabentry>");
	}
        
    /** 
     * Returns true if the time table entry matchs with the calendar given
     * @param cal Calendar to compare with the time table entry
     * @return true if the time table entry matchs with the calendar given
     */    
	public boolean equals(Calendar cal) {
        // IMPORTANT: Day of week and day of month in Calendar begin in
        // 1, not in 0. Thats why we decrement them
        return ( bHours[cal.get(Calendar.HOUR_OF_DAY)] &&
            bMinutes[cal.get(Calendar.MINUTE)] &&
            bMonths[cal.get(Calendar.MONTH)] &&
            bDaysOfWeek[cal.get(Calendar.DAY_OF_WEEK)-1] &&
            bDaysOfMonth[cal.get(Calendar.DAY_OF_MONTH)-1]);
	}

    /** 
     * Returns true if the CrontabEntryBean equals the given
     * @param ceb CrontabEntryBean to compare with the CrontabEntryBean 
     * @return true if the CrontabEntryBean entry equals the CrontabEntryBean given
     */    
	public boolean equals(CrontabEntryBean ceb) {
		if (this.id == ceb.getId()) {
			return true;
		} else {
			return false;	
		}
	}
 }
