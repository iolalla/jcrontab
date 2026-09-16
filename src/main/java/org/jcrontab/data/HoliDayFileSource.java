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
package org.jcrontab.data;

import org.jcrontab.Crontab;
import org.jcrontab.log.Log;
import java.util.Vector;
import java.io.*;
import java.text.SimpleDateFormat;

/**
 * This HoliDaySource builds a basic holidays information source.
 * @author $Author: iolalla $
 * @version $Revision: 1.4 $
 */
public class HoliDayFileSource implements HoliDaySource {
    private HoliDay[] hol = null;
    
    public HoliDay[] findAll() throws Exception {
        
        if (hol != null) return hol;
        
        String filename = Crontab.getInstance().getProperty(
								"org.jcrontab.data.holidaysfilesource");
        String dateFormat = Crontab.getInstance().getProperty(
								"org.jcrontab.data.dateFormat");
        
        Vector listOfLines = new Vector();
        
             if (filename == null || filename == "") 
                 throw new FileNotFoundException("Should provide a valid file" +
                "name plz set correctly org.jcrontab.data.holidaysfilesource");
             
             if (dateFormat == null || dateFormat == "") 
                 dateFormat="dd/MM/yyyy";
             
             InputStream fis = new FileInputStream(filename);
             
             BufferedReader input = new BufferedReader(
												new InputStreamReader(fis));
		     
             SimpleDateFormat formater = new SimpleDateFormat(dateFormat);
             
			 String strLine;
				
				while((strLine = input.readLine()) != null){
					//System.out.println(strLine);
					strLine = strLine.trim();
					listOfLines.add(strLine);
				}
             fis.close();
             
             Vector validHolidays = new Vector();
             for (int i = 0; i < listOfLines.size(); i++) {
                 String line = (String) listOfLines.get(i);
                 if (line == null || line.trim().isEmpty() || line.trim().startsWith("#")) {
                     continue;
                 }
                 try {
                     HoliDay holiday = new HoliDay();
                     holiday.setId(validHolidays.size());
                     holiday.setDate(formater.parse(line.trim()));
                     validHolidays.add(holiday);
                 } catch (Throwable ex) {
                     Log.error("Error parsing holiday in " + filename + " at line " + (i + 1) + " [" + line + "]: "
                             + ex.getMessage());
                 }
             }
             hol = new HoliDay[validHolidays.size()];
             for (int i = 0; i < validHolidays.size(); i++) {
                 hol[i] = (HoliDay) validHolidays.get(i);
             }
             return hol;
    }
}
