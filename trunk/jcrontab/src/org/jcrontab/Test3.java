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

public class Test3 {
	
	static private Crontab crontab = null;

	public static void main(String[] args) {
	
	crontab = new Crontab();
	try {
	int iFrec = 60;
	crontab.init("events3.cfg",iFrec);
	System.out.println("Working....");
	for(;;) {
	
	}
	} catch (Exception e) {
	e.printStackTrace();
	}
		
	}

}
