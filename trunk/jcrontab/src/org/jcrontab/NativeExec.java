/*
 *  This file is part of the jcrontab package
 *  Copyright (C) 2001 Michael C. Daconta
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

import java.util.*;
import java.io.*;

/**
 *	This class ejecutes a native command
 * @author $Author: iolalla $
 * @version $Revision: 1.7 $
 */
public class NativeExec {
	/**
	 * main method
	 * @param args String[] the params passed from the console
	 */
    public static void main(String args[]) {
        if (args.length < 1) {
            System.out.println("USAGE: java nativeExec <cmd>");
            System.exit(1);
        }
        
        try {
			//with this variable will be done the swithcing
            String osName = System.getProperty("os.name" );

            String[] cmd = new String[3];
			//only will work with Windows NT
            if( osName.equals( "Windows NT" ) ) {
                cmd[0] = "cmd.exe" ;
                cmd[1] = "/C" ;
                cmd[2] = args[0];
            }
			//only will work with Windows 95
            else if( osName.equals( "Windows 95" ) ) {
                cmd[0] = "command.com" ;
                cmd[1] = "/C" ;
                cmd[2] = args[0];
            }
			//only will work with Windows 2000
			else if( osName.equals( "Windows 2000" ) ) {
                cmd[0] = "cmd.exe" ;
                cmd[1] = "/C" ;
                cmd[2] = args[0];
            }
			//only will work with Linux
			else if( osName.equals( "Linux" ) ) {
                cmd = args;
            }	
			//will work with the rest
			else  {
                cmd = args;
            }
            
            Runtime rt = Runtime.getRuntime();
	        // Executes the command
            Process proc = rt.exec(cmd);
            // any error message?
            StreamGobbler errorGobbler = new 
                StreamGobbler(proc.getErrorStream(), "ERROR");            
            
            // any output?
            StreamGobbler outputGobbler = new 
                StreamGobbler(proc.getInputStream(), "OUTPUT");
                
            // kick them off
            errorGobbler.start();
            outputGobbler.start();
                                    
            // any error???
            int exitVal = proc.waitFor();
            System.out.println("ExitValue: " + exitVal);        
        } catch (Throwable t) {
            t.printStackTrace();
          }
    }
}
