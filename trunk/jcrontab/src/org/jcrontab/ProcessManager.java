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

import java.util.HashMap;
import org.jcrontab.data.Process;
import org.jcrontab.data.ProcessDAO;

/** 
 * Manages the creation and execution of all the scheduled tasks 
 * of jcrontab. This class is the core of the jcrontab
 * @author $Author: iolalla $
 * @version $Revision: 1.2 $
 */

public class ProcessManager {
    
    private static ProcessManager singleton = null;
    
    private HashMap processes = null;
    
    private int counter = 0;
    /**
     * ProcessManager constructor, private to avoid more than one instance running
     * at the same time
     */
    private ProcessManager() {
        processes = new HashMap();
    }
    /**
     *  Returns the only instance of this class
     *  we've choosen a singleton pattern to avoid launch different Crontab
     *  If you need diferent crontab classes to be launched only should 
     *  Change the private constructor to public.
     *  @return singleton the only instance of this class
     */
    public static synchronized ProcessManager getInstance(){
			if (singleton == null){
				singleton = new ProcessManager();
			}
			return singleton;
    }
    
    public void addProcess(String[] args) {
        String className, methodName = null;
        
        for (int i = 0 ; i < args.length ; i++) {
            
            CronTask task = new CronTask();
            
            String[] params = args[i].split(" ");
            String[] resultParams = new String[params.length - 1];
            int y = 0;
            for (int j = 1 ; j < params.length ; j++) {
                resultParams[y] = params[j];
                y++;
            }
            
            if (params[0].indexOf("#") != -1) {
                String[] firstParam = params[0].split("#");
                className = firstParam[0];
                methodName = firstParam[1];
            } else {
                className = params[0];
            }
            task.setParams(Crontab.getInstance(), i ,className, methodName, 
                                resultParams);
            task.setOrder(i);
            task.setProcessId(counter);
            //process.addTask(task);
            //processes.put(process, process);
            //runProcess(process);
        }
                    counter++;
  
    }
    
    public void addProcesses(Process[] newProcesses) {
        for (int i = 0; i < newProcesses.length; i++) {
            processes.put(newProcesses[i], newProcesses[i]);
            runProcess(newProcesses[i]);
        }
    }
    
    public void runProcess(Process process) {
        System.out.println("Proceso: " + process);
    }
    
    public void startAll() throws Exception {
        addProcesses(ProcessDAO.getInstance().findAll());
    }
    
    public void startProcess(int id) throws Exception {
        Process process = new Process();
        process.setId(id);
        Process[] processex = new Process[1];
        processex[0] = ProcessDAO.getInstance().find(process);
        addProcesses(processex);
    }
    
    public static void main(String[] args) {
        ProcessManager.getInstance().addProcess(args);
    }
}
