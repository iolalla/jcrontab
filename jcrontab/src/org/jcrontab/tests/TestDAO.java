/*
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
package org.jcrontab.tests;

import org.jcrontab.data.CrontabEntryBean;
import org.jcrontab.data.CrontabEntryDAO;

/**
 * This class helps the testing process to make easier testing
 * The objective of this test is to test the DAO.
 * This class prints all the CrontabEntryBean of the system.
 * @author $Author: iolalla $
 * @version $Revision: 1.8 $
 */
public class TestDAO {
	

	public static void main(String[] args) {
	
	try {
        CrontabEntryBean[] listOfBeans= CrontabEntryDAO.getInstance().findAll();
        for (int i = 0; i < listOfBeans.length; i++) {
            System.out.print("The bean :" + i + " has been edited... \n");
            
            System.out.print(listOfBeans[i].toXML());
            
            System.out.print("The bean " + i + " has been edited... \n");
            System.out.print(listOfBeans[i]);
        }
        
        CrontabEntryDAO.getInstance().store(listOfBeans);
	} catch (Exception e) {
	e.printStackTrace();
	}
	}

}
