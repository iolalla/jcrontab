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


import org.jcrontab.Crontab;


public class jcrontab {
	
	static private Crontab crontab = null;

	public static void main(String[] args) {

	String events = new String();
	int iFrec = 0; 

	crontab = Crontab.getInstance();
        
	if (args.length > 0 && args.length == 2) {
		events = args[0];
		iFrec = Integer.parseInt(args[1]);
	} else if (args.length == 0) {
	       events = "events.cfg";
	       iFrec = 3;
	} else {
		System.out.println("You have two options:");
		System.out.println("First:");
		System.out.println("\tNo parameters passed: ");
		System.out.print("org.jcrontab.jcrontab");
		System.out.println("\tIt assumes you are executing: ");
		System.out.print("org.jcrontab.jcrontab events.cfg 60");
		System.out.println("Second:");
		System.out.println("\tPassing two parameters events "); 
		System.out.print(" file and frequency to reload this ");
		System.out.print(" file in minutes ");
		System.out.println("\torg.jcrontab.jcrontab yourfile.cfg 35");
	}
	 
	try {
		ShutdownHook();
		crontab.init(events,iFrec);
		System.out.println("Working....");
	    	Thread.currentThread().join();
	} catch (Exception e) {
	e.printStackTrace();
	}
		
	}

	 public static void ShutdownHook() throws Exception {
         try
         {
             Runtime.getRuntime().addShutdownHook(new Thread() {         
	 	public void run() {
			System.out.println("Shutting down...");
            	}
			});
         }
         catch (Exception e)
         {
             throw new Exception(e.toString());
         }
    }

}
