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
import java.util.Calendar;
import java.util.StringTokenizer;

/** 
 * A row in the time table, that describes the frecuency that a process should
 * be executed.
 * @author iolalla
 * @version 0.01
 */

public class CommandParser
{
    private String strClassName;
    private String[] strExtraInfo;
    
    private boolean[] bHours;
    private boolean[] bMinutes;
    private boolean[] bMonths;
    private boolean[] bDaysOfWeek;
    private boolean[] bDaysOfMonth;
    
    /** 
     * Creates a new time table entry
     */    
    public CommandParser() {
        bHours = new boolean[24];
        bMinutes = new boolean[60];
        bMonths = new boolean[12];
        bDaysOfWeek = new boolean[7];
        bDaysOfMonth = new boolean[31];
        
        // Initializes all arrays to false
        for(int i=0; i<60; i++) {
            if(i<24)
                bHours[i] = false;
            if(i<60)
                bMinutes[i] = false;
            if(i<12)
                bMonths[i] = false;
            if(i<7)
                bDaysOfWeek[i] = false;
            if(i<31)
                bDaysOfMonth[i] = false;
        }
    }
    
    /** 
     * Parses a string describing this time table entry
     * @param strEntry String describing the time table entry
     * @throws Exception Error parsing the string
     */    
    public void parseEntry(String strEntry) throws Exception {
        StringTokenizer tokenizer = new StringTokenizer(strEntry, " ");
        int numTokens = tokenizer.countTokens();
        int i=0;
        for(i = 0; tokenizer.hasMoreElements(); i++) {
            String token = tokenizer.nextToken();
            switch(i)
            {
                case 0:     // Hours
                    parseToken(token,bHours);
                    break;
                case 1:     // Minutes
                    parseToken(token,bMinutes);
                    break;
                case 2:     // Months
                    parseToken(token,bMonths);
                    break;
                case 3:     // Days of week
                    parseToken(token,bDaysOfWeek);
                    break;
                case 4:     // Days of month
                    parseToken(token,bDaysOfMonth);
                    break;
                case 5:     // Name of the class
                    strClassName = token;
                    break;
                case 6:    // Class parameters 
                    strExtraInfo = new String[numTokens - 6];
                    for(strExtraInfo[i - 6] = token; tokenizer.hasMoreElements(); strExtraInfo[i - 6] = tokenizer.nextToken()) {
                        i++;
                    }
                    break;
                default:
                    break;
            }
        }
       // The string must Contain 6 tokens 
        if(i<6) {
            throw new Exception("The number of tokens must  be 6");
        }
    }

    private void parseToken(String token, boolean[] arrayBool) 
                    throws Exception {
        int i;
        try
        {
            if(token.equals("*")) {
                for(i=0; i<arrayBool.length; i++) {
                    arrayBool[i] = true;
                }
                return;
            }

            int index = token.indexOf(",");
            if(index > 0)
            {
                StringTokenizer tokenizer = new StringTokenizer(token, ",");
                while(tokenizer.hasMoreTokens()) {
                    parseToken(tokenizer.nextToken(), arrayBool);
                }
                return;
            }
            
            index = token.indexOf("-");
            if(index > 0)
            {
                int start = Integer.parseInt(token.substring(0, index));
                int end = Integer.parseInt(token.substring(index + 1));
                for(int j=start; j<end; j++)
                    arrayBool[j] = true;
                return;
            }
            
            index = token.indexOf("/");
            if(index > 0)
            {
                int each = Integer.parseInt(token.substring(index + 1));
                for(int j=0; j<arrayBool.length; j+= each)
                    arrayBool[j] = true;
                return;
            }

            else
            {
                arrayBool[Integer.parseInt(token)] = true;
                return;
            }
        }
        catch(Exception e)
        {
            throw new Exception(e.toString());
        }
    }
        
    /** 
     * Returns the name of the class that the event calls when activated
     * @return The name of the class that the event calls when activated
     */    
    public String getClassName() {
        return strClassName;
    }
    
    /** 
     * Returns the extra information given to the task thrown when the thread is
     * activated
     * @return The extra information given to the task thrown when the thread is
     * activated
     */    
    public String[] getExtraInfo() {
        return strExtraInfo;
    }

    /** 
     * Returns true if the time table entry matchs with the calendar given
     * @param cal Calendar to compare with the time table entry
     * @return true if the time table entry matchs with the calendar given
     */    
    public boolean matchs(Calendar cal) {

        return ( bHours[cal.get(Calendar.HOUR_OF_DAY)] &&
            bMinutes[cal.get(Calendar.MINUTE)] &&
            bMonths[cal.get(Calendar.MONTH)] &&
            bDaysOfWeek[cal.get(Calendar.DAY_OF_WEEK)-1] &&
            bDaysOfMonth[cal.get(Calendar.DAY_OF_MONTH)-1]);
    }
}
