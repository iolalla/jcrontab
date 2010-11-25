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
 
package org.jcrontab;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays; 
import javax.naming.InitialContext; 

import org.jcrontab.log.Log;

/** 
 * Implements a runnable task that can be scheduled and executed by the
 * Crontab.
 * If a new kind of task is desired, this class should be extended and the
 * abstract method runTask should be overwritten.
 * @author $Author: iolalla $
 * @version $Revision: 1.27 $
 */
public class CronTask
    extends Thread {
    private Crontab crontab;
    private int identifier;
    private String[] strExtraInfo;
    public String strClassName;
    public String strMethodName;
    public String[] strParams;
    private static Runnable runnable = null;

    /**
     * Constructor of a task.
     * @param strClassName Name of the Class
     * @param strParams Parameters for the class or the Method 
     */
    public CronTask(String strClassName, String strMethodName, 
                    String[] strParams) {
        this.strClassName = strClassName;
        this.strMethodName = strMethodName;
        this.strParams = strParams;
    }
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
    public final void setParams(Crontab cront, int iTaskID, 
                                String strClassName, String strMethodName, 
                                String[] strExtraInfo) {
        crontab = cront;
        identifier = iTaskID;
        this.strExtraInfo = strExtraInfo;
        this.strMethodName = strMethodName;
        this.strClassName = strClassName;
    }
    /**
     * Returns the aditional parameters given to the task in construction
     * @return The aditional parameters given to the task in construction
     */
    protected final String[] getExtraInfo() {
        return strExtraInfo;
    }
    /**
     * Returns the Method Name given to the task in construction
     * @return The aditional parameters given to the task in construction
     */
    protected final String getMethodName() {
        return strMethodName;
    }
    /**
     * Runs this task. This method does the whole enchilada.
     * This method decides wich method call in  the given class
     */
    public void runTask() {
    
        try {
            // Do class instantiation first (common to all cases of 'if' below)
            Class cl = CronTask.class.getClassLoader().loadClass(strClassName);
            
            // Check if we have a Method
            if (!("".equals(strMethodName))) {
                try {
                    Class[] argTypes = {String[].class};
                    Object[] arg = {strExtraInfo};

                    // accessing the given method
                    try {
                        Method mMethod = cl.getMethod(strMethodName, argTypes);
                        mMethod.invoke(null, arg);
                    } catch (NoSuchMethodException e) {

                        // If its not a method meaybe is a Constructor
                        try {
                            Constructor con = cl.getConstructor(argTypes);
                            runnable = (Runnable)con.newInstance(arg);
                        } catch (NoSuchMethodException e2) {

                            // Well maybe its not a method neither a constructor
                            // Usually this code will never run
                            // but?
                            runnable = (Runnable)cl.newInstance();
                        }

                        runnable.run();
                    }

                    // let's catch Throwable its more generic
                } catch (Exception e) {
                    Log.error(e.toString(), e);
                }

                // No method given
            } else {
                try {
                    Class[] argTypes = {String[].class};
                    Object[] arg = {strExtraInfo};

                    // lets try with main()
                    try {
                        Method mMethod = cl.getMethod("main", argTypes);
                        mMethod.invoke(null, arg);
                    } catch (NoSuchMethodException et) {
                        try {

                            // If its not a method meaybe is a Constructor
                            Constructor con = cl.getConstructor(argTypes);
                            runnable = (Runnable)con.newInstance(arg);
                        } catch (NoSuchMethodException e2) {

                            // Well maybe its not a method neither a constructor
                            // Usually this code will never run
                            // but?
                            runnable = (Runnable)cl.newInstance();
                        }

                        runnable.run();
                    }

                } catch (Exception e) {
                    Log.error(e.toString(), e);
                }
            }
        } catch (Exception e) {
        	if (strMethodName != null && strMethodName.length() > 0) {
        		EJBLookup.tryEjb(strClassName, strMethodName, strExtraInfo);
            } else { 
                Log.error("Unable to instantiate class: " + strClassName, e) ; 
            }        		
        }
    }
 
	/**
     * Runs this task
     */
    public final void run() {
        File tempFile = null;

        try {
            if (Crontab.getInstance().getProperty("org.jcrontab.SendMail.to") != null) {
                tempFile = new File(strClassName).createTempFile("jcrontab", 
                                                                 ".tmp");

                FileOutputStream fos = new FileOutputStream(tempFile);
                PrintStream pstream = new PrintStream(fos);
                System.setOut(pstream);
            }

            // Runs the task
            runTask();

            // Deletes the task from the crontab array
            crontab.getInstance().deleteTask(identifier);

            //This line sends the email to the config
            if (Crontab.getInstance().getProperty("org.jcrontab.SendMail.to") 
							!= null) {
                SendMail sndm = new SendMail();
                sndm.send(tempFile);
                tempFile.delete();
            }
        } catch (Exception e) {
            Log.error(e.toString(), e);
        }
    }
}
