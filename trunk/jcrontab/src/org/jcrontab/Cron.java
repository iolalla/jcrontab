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
import java.io.*;
import org.jcrontab.*;

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
	
    public String strFileName = "crontab";

    private LinkedList eventsQueue;
    
    private Vector timeTable;

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
     * @param strFileName Name of the tasks configuration file
     * @throws CrontabEntryException Error parsing tasks configuration file entry
     * @throws FileNotFoundException Tasks configuration file not found
     * @throws IOException Error reading tasks configuration file
     */    
    public void init(String strFileName) throws CrontabEntryException,
                        FileNotFoundException, IOException {
        readTimeTableFromFile(strFileName);
    }


    /** 
     * Runs the scheduler.
     */    
    public void run() {
        // Waits until the next minute to begin
        waitNextMinute();
        // Generates events list
        generateEvents();

        // Infinite loop, this thread will stop when the jvm is stopped (it is
        // a daemon).
        while(true) {
            
            Task nextEv = null;
            try {
                nextEv = (Task)(eventsQueue.getFirst());
            } catch(NoSuchElementException e) {
			// Fatal error 
			e.printStackTrace();
                 System.exit(1);
            }
            
            long intervalToSleep = nextEv.timeMillis - System.currentTimeMillis() + 50;
            if(intervalToSleep > 0) {
                // Waits until the next event
                try {
                    synchronized(this) {
                        wait(intervalToSleep);
                    }
                } catch(InterruptedException e) {
                    try {
                        readTimeTableFromFile(strFileName);
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
            
            Task ev = null;
            try {
                ev = (Task)(eventsQueue.removeFirst());
            } catch(NoSuchElementException e) {
		//Fatal Error
		 e.printStackTrace();
                System.exit(1);
            }
            
            // If it is a generate time table event, does it.
            if(ev.strClassName.equals(GENERATE_TIMETABLE_EVENT)) {
                generateEvents();
            }
            // Else, then tell the crontab to create the new task
            else {
                crontab.newTask(ev.strClassName, ev.iPriority, ev.strExtraInfo);
            }            
        }
    }

    private void waitNextMinute() {
        // Waits until the next minute
        long tmp = System.currentTimeMillis();
        if(tmp % 60000 != 0) {
            long intervalToSleep = ((((long)(tmp / 60000))+1) * 60000) - tmp + 50;
            
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

    /**
     * Reads the cron-table and converts it to the internal representation
     * @param strFileName Name of the tasks configuration file
     * @throws CrontabEntryException Error parsing tasks configuration file entry
     * @throws FileNotFoundException Error opening tasks configuration file
     * @throws IOException Error reading tasks configuration file
     */    
     public void readTimeTableFromFile(String strFileName) throws 
               CrontabEntryException, FileNotFoundException, IOException {
	Class cl = Cron.class;
        // BufferedReader input = new BufferedReader(new FileReader(strFileName));
	// This Line allows the events.cfg to be included in a jar file
	// and accessed from anywhere
	BufferedReader input = new BufferedReader(new InputStreamReader(cl.getResourceAsStream(strFileName)));	
	//
        String strLine;
        timeTable = new java.util.Vector();
        CommandParser entry;
        while((strLine = input.readLine()) != null)
        {
            strLine = strLine.trim();

            // Skips blank lines and comments
            if(strLine.equals("") || strLine.charAt(0) == '#')
                continue;
            
            entry = new CommandParser();
            entry.parseEntry(strLine);
            timeTable.add(entry);
        }
        input.close();
    }


    /**
     * Generates new time table entries (for new events).
     */
    public void generateEvents() {
        CommandParser entry;
        
        // Rounds the calendar to the previous minute
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(((long)(System.currentTimeMillis() / 60000)) * 60000));
        for(int i=0; i<iFrec; i++) {
            for(int j=0; j<timeTable.size(); j++) {
                entry = (CommandParser)(timeTable.get(j));
                if(entry.matchs(cal)) {
                    Task ev = new Task(
                        cal.getTime().getTime(),
                        entry.getClassName(),
                        entry.getPriority(),
                        entry.getExtraInfo());
                    eventsQueue.addLast(ev);
                }
            }
            cal.add(Calendar.MINUTE, 1);
        }
        
        // The last event is the new generation of the event list
        Task ev = new Task(cal.getTime().getTime(),
                        GENERATE_TIMETABLE_EVENT, 0, null);
        eventsQueue.addLast(ev);
    }
    
    private class Task {
        long timeMillis;
        String strClassName;
        int iPriority;
        String[] strExtraInfo;
        
        /** 
         * Creates a new Task (Event of the internal events table)
         * @param timeMillis Time in millis from 1970 to throw the event
         * @param strClassName Name of the class that should the event throw
         * @param iPriority Priority of the event thread
         * @param strExtraInfo Extra Information given to the event class when created
         */        
        public Task(long timeMillis, String strClassName, int iPriority, 
                        String[] strExtraInfo) {
            this.timeMillis = timeMillis;
            this.strClassName = strClassName;
            this.iPriority = iPriority;
            this.strExtraInfo = strExtraInfo;
        }
    }
}
