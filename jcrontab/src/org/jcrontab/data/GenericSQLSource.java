/*
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

import java.io.PrintStream;
import java.util.Vector;
import java.util.Properties;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.sql.*;
import org.jcrontab.Cron;


/**
 * This class is only a generic example and doesn't aim to solve all the needs
 * for the differents system's. if you want to make this class to fit your needs
 * feel free to do it and remember the license.
 * On of the things this class does is to open a connection to the database
 * , this is nasty and very expensive, y you want to integrate jcrontab with a 
 * pool like poolman or jboss it's quite easy, should substitute connection logic
 * with particular one.
 * @author  iolalla@yahoo.com
 * @version 0.01
 */
public class GenericSQLSource implements DataSource {

    private static GenericSQLSource instance;
    
	public static String queryAll = "select minute, hour, dayofmonth, month, "
		   						  + "dayofweek, task, extrainfo from events";
	public static String querySearching = "select minute, hour, dayofmonth, month, "
		   						  + "dayofweek, task, extrainfo from events" 
								  + " where task = ? ";
	public static String queryStoring = "";

    private static Properties props = new Properties();
	
    /** Creates new GenericSQLSource */
	
    public GenericSQLSource() {
    }	

    public DataSource getInstance() {
		if (instance == null) {
		instance = new GenericSQLSource();
		}
		return instance;
    }
    
    public void init(Properties props) {
	
		this.props = props;
    }
    
    public CrontabEntryBean[] find(String cl) throws  CrontabEntryException, 
						ClassNotFoundException, FileNotFoundException, 
						IOException, SQLException
			 {
				Vector list = new Vector();

				Class.forName(props.getProperty("driver"));

				//db = DriverManager.getConnection(url, usr, pwd);
				Connection conn = DriverManager.getConnection(
								   props.getProperty("url"),
								   props.getProperty("username"),
								   props.getProperty("password"));

				PreparedStatement ps = conn.prepareStatement(querySearching);
				ps.setString(1 , cl);
				ResultSet rs = ps.executeQuery();
				if(rs!=null) {
					while(rs.next()) {
						String minute = rs.getString("minute");
						String hour = rs.getString("hour");
						String dayofmonth = rs.getString("dayofmonth");
						String month = rs.getString("month");
						String dayofweek = rs.getString("dayofweek");
						String task = rs.getString("task");
						String extrainfo = rs.getString("extrainfo");
						String line = minute + " " + hour + " " + dayofmonth 
								  + " " + month + " " 
								  + dayofweek + " " + task + " " + extrainfo;
					    CrontabEntryBean ceb = new CrontabEntryBean(line);
						list.add(ceb);
					}
					  rs.close();
				}
				ps.close();
				conn.close();
			CrontabEntryBean[] result = new CrontabEntryBean[list.size()];
			for (int i = 0; i < list.size(); i++) {
				result[i] = (CrontabEntryBean)list.get(i);
			}
			return result;
    }
    
    public CrontabEntryBean[] findAll() throws CrontabEntryException, 
						ClassNotFoundException, FileNotFoundException, 
						IOException, SQLException

			{
				Vector list = new Vector();

				Class.forName(props.getProperty("driver"));

				//db = DriverManager.getConnection(url, usr, pwd);
				Connection conn = DriverManager.getConnection(
								   props.getProperty("url"),
								   props.getProperty("username"),
								   props.getProperty("password"));

				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(queryAll);
				if(rs!=null) {
					while(rs.next()) {
						String minute = rs.getString("minute");
						String hour = rs.getString("hour");
						String dayofmonth = rs.getString("dayofmonth");
						String month = rs.getString("month");
						String dayofweek = rs.getString("dayofweek");
						String task = rs.getString("task");
						String extrainfo = rs.getString("extrainfo");
						String line = minute + " " + hour + " " + dayofmonth 
								  + " " + month + " " 
								  + dayofweek + " " + task + " " + extrainfo;
					    CrontabEntryBean ceb = new CrontabEntryBean(line);
						list.add(ceb);
					}
					  rs.close();
				}
				st.close();
				conn.close();
			CrontabEntryBean[] result = new CrontabEntryBean[list.size()];
			for (int i = 0; i < list.size(); i++) {
				result[i] = (CrontabEntryBean)list.get(i);
			}
			return result;
	}
					
	public void remove(CrontabEntryBean[] ceb) throws Exception {
					
    }
    
	/**
	 *	This method saves the CrontabEntryBean the actual problem with 
	 * this method is that doesn´t store comments and blank lines from 
	 * the original file any ideas?
	 *  @param CrontabEntryBean bean this method only lets store an 
	 *  entryBean each time.
	 *  @throws CrontabEntryException when it can't parse the line correctly
	 *  @throws IOException
	 *  @throws DataNotFoundException 
	 */
	public void store(CrontabEntryBean[] beans) throws CrontabEntryException, 
						ClassNotFoundException, FileNotFoundException, 
						IOException, SQLException {
				Class.forName(props.getProperty("driver"));
				Connection conn = DriverManager.getConnection(
								   props.getProperty("url"),
								   props.getProperty("username"),
								   props.getProperty("password"));

				PreparedStatement ps = conn.prepareStatement(queryStoring);
				for (int i = 0 ; i < beans.length ; i++) {
					ps.setString(1 , beans[i].getMinutes());
					ps.setString(2 , beans[i].getHours());
					ps.setString(3 , beans[i].getDaysOfMonth());
					ps.setString(4 , beans[i].getMonths());
					ps.setString(5 , beans[i].getDaysOfWeek());
					if (beans[i].getMethodName().equals("NULL")) { 
					ps.setString(6 , beans[i].getClassName());
					} else {
					String classAndMethod = beans[i].getClassName() +
											"#" + beans[i].getMethodName();
			        ps.setString(6 , classAndMethod);
					}

					String extraInfo[] = beans[i].getExtraInfo();
					String extraInfob = new String();
					for (int z = 0; z< extraInfo.length ; z++) {
						extraInfob += extraInfo[z];
					}

					ps.setString(7 , extraInfob);
					ps.executeQuery();
				}
				ps.close();
				conn.close();
	}
	
	/**
	 *  This method saves the CrontabEntryBean the actual problem with this
	 *  method is that doesn´t store comments and blank lines from the 
	 *  original file any ideas?
	 *  @param CrontabEntryBean bean this method only lets store an 
	 * entryBean each time.
	 *  @throws CrontabEntryException when it can't parse the line correctly
	 *  @throws IOException
	 *  @throws DataNotFoundException 
	 */
	public void store(CrontabEntryBean bean) throws CrontabEntryException, 
			IOException, DataNotFoundException {
	}

}
