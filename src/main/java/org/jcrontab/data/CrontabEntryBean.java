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
 
package org.jcrontab.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;
import org.jcrontab.CrontabBean;

/** CrontabEntryBeans represents each entry into
 * crontab "DataSource" usually a file.
 * This Bean allows jcrontab to interact with
 * the information from CrontabEntry
 * @author $Author: iolalla $
 * @version $Revision: 1.44 $
 */
public class CrontabEntryBean implements Serializable {
    
    private int id = -1;
    
    private boolean runInBusinessDays = true;
    
    private String seconds = "0";
    private String hours = "*";
    private String minutes = "*";
    private String months = "*";
    private String daysOfWeek = "*";
    private String daysOfMonth = "*";
    private String years = "*";
    private Date startDate;
    private Date endDate;
	
    private String className;
    private String methodName = "";
    private String[] extraInfo;
    private boolean bextraInfo = false;
    private String description;
        
        
    private boolean[] bHours;
    private boolean[] bSeconds;
    private boolean[] bMinutes;
    private boolean[] bMonths;
    private boolean[] bDaysOfWeek;
    private boolean[] bDaysOfMonth;
    private boolean[] bYears;

	private String header = "";
        
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
        this.bextraInfo = true;
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
	/** Seconds setter
	 * @param seconds The seconds to execute the Class,
	 * the values can take are [ * , 2-4 , 2,3,4,5 , 3/5]
	 */      
	public void setSeconds(String seconds){
		this.seconds = seconds;
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
    /** Years Setter
     * @param years to be executed this task
     */
    public void setYears(String years) {
        this.years = years;
    }
	/** Hours setter
	 * @param hours The hours to execute the Class,
	 * the values can take are [ * , 2-4 , 2,3,4,5 , 3/5]
	 */        
	public void setBHours(boolean[] bHours){
		this.bHours = bHours;
	}
	/** Minutes setter
	 * @param minutes The minutes to execute the Class,
	 * the values can take are [ * , 2-4 , 2,3,4,5 , 3/5]
	 */      
	public void setBMinutes(boolean[] bMinutes){
		this.bMinutes = bMinutes;
	}
	/** Months setter
	 * @param months The Monts to execute the Class,
	 * the values can take are [ * , 2-4 , 2,3,4,5 , 3/5]
	 */  
	public void setBMonths(boolean[] bMonths){
		this.bMonths = bMonths;
	}
	/** Days of Week
	 * @param bdaysOfWeek The days of the week
	 */  
    public void setBDaysOfWeek(boolean[] bDaysOfWeek){
		this.bDaysOfWeek = bDaysOfWeek;
	}
	/** Days of Month setter
	 * @param bdaysOfMonth The days of the month
	 */  
	public void setBDaysOfMonth(boolean[] bDaysOfMonth){
		this.bDaysOfMonth = bDaysOfMonth;
	}
    /** Seconds setter
	 * @param bSeconds Of ecah minute
	 */  
	public void setBSeconds(boolean[] bSeconds){
		this.bSeconds = bSeconds;
	}
    /** Years setter
	 * @param bYears Of ecah century
	 */  
    public void setBYears(boolean[] bYears){
        this.bYears = bYears;
    }
	/** bextraInfo setter
	 * @param daysOfMonth There are 
	 */  
	public void setBExtraInfo(boolean bextraInfo) {
	this.bextraInfo = bextraInfo;
	}
	/**Description setter
	 * @param description The desciption 
	 */
	public void setDescription(String description) {
		this.description = description;
	}
    
     /**runInBusinessDays setter
     * @param true if shouldRun only in Business Days false otherwise
     */
     public void setBusinessDays(boolean runInBusinessDays) {
          this.runInBusinessDays = runInBusinessDays;
     }
     /**startDate setter
     * @param the starting date of this Task
     */
     public void setStartDate(Date startDate) {
          this.startDate = startDate;
     }
     /**endDate setter
     * @param the ending date of this Task
     */
     public void setEndDate(Date endDate) {
          this.endDate = endDate;
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
    /** Minutes getter
	 * @return the minutes of this CrontabBean
	 */      
    public String getSeconds(){
		return seconds;
	}
	/** Months getter
	 * @return the months of this CrontabBean
	 */      
    public String getMonths(){
		return months;
	}
	/** Hours booleans getter
	 * @return boolean[] The hours to execute the Class,
	 */        
	public boolean[] getBHours(){
		return bHours;
	}
	/** Minutes getter
	 * @return boolean[] The minutes to execute the Class,
	 */      
	public boolean[] getBMinutes(){
		return bMinutes;
	}
	/** Months Boolean getter
	 * @return months The Months to execute the Class,
	 */  
	public boolean[] getBMonths(){
		return bMonths;
	}
	/** Getter Days of Week
	 * @return daysOfWeek The days of the week
	 */  
    public boolean[] getBDaysOfWeek(){
		return bDaysOfWeek;
	}
	/** Days of Month getter
	 * @return daysOfMonth The days of the month
	 */  
	public boolean[] getBDaysOfMonth(){
		return bDaysOfMonth;
	}
    	/** Days of Month setter
	 * @return daysOfMonth The days of the month
	 */  
	public boolean[] getBSeconds(){
		return bSeconds;
	}
    	/** bYears getter
	 * @return bYears Of ecah century
	 */  
    	public boolean[] getBYears(){
        	return bYears;
    	}
	/** Returns true if theres extra info false otherwise.
	 * @return extraInfo
	 */   
	public boolean getBExtraInfo() {
		return bextraInfo;
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
    /** Year getter
     * @return the year of this CrontabEntryBean
     */
    public String getYear() {
	return years;	
    }
	/** Description getter
	 * @return the Description of this CrontabBean 
	 */      
    public String getDescription(){
		return description;
	}
    
    /**runOnlyInBusinessDays getter
     * @return true if shouldRun only in Business Days false otherwise
     */
     public boolean getBusinessDays() {
         return runInBusinessDays;
     }
    /**startDate setter
     * @param the starting date of this Task
     */
     public Date getStartDate() {
          return startDate;
     }
     /**endDate setter
     * @param the ending date of this Task
     */
     public Date getEndDate() {
          return endDate;
     }
   /** Represents the CrotnabEntryBean in ASCII format
    * @return the returning string
    */        
	public String toString(){
        try {
            CrontabParser cp = new CrontabParser();
        	return cp.unmarshall(this).trim();
        } catch (Exception e) {
            return e.toString();
        }
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
		
		pw.println("<!--  "+  this.header + "\n -->");
		pw.println("<crontabentry id=\""+ id + "\">");
        pw.println("<seconds>" + seconds + "</seconds> ");
		pw.println("<minutes>" + minutes + "</minutes> ");
        pw.println("<hours>" + hours + "</hours> ");
        pw.println("<daysofmonth>" + daysOfMonth + "</daysofmonth> ");
		pw.println("<months>" + months + "</months> ");
		pw.println("<daysofweek>" + daysOfWeek + "</daysofweek> ");
        pw.println("<years>" + years + "</years> ");
        pw.println("<bussinesdays>" + runInBusinessDays +"</bussinesdays> " );
        pw.println("<startDate>" + startDate +"</startDate> " );
        pw.println("<endDate>" + endDate +"</endDate> " );
        pw.println("<class>" + className + "</class> ");
		pw.println("<method>" + methodName + "</method> ");
 		if (bextraInfo) {               
			for (int i = 0; i < extraInfo.length ; i++) {
			pw.println("<parameters order = \"" + i + "\" >");
			pw.println(extraInfo[i] + " </parameters>");
			}
        	}
                pw.println("<description>" + description + "</description> ");
		pw.println("</crontabentry>");
	}
    
     /** 
     * This method is here to wrap other two avaiable equals
     * @param obj Object to compare with the time table entry
     * @return true if the time table entry matchs with the Object given
     *     false otherwise
     */    
    
    public boolean equals(Object obj) {
        if (obj instanceof Calendar ) {
            return equalsCalendar((Calendar)obj);
        } else if (obj instanceof CrontabEntryBean) {
            return equalCrontabEntryBean((CrontabEntryBean)obj);
        } else {
            return false;
        }
    }
     /** 
     * Helps to do the castings in a more simple way.
     * @param obj Object to cast to CrontabEntryBean
     * @return The resulting array of CrontabEntryBean
     */    
    public static CrontabEntryBean[] toArray(Object[] obj) {
        CrontabEntryBean[] ceb = new CrontabEntryBean[obj.length];
        for (int i = 0; i < obj.length ; i++) {
            ceb[i] = (CrontabEntryBean)obj[i];
        }
        return ceb;
    }
    /** 
     * Returns true if the time table entry matchs with the calendar given
     * @param cal Calendar to compare with the time table entry
     * @return true if the time table entry matchs with the calendar given
     */    
	private boolean equalsCalendar(Calendar cal) {
        // IMPORTANT: Day of week and day of month in Calendar begin in
        // 1, not in 0. Thats why we decrement them
        return (
            bSeconds[cal.get(Calendar.SECOND)] &&
            bHours[cal.get(Calendar.HOUR_OF_DAY)] &&
            bMinutes[cal.get(Calendar.MINUTE)] &&
            bMonths[cal.get(Calendar.MONTH)] &&
            bDaysOfWeek[cal.get(Calendar.DAY_OF_WEEK)-1] &&
            bDaysOfMonth[cal.get(Calendar.DAY_OF_MONTH)-1]) &&
            bYears[0] ;
	}

    /** 
     * Returns true if the CrontabEntryBean equals the given
     * @param ceb CrontabEntryBean to compare with the CrontabEntryBean 
     * @return true if the CrontabEntryBean entry equals the CrontabEntryBean 
	 * given
     */
	
	private boolean equalCrontabEntryBean(CrontabEntryBean ceb) {
// 		if (this.id != ceb.getId()) {
// 			return false;
// 		}
		if (!this.getSeconds().equals(ceb.getSeconds())) {
			return false;
		}
		if (!this.getMinutes().equals(ceb.getMinutes())) {
			return false;
		}
		if (!this.getHours().equals(ceb.getHours())) {
			return false;
		}
		if (!this.getDaysOfWeek().equals(ceb.getDaysOfWeek())) {
			return false;
		}
		if (!this.getDaysOfMonth().equals(ceb.getDaysOfMonth())) {
			return false;
		}
		if (!this.getMonths().equals(ceb.getMonths())) {
			return false;
		}
		if (!this.getYear().equals(ceb.getYear())) {
			return false;
		}
		if (!this.getClassName().equals(ceb.getClassName())) {
			return false;
		}
		if (this.getBExtraInfo() != ceb.getBExtraInfo()) {
			return false;
		}
		if (this.getBusinessDays() != ceb.getBusinessDays()) {
			return false;
		}
		if (this.getBExtraInfo()) {
			if (this.getExtraInfo().length != ceb.getExtraInfo().length)
				return false;
			for (int i = 0; i < this.getExtraInfo().length; i++) {
				if (!this.getExtraInfo()[i].trim().equals(
						ceb.getExtraInfo()[i].trim()))
					return false;
			}
		}
		return true;
	}
	public void setHeader(StringBuffer sb) {
		this.header = sb.toString();
	}
	public String getHeader() { 
			return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	@Override
	public int hashCode() {
		return 
			this.toString().hashCode();
	}
}
