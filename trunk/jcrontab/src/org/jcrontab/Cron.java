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


import java.util.Calendar;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Date;
import java.util.Vector;
import java.util.Properties;
import java.io.*;

import org.jcrontab.data.CrontabEntryException;
import org.jcrontab.data.CrontabEntryDAO;
import org.jcrontab.data.CrontabEntryBean;

/** 
 * Implements a process that interprets a con-table and generates the events
 * described in that table
 * @author iolalla
 * @version 0.01
 */

public class Cron extends Thread
{
    private static final String GENERATE_TIMETABLE_EVENT = "gen_timetable";

    private Crontab crontab;

    private int iFrec;
	
    private static int minute = 60000;

    public String strFileName = "crontab";

    private LinkedList eventsQueue;
    
   // public static Vector timeTable;
    
    public static CrontabEntryBean[] crontabEntryArray;

    /**
     * Constructor of a Cron. This one doesn't receive any parameters to make 
     * it easier to build an instance of Cron
     */
    public Cron() {
        crontab = Crontab.getInstance();
        iFrec = 60;
        eventsQueue = new LinkedList();
    }
    
    /**
     * Constructor of a Cron
     * @param cront The  Crontab that the cron must call to generate
     * new tasks
     * @param iTimeTableGenerationFrec Frecuency of generation of new time table entries.
     */
    public Cron(Crontab cront, int iTimeTableGenerationFrec) {
        crontab = cront;
        iFrec = iTimeTableGenerationFrec;
        eventsQueue = new LinkedList();
    }
    

    
    /** 
     * Initializes the crontab, reading tasks from configuration file
     * @throws CrontabEntryException Error parsing tasks configuration file entry
     * @throws FileNotFoundException Tasks configuration file not found
     * @throws IOException Error reading tasks configuration file
     */    
    public static void init() throws Exception {
      crontabEntryArray = readCrontab();
    }
    
    /** 
     * Initializes the crontab, reading tasks from configuration file
     * @param strFileName Name of the tasks configuration file
     * @throws CrontabEntryException Error parsing tasks configuration file entry
     * @throws FileNotFoundException Tasks configuration file not found
     * @throws IOException Error reading tasks configuration file
     */    
    public static void init(Properties prop) throws Exception {
      crontabEntryArray = readCrontab(prop);
    }


    /** 
     * Runs the Cron Thread.
     */    
    public void run() {
        
        try {
        crontabEntryArray = readCrontab();
        // Waits until the next minute to begin
        waitNextMinute();
        // Generates events list
        generateEvents();
        } catch (Exception e) {
            e.printStackTrace();
            
        }
        // Infinite loop, this thread will stop when the jvm is stopped (it is
        // a daemon).
        while(true) {
            
            CrontabBean nextEv = null;
            try {
                nextEv = (CrontabBean)(eventsQueue.getFirst());
            } catch(NoSuchElementException e) {
			// Fatal error 
			e.printStackTrace();
                 System.exit(1);
            }
            
            long intervalToSleep = nextEv.getTime() - System.currentTimeMillis() + 50;
            if(intervalToSleep > 0) {
                // Waits until the next event
                try {
                    synchronized(this) {
                        wait(intervalToSleep);
                    }
                } catch(InterruptedException e) {
                    try {
                      crontabEntryArray = readCrontab();
                    } catch(Exception e2) {
			e2.printStackTrace();
                         System.exit(1);
                    }
                    // Creates previous events
                    eventsQueue = new LinkedList();
                    // Waits until the next minute to begin
                    waitNextMinute();
                    // Generates events list
                    generateEvents();
                    // Continues loop
                    continue;
                }
            }
            
            CrontabBean ev = null;
            try {
                ev = (CrontabBean)(eventsQueue.removeFirst());
            } catch(NoSuchElementException e) {
		//Fatal Error
		 e.printStackTrace();
                System.exit(1);
            }
            
            // If it is a generate time table event, does it.
            if(ev.getClassName().equals(GENERATE_TIMETABLE_EVENT)) {
                generateEvents();
            }
            // Else, then tell the crontab to create the new task
            else {
                crontab.newTask(ev.getClassName(), ev.getMethodName(), 
		ev.getExtraInfo());
            }            
        }
    }

    private void waitNextMinute() {
        // Waits until the next minute
        long tmp = System.currentTimeMillis();
        if(tmp % minute != 0) {
            long intervalToSleep = ((((long)(tmp / minute))+1) * minute) - tmp + 50;
            
            // Waits until the next minute
            try {
                synchronized(this) {
                    wait(intervalToSleep);
                }
            } catch(InterruptedException e) {
                // Waits again
                waitNextMinute();
            }
        }        
    }

	/*
	 *   This method waits until the next excution is neded
	 */

	private void waitNextEvent() {
		Calendar rightNow = Calendar.getInstance();
		Calendar nextEventCal =  nextEvent(rightNow);
        // Waits until the next minute
        long tmp = nextEventCal.getTime().getTime()
				   - rightNow.getTime().getTime();
            // Waits until the next minute
            try {
                synchronized(this) {
                    wait(tmp);
                }
            } catch(InterruptedException e) {
                // Waits again
                waitNextEvent();
            }
	}    

	public Calendar nextEvent(Calendar fromCal) {
		
	Calendar nextCal = Calendar.getInstance();
	nextCal.setTime (fromCal.getTime());

	findNext (nextCal, bMinutes, Calendar.MINUTE, Calendar.HOUR_OF_DAY,	false);
	findNext (nextCal, bHours, Calendar.HOUR_OF_DAY, Calendar.DAY_OF_MONTH, false);
	findNext (nextCal, bDaysOfMonth, Calendar.DAY_OF_MONTH, Calendar.MONTH, true);
	findNext (nextCal, bMonths, Calendar.MONTH, Calendar.YEAR, false);

		while (eventMatch(nextCal) == false) {
			nextCal.add (Calendar.DAY_OF_MONTH, 1);
			nextCal = nextEvent (nextCal);
		}
		return nextCal;
	}

    int findNext (Calendar cal, boolean [] array, int calField, int
     nextCalField, boolean bBeginInOne) { 
    	int start = cal.get(calField);
    	if (bBeginInOne)
    		start--;
   	 	while (start < array.length) {
    			if (array[start]) {
    				if (bBeginInOne)
    					start++;
    				cal.set (calField, start);
    				return start;
    			}
    			start++;
    	}
    	start = 0;
    	cal.add (nextCalField, 1);
    	while (start < array.length) {
    			if (array[start]) {
    			if (bBeginInOne)
    				start++;
    			cal.set (calField, start);
    			return start;
    			}
    			start++;
    	}
    	return 0;
    }
    

   /**
    * Reads the cron-table and converts it to the internal representation
    * @param strFileName Name of t 	        he tasks configuration file
    * @throws CrontabEntryException Error parsing tasks configuration file entry
    *
    */
         
   public static CrontabEntryBean[] readCrontab() throws Exception {
       CrontabEntryDAO.init();
       crontabEntryArray = CrontabEntryDAO.getInstance().findAll();
       return crontabEntryArray;
   }
   
      /**
    * Reads the cron-table and converts it to the internal representation
    * @param strFileName Name of the tasks configuration file
    * @throws CrontabEntryException Error parsing tasks configuration file entry
    *
    */
         
   public static CrontabEntryBean[] readCrontab(Properties prop) throws Exception {
       
       CrontabEntryDAO.init(prop);
       crontabEntryArray = CrontabEntryDAO.getInstance().findAll();
       return crontabEntryArray;
   }

    /**
     * Generates new time table entries (for new events).
     */
    public void generateEvents() {
        CrontabEntryBean entry;
        
        // Rounds the calendar to the previous minute
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(((long)(System.currentTimeMillis() / 60000)) 
                * 60000));
        // System.out.println("Tamaño :" + timeTable.size());
        for(int i=0; i<iFrec; i++) {
            for(int j=0; j<crontabEntryArray.length; j++) {
                entry = crontabEntryArray[j];
                if(entry.equals(cal)) {
                    
                    CrontabBean ev = new CrontabBean();
                        ev.setId(j);
			ev.setCalendar(cal);
			ev.setTime(cal.getTime().getTime());
			ev.setClassName(entry.getClassName());
			ev.setMethodName(entry.getMethodName());
			ev.setExtraInfo(entry.getExtraInfo());
                        
                    eventsQueue.addLast(ev);
                  //  System.out.println(ev);
                }
            }
            cal.add(Calendar.MINUTE, 1);
        }
        
        // The last event is the new generation of the event list
        CrontabBean ev = new CrontabBean();
		ev.setCalendar(cal);
		ev.setTime(cal.getTime().getTime());
		ev.setClassName(GENERATE_TIMETABLE_EVENT);
		ev.setMethodName("");
        eventsQueue.addLast(ev);
    }

}
