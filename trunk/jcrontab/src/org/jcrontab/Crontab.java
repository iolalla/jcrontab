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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

/** 
 * Manages the creation and execution of all the scheduled tasks of the engine
 * @author iolalla
 * @version 0.01
 */

public class Crontab
{
    private HashMap tasks;
    private HashMap loadedClasses;
    private int iNextTaskID;
    private Cron cron;
    private boolean bUninitializing = false;
    
    /**
     * Task manager constructor
     * @param dm The data manager to pass to the tasks created
     */
    public Crontab() {
        tasks = new HashMap();
        loadedClasses = new HashMap();
        iNextTaskID = 1;
    }
    
    /** 
     * Initializes the task manager, reading task table from configuration file
     * @param strFileName Name of the tasks configuration file
     * @param iTimeTableGenerationFrec Frecuency of regeneration of the events table
     * @throws CrontabEntryException Bad crontab entry in the tasks configuration file
     * @throws FileNotFoundException Tasks configuration file not found
     * @throws IOException Error reading tasks configuration file
     */    
    public void init(String strFileName, int iTimeTableGenerationFrec)
                    throws CrontabEntryException, FileNotFoundException,
                    IOException {
        // Creates the thread Cron, wich generates the engine events
        cron = new Cron(this, iTimeTableGenerationFrec);
        cron.init(strFileName);

        int iPriority;
        if(Thread.NORM_PRIORITY + 1 < Thread.MAX_PRIORITY)
            iPriority = Thread.NORM_PRIORITY + 1;
        else
            iPriority = Thread.MAX_PRIORITY;
        cron.setPriority(iPriority);
        cron.setDaemon(true);
        // Runs the scheduler as a daemon process
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
                tasks[i].finish();
            }

            synchronized(this) {
                wait(iSecondsToWait * 1000);
            }
        } catch(InterruptedException e) {
        }
    }

    /**
     * Creates and runs a new task
     * @param strClassName Name of the task
     * @param iPriority Priority of the task
     * @param strExtraInfo Extra Information given to the task
     * @return The identifier of the new task created, or -1 if could not create
     * the new task (maximum number of tasks exceeded or another error)
     */
    public synchronized int newTask(String strClassName, 
    				   String strMethodName, int iPriority, 
                                   String[] strExtraInfo) {
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
            newTask = (CronTask) cl.newInstance();
            newTask.setPriority(iPriority);
            newTask.setDaemon(true);
            newTask.setParams(this, iTaskID, strExtraInfo);

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
        } catch(InstantiationException e) {
		e.printStackTrace();
        } catch(IllegalAccessException e) {
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
     * Returns a reference to the task whose idenfier is iTaskID, or null if it
     * does not exists.
     * @return A reference to the task whose idenfier is iTaskID, or null if it
     * does not exists.
     * @param iTaskID The identifier of the task to get
     */
    public CronTask getTask(int iTaskID) {
        synchronized(tasks) {
            return ((TaskTableEntry)(tasks.get(new Integer(iTaskID)))).task;
        }
    }

    /**
     * Returns the total number of active tasks
     * @return The total number of active tasks
     */
    public int getNumTasks() {
        synchronized(tasks) {
            return tasks.size();
        }
    }

    /**
     * Returns the number of active tasks of the class strClassName
     * @return The number of active tasks of the class strClassName
     * @param strClassName Name of the class to find number of tasks of
     */
    public int getNumTasksOfClass(String strClassName) {
        int howMany = 0;
        synchronized(tasks) {
            Iterator iter = tasks.values().iterator();
            while(iter.hasNext()) {
                if(((TaskTableEntry)(iter.next())).strClassName.equals(strClassName)) {
                    howMany++;
                }
            }
        }
        return howMany;
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
     * Returns an array with all active tasks of a given class
     * @return An array with all active tasks of a given class
     * NOTE: Does not return the internal array because it is synchronized,
     * returns a copy of it.
     * @param strClassName Name of the class to find tasks of
     */
    public CronTask[] getTasksOfClass(String strClassName) {
        CronTask[] t;
        TaskTableEntry tmp;
        synchronized(tasks) {
            int i = 0;
            t = new CronTask[tasks.size()];
            Iterator iter = tasks.values().iterator();
            while(iter.hasNext()) {
                tmp = (TaskTableEntry)(iter.next());
                if(tmp.strClassName.equals(strClassName)) {
                    t[i] = tmp.task;
                    i++;
                }
            }
        }
        return t;
    }
    
    /**
     * Returns a reference to the scheduler
     * @return A reference to the scheduler
     */
    public void refreshEventsTable() {
        cron.interrupt();
    }

    /** Internal class that represents an entry in the task table */
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
