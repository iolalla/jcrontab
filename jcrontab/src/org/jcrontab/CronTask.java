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

import org.jcrontab.*;

/** 
 * Implements a runnable task that can be scheduled and executed by the
 * Crontab.
 * If a new kind of task is desired, this class should be extended and the
 * abstract method runTask should be overwritten.
 * @author iolalla
 * @version 0.01
 */


public abstract class CronTask extends Thread
{
    private Crontab crontab;
    private int identifier;
    private String[] extraInfo;
    
    /**
     * Constructor of a task.
     * We always call the constructor with no arguments, because the tasks
     * are created dinamically (by Class.forName).
     * You should call the method setParams inmediatly after creating a new task
     */
    public CronTask() {
    }

    /**
     * Selects the initial parameters for the task. As a task is created loaded
     * dinamically from the class name, the default constructor called is
     * the one with no arguments. You should call this method after creating
     * the new instance of the task.
     * @param cront The Crontab that creates and executes this task. It 
     * should be used to have access to other tasks, in order to wait for them
     * or other tasks operations.
     * @param iTaskID Identifier of the task
     * @param strExtraInfo Extra information given to the task when created
     */
    public final void setParams(Crontab cront,  
            int iTaskID, String[] strExtraInfo) {
        crontab = cront;
        identifier = iTaskID;
        extraInfo = strExtraInfo;
    }

    /**
     * Runs this task. Each class that extends CronTask should overwrite
     * this method. 
     */
    public abstract void runTask();

    /**
     * Returns the aditional parameters given to the task in construction
     * @return The aditional parameters given to the task in construction
     */
    protected final String[] getExtraInfo() {
        return extraInfo;
    }

    /**
     * Tells this task to finish its execution
     * This method should be overwritten by the classes that extends 
     * CronTask wich require to close some connections, or in general to
     * let the system in a consistent state
     */
    public void finish() {
        return;
    }


    /**
     * Runs this task
     */
    public final void run() {
        // Runs the task
        runTask();
        
        // Deletes the task from the task manager array
        crontab.deleteTask(identifier);
    }
}
