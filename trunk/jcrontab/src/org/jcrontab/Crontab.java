/*
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
package org.jcrontab;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.sql.SQLException;
import java.util.Properties;

import org.jcrontab.data.CrontabEntryException;

/** 
 * Manages the creation and execution of all the scheduled tasks 
 * of jcrontab. This class is the core of the jcrontab
 * @author $Author: iolalla $
 * @version $Revision: 1.22 $
 */

public class Crontab {
    private HashMap tasks;
    private HashMap loadedClasses;
    private int iNextTaskID;
	private Properties prop = new Properties();
	
	private int iTimeTableGenerationFrec = 60;
	/** The Cron that controls the execution of the tasks */
    private Cron cron;
    private boolean bUninitializing = false;
    
    private String strFileName = "properties.cfg";
    /** The only instance of this cache */
    private static Crontab singleton = null;
    
    /**
     * Crontab constructor
     * Change the default constructor to public if you need 
     * more than an instance running on the system
     */
    private Crontab() {
        tasks = new HashMap();
        loadedClasses = new HashMap();
        iNextTaskID = 1;
    }
    /**
     *  Returns the only instance of this class
     *  we've choosen a singleton pattern to avoid launch different Crontab
     *  If you need diferent crontab classes to be launched only should 
     *  Change the private constructor to public.
     *  @return singleton the only instance of this class
     */ 
    public static synchronized Crontab getInstance(){
			if (singleton == null){
				singleton = new Crontab();
			}
			return singleton;
    }
    
    /** 
     * Initializes the task manager, reading task table from configuration 
     * file
     * @param iTimeTableGenerationFrec Frecuency of regeneration of the events
     * table
     * @throws Exception
     */    
    public void init() throws Exception {
       // Properties prop = new Properties
        // Creates the thread Cron, wich generates the engine events
		loadConfig();          
        cron = new Cron(this, iTimeTableGenerationFrec);
        cron.start();
        bUninitializing = false;
    }
    
    /** 
     * Initializes the task manager, reading task table from configuration 
     * file
     * @param strFileName Name of the tasks configuration file
     * @param iTimeTableGenerationFrec Frecuency of regeneration of the events
     * table
     * @throws Exception
     */    
    public void init(String strFileName, int iTimeTableGenerationFrec)
                    throws Exception {
		this.strFileName = strFileName;
		this.iTimeTableGenerationFrec = iTimeTableGenerationFrec;
        // Creates the thread Cron, wich generates the engine events         
        cron = new Cron(this, iTimeTableGenerationFrec);
		
		loadConfig();
        cron.start();
        bUninitializing = false;
    }

    /** 
     * UnInitializes the task manager. Calls to the method uninit() of each of
     * the tasks running.
     * @param iSecondsToWait Number of seconds to wait for the tasks to end
     * their process before returning from this method
     */    
    public void uninit(int iSecondsToWait) {
        try {
            // Updates uninitializing flag
            bUninitializing = true;
            
            CronTask[] tasks = getAllTasks();

            for(int i=tasks.length-1; i>=0; i--) {
                tasks[i].join(iSecondsToWait);
            }
        } catch(InterruptedException e) {
	    e.printStackTrace();
        }
    }
	/**
	 *	This method loads the config for the whole Crontab
	 *	@param property
	 *  @return value
	 */
	private void loadConfig() throws Exception {
		 // Get the Params from the config File
		 File filez = new File(strFileName);
         FileInputStream input = new FileInputStream(filez);
         prop.load(input);
         input.close();
	}
	/**
	 *	This method gets the value of the given property
	 *	@param property
	 *  @return value
	 */
	public String getProperty(String property) {
		 return	prop.getProperty(property);
	}
	
	/**
	 *	This method sets the given property
	 *	@param property
	 *  @param value
	 */
	 public void setProperty(String property, String value) {
		 prop.getProperty(property, value);
	}

    /**
     * Creates and runs a new task
     * @param strClassName Name of the task
	 * @param strMethodName Name of the method that will be called
     * @param strExtraInfo Extra Information given to the task
     * @return The identifier of the new task created, or -1 if could not create
     * the new task (maximum number of tasks exceeded or another error)
     */
    public synchronized int newTask(String strClassName, 
    				   String strMethodName, String[] strExtraInfo) {
        CronTask newTask;
        Class cl;
        int iTaskID;

        // Do not run new tasks if it is uninitializing
        if(bUninitializing) {
            return -1;
        }

        try {
            iTaskID = iNextTaskID;

            cl = (Class)(loadedClasses.get(strClassName));
            // If the class was not previously created, then creates it
            if(cl == null) {
                cl = Class.forName(strClassName);
                loadedClasses.put(strClassName, cl);
            }
            else {
            }
            // Creates the new task

            newTask = new CronTask();
            newTask.setParams(this, iTaskID, strClassName, strMethodName, 
								strExtraInfo);

            synchronized(tasks) {
                tasks.put(new Integer(iTaskID), 
                          new TaskTableEntry(strClassName, newTask));
            }
            // Starts the task execution
            newTask.start();
            // Increments the next task identifier
            iNextTaskID++;
            return iTaskID;

        } catch(ClassNotFoundException e) {
			e.printStackTrace();
        } catch(Exception e) {
			e.printStackTrace();
        }

        return -1;
    }

    /**
     * Removes a task from the internal arrays of active tasks. This method
     * is called from method run() of CronTask when a task has finished.
     * @return true if the task was deleted correctly, false otherwise
     * @param iTaskID Identifier of the task to delete
     */
    public boolean deleteTask(int iTaskID) {
        synchronized(tasks) {
            if( tasks.remove(new Integer(iTaskID)) == null)
                return false;
            return true;
        }
    }

    /**
     * Returns an array with all active tasks
     * @return An array with all active tasks
     * NOTE: Does not returns the internal array because it is synchronized,
     * returns a copy of it.
     */
    public CronTask[] getAllTasks() {
        CronTask[] t;
        synchronized(tasks) {
            int i = 0;
            t = new CronTask[tasks.size()];
            Iterator iter = tasks.values().iterator();
            while(iter.hasNext()) {
                t[i] = ((TaskTableEntry)(iter.next())).task;
                i++;
            }
        }
        return t;
    }
    
    /** 
     * Internal class that represents an entry in the task table 
     */
    private class TaskTableEntry
    {
        String strClassName;
        CronTask task;

        /** Constructor of an entry of the task table
         * @param strClassName Name of the class of the task
         * @param task Reference to the task
         */        
        public TaskTableEntry(String strClassName,
                                 CronTask task) {
            this.strClassName = strClassName;
            this.task = task;
        }
    }
}
