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

/**
 *	This class starts a jcrontab.
 *  Call the main method with two parameters and will start a Crontab
 * @author $Author: iolalla $
 * @version $Revision: 1.6 $
  *          pfharris 7/31/2003
 *          fixed start up messsage - to match the code
 */

public class Jcrontab {
	//This variable defines the Crontab
	static private Crontab crontab = null;
	/**
	 * main method
	 * @param args String[] the params passed from the console
	 */
	public static void main(String[] args) {
	
    String events = new String();
	crontab = Crontab.getInstance();
        
        if (args.length == 1) {
            events = args[0];
            //This block starts the whole thing
            try {
                ShutdownHook();
                crontab.getInstance().setDaemon(false);
                crontab.getInstance().init(events);
                System.out.println("Working...");
            } catch (Exception e) {
            e.printStackTrace();
            }
        } else if (args.length == 0) {
            try {
                ShutdownHook();
                crontab.getInstance().setDaemon(false);
                crontab.getInstance().init();
                System.out.println("Working...");
            } catch (Exception e) {
            e.printStackTrace();
            }
        } else {
            System.out.println("Usage: java -jar jcrontab.jar <filename>");
            System.out.println("filename = path the properies file");
            System.out.println("\t If filename is not passed: ");
            System.out.print("\t default files are created in users $home directory");
        }
	}
	/**
	 * This method seths a ShutdownHook to the system
	 *  This traps the CTRL+C or kill signal and shutdows 
	 * Correctly the system.
	 */ 
	 public static void ShutdownHook() throws Exception {
             Runtime.getRuntime().addShutdownHook(new Thread() {         
	 	public void run() {
			System.out.println("Shutting down...");
			// stops the system in 200 miliseconds :-)
			crontab.uninit(200);
			System.out.println("Stoped");
            	}
			});
    }

}

