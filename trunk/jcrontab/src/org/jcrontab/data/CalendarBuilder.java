/**
 *  This file is part of the jcrontab package
 *  Copyright (C) 2001-2002 Israel Olalla
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
package org.jcrontab.data;

import java.util.Calendar;
import java.util.Date;
import org.jcrontab.log.Log;

/** This class processes a CrontabEntryBean and returns a Calendar. This class 
 * is a "conversor" to convert from CrontabEntries to Calendars.
 * Thanks to Javier Pardo for the idea and for the Algorithm
 * @author $Author: iolalla $
 * @version $Revision: 1.1 $
 */

public class CalendarBuilder  {
	
	public CrontabEntryBean getNextEvent(CrontabEntryBean[] cebs){
		CrontabEntryBean returnCeb ;
		Date returnDate, resultDate;
		returnDate = new Date(System.currentTimeMillis()+ 100000000L);
		for (int i = 0; i < cebs.length; i++) {
			resultDate = buildCalendar(cebs[i]);
			if (resultDate.getTime() < returnDate.getTime())
				returnDate = resultDate;
			System.out.println(resultDate);
		}
		return new CrontabEntryBean();
	}
	
	public Date buildCalendar(CrontabEntryBean ceb) {
		Date now = new Date(System.currentTimeMillis()); 
		return buildCalendar(ceb, now);
	}
	
	
	public Date buildCalendar(CrontabEntryBean ceb, Date afterDate) {
		Calendar after = Calendar.getInstance();
        after.setTime(afterDate);

		int second = getNextIndex(ceb.getBSeconds(), after.get(Calendar.SECOND));
        if (second == -1) {
            second = getNextIndex(ceb.getBSeconds(), 0);
            after.add(Calendar.MINUTE, 1);
        }
		
        int minute = getNextIndex(ceb.getBMinutes(), after.get(Calendar.MINUTE));
        if (minute == -1) {
			second = getNextIndex(ceb.getBSeconds(), 0);
            minute = getNextIndex(ceb.getBMinutes(), 0);
            after.add(Calendar.HOUR_OF_DAY, 1);
        }

        int hour = getNextIndex(ceb.getBHours(), after.get(Calendar.HOUR_OF_DAY));
        if (hour == -1) {
			second = getNextIndex(ceb.getBSeconds(), 0);
            minute = getNextIndex(ceb.getBMinutes(), 0);
            hour = getNextIndex(ceb.getBHours(), 0);
            after.add(Calendar.DAY_OF_MONTH, 1);
        }

        int dayOfMonth = getNextIndex(ceb.getBDaysOfMonth(), after.get(Calendar.DAY_OF_MONTH) - 1);
        if (dayOfMonth == -1) {
			second = getNextIndex(ceb.getBSeconds(), 0);
            minute = getNextIndex(ceb.getBMinutes(), 0);
            hour = getNextIndex(ceb.getBHours(), 0);
            dayOfMonth = getNextIndex(ceb.getBDaysOfMonth(), 0);
            after.add(Calendar.MONTH, 1);
        }

        int month = getNextIndex(ceb.getBMonths(), after.get(Calendar.MONTH));
        if (month == -1) {
			second = getNextIndex(ceb.getBSeconds(), 0);
            minute = getNextIndex(ceb.getBMinutes(), 0);
            hour = getNextIndex(ceb.getBHours(), 0);
            dayOfMonth = getNextIndex(ceb.getBDaysOfMonth(), 0);
            month = getNextIndex(ceb.getBMonths(), 0);
            after.add(Calendar.YEAR, 1);
        }

        Date byMonthDays = getTime(second, minute, hour, dayOfMonth + 1,
                       month, after.get(Calendar.YEAR));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(byMonthDays);

		boolean[] bDaysOfWeek = ceb.getBDaysOfWeek();
		
         if (bDaysOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1]) {
             return calendar.getTime();
         } else {
             calendar.add(Calendar.DAY_OF_YEAR, 1);
             return buildCalendar(ceb , calendar.getTime());
         }
	}

    private Date getTime(int seconds,
                         int minutes,
                         int hour,
                         int dayOfMonth,
                         int month,
                         int year) {
        try {
            Calendar cl = Calendar.getInstance();
            if (hour >= 0 && hour <= 12)
                cl.set(Calendar.AM_PM, Calendar.AM);
            if (hour >= 13 && hour <= 23)
                cl.set(Calendar.AM_PM, Calendar.PM);
            cl.setLenient(false);
            cl.set(Calendar.SECOND, seconds);
            cl.set(Calendar.MINUTE, minutes);
            cl.set(Calendar.HOUR_OF_DAY, hour);
            cl.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            cl.set(Calendar.MONTH, month);
            cl.set(Calendar.YEAR, year);
            return cl.getTime();
        } catch (Exception e) {
            Log.error("Smth was wrong:", e);
            return null;
        }
    }

    private int getNextIndex(boolean[] array, int start) {
        for (int i = start; i < array.length; i++) {
            if (array[i]) return i;
        }
        return -1;
    }
}
