/**
 *  This file is part of the jcrontab package
 *  Copyright (C) 2001-2004 Israel Olalla
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

import org.jcrontab.CronTask;

/** 
 * This Bean reresents the basis to build Process logic, basically represents
 * the Process
 * @author $Author: iolalla $
 * @version $Revision: 1.3 $
 */
public class Process {
    
    /** This id is the primay key of this Bean
     */
    private int id;
    /** This is the list of tasks to execute by this process
     */
    private CronTask[] tasksList = null;
    
    private int counter = 0;
    /** This is the boolean to say if this Process executes the tasks one by one
     *  or all at the same time.
     */
    private boolean isConcurrent = false;
    /** This is the boolean to say if this Process exits when there is an error
     */
    private boolean isFaultTolerant = false;
    
    private boolean isRunning = false;
    
    private java.sql.Date lastRun = null;

     /** This id setter 
      * @param id int the id of this bean
      */
    public void setId(int id) {
        this.id = id;
    }
     /** This id getter 
      * @return id int the id of this bean
      */
    public int getId() {
        return this.id;
    }
    /**
     */
    public void setTasksList(CronTask[] list) {
	    this.tasksList = list;
        order();
    }
     /** This method returh the list of Tasks ordered by id 
      * @return CronTask[] list of tasks
      */
    public CronTask[] getTasksList() {
        return tasksList;
    }
    
    
    public void setIsConcurrent(boolean isconcurrent) {
        this.isConcurrent = isconcurrent;
    }
    
    public void setIsFaultTolerant(boolean isFaultTolerant) {
        this.isFaultTolerant = isFaultTolerant;
    }
    
    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
    
    public void setLastRun(java.sql.Date lastrun) {
        this.lastRun = lastrun;
    }
    
    public boolean getIsConcurrent() {
        return isConcurrent;
    }
    
    public boolean getIsRunning() {
        return isRunning;
    }
    
    public java.sql.Date getLastRun() {
        return lastRun;
    }

    /** Adds a single Task to the list and orders the list
     * @param Crontask The task to add
     */
    public void addTask(CronTask task) {
        CronTask[] list = null;
        if (counter == 0) {
            list = new CronTask[1];
            list[0] = task;
        } else {
            list = new CronTask[tasksList.length + 1];
            for (int i = 0; i < tasksList.length ; i++) {
                list[i] = tasksList[i];
            }
                list[tasksList.length] = task;
        }
        this.tasksList = list;
        order();
    }
    /** This method orders the list based in the Crontask id
     */
    private void order() {
        CronTask[] orderedList = new CronTask[tasksList.length];
         for (int i = 0; i < tasksList.length ; i++) {
            orderedList[tasksList[i].getOrder()] = tasksList[i];
        }
        counter = orderedList.length;
        this.tasksList = orderedList;
    }
   /** Represents the Process in ASCII format
    * @return the returning string
    */        
	public String toString(){
            StringBuffer sb = new StringBuffer();
            sb.append("[id: " + id + " ]\n ");
            sb.append("[isConcurrent: " + isConcurrent + " ]\n ");
            sb.append("[isReentrant: " + id + " ]\n ");
            for (int i = 0 ; i < tasksList.length ; i++) {
                sb.append("[Task: " + tasksList[i] + " ]\n");
            }
            return sb.toString();
	}
    
     /** 
     * @param obj Object to compare with the Holidays Bean
     * @return true if the time table entry matchs with the Object given
     *     false otherwise
     */    
    
    public boolean equals(Object obj) {
         Process process = null;
        if (!(obj instanceof Process)) {
            return false;
        } else  {
            process = (Process)obj;
        }
        if (id !=  process.getId()) {
            return false;
	}
        return true;
    }
     /** 
     * Helps to do the castings in a more simple way.
     * @param obj Object to cast to Process
     * @return The resulting array of Process
     */    
    public static Process[] toArray(Object[] obj) {
        Process[] process = new Process[obj.length];
        for (int i = 0; i < obj.length ; i++) {
            process[i] = (Process)obj[i];
        }
        return process;
    }
}
