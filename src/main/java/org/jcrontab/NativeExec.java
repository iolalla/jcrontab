/**
 *  This file is part of the jcrontab package
 *  Copyright (C) 2001-2026 Israel Olalla
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
 *  iolalla@gmail.com
 *
 */

package org.jcrontab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.jcrontab.log.Log;

/**
 * Executes a native system command.
 *
 * @author Israel Olalla
 */
public class NativeExec {

    /**
     * main method
     * 
     * @param args String[] the params passed from the console
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            Log.error("Missing command for org.jcrontab.NativeExec. Usage: org.jcrontab.NativeExec <cmd>");
            return;
        }

        try {
            boolean isWindows = System.getProperty("os.name", "")
                    .toLowerCase(Locale.ROOT)
                    .contains("win");

            List<String> command = new ArrayList<>();
            if (isWindows) {
                command.add("cmd.exe");
                command.add("/C");
            }
            command.addAll(Arrays.asList(args));

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO();
            Process proc = pb.start();
            int exitVal = proc.waitFor();
            Log.info("ExitValue: " + exitVal);
        } catch (Throwable t) {
            Log.error(t.toString(), t);
        }
    }
}
