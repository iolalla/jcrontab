/**
 *  This file is part of the jcrontab package
 *  Copyright (C) 2001-2022 Israel Olalla
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

package org.jcrontab.tests;

import java.util.Date;

/**
 * This class helps the testing process to make easier testing
 * The objective of this test is to test Thread passing parameters 
 * to the constructor
 * @author $Author: iolalla $
 * @version $Revision: 1.8 $
 */
public class TaskTest3 extends Thread {
	
	private static String[] args;

	public TaskTest3(String[] args){
		this.args = args;
	}

	public void run() {

	Date now = new Date();

	System.out.print(now + "\n");

	System.out.print("Hola mundo from TaskTest3 \n");
	}
}
