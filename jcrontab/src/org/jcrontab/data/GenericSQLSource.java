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

package org.jcrontab.data;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.jcrontab.Crontab;
import org.jcrontab.log.Log;

/**
 * This class is only a generic example and doesn't aim to solve all the needs
 * for the differents system's. if you want to make this class to fit your needs
 * feel free to do it and remember the license.
 * On of the things this class does is to open a connection to the database
 * , this is nasty and very expensive, y you want to integrate jcrontab with a 
 * pool like poolman or jboss it's quite easy, should substitute connection logic
 * with particular one.
 * @author $Author: iolalla $
 * @version $Revision: 1.37 $
 */
public class GenericSQLSource implements DataSource {
	
	private CrontabParser cp = new CrontabParser();

    /** This is the database driver being used. */
    private static Object dbDriver = null;
    private static GenericSQLSource instance;
    
    /** This Query gets all the Crontab entries from the
     * events table
     */    
    public static String queryAll = "SELECT id, second, minute, hour, dayofmonth, "
                                    + " month,"
                                    + " dayofweek, "
                                    + " year, task, extrainfo, businessDays "
                                    + " FROM events";
    /** This Query gets all the Crontab entries from the
     * events table but searching by the name
     */    
    public static String querySearching = "SELECT id, second, minute, hour, "
                                    + " dayofmonth, month,"
                                    + " dayofweek, "
                                    + " year, task, extrainfo, businessDays "
                                    + " FROM events" 
                                    + " WHERE task = ? ";
    /** This Query stores the Crontab entries
     */    
    public static String queryStoring = "INSERT INTO events("
                                    + " id, second, minute, hour, dayofmonth,"
                                    + " month, dayofweek, year, "
                                    + " task, extrainfo, businessDays) "
                                    + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /** This Query removes the given Crontab Entries
     */    
    public static String queryRemoving = "DELETE FROM events WHERE "
                                      + " id = ? ";

	/** This Query finds the next value in the sequence 
     */
    public static String nextSequence = "SELECT MAX(id) id FROM EVENTS " ;
    
    /** Creates new GenericSQLSource */
	
    protected GenericSQLSource() {
    }	

    /** This method grants this class to be a singleton
     * and grants data access integrity
     * @return returns the instance
     */    
    public DataSource getInstance() {
		if (instance == null) {
		    instance = new GenericSQLSource();
		}
		return instance;
    }
    
    /**
     *  This method searches the Crontab Entry that the class has the given name
     *  @param CrontabEntryBean bean this method only lets store an 
     * entryBean each time.
     *  @throws CrontabEntryException when it can't parse the line correctly
     *  @throws ClassNotFoundException cause loading the driver can throw an
     *  ClassNotFoundException
     *  @throws SQLException Yep can throw an SQLException too
     */ 
    public CrontabEntryBean find(CrontabEntryBean ceb) throws  CrontabEntryException, 
                            ClassNotFoundException, SQLException, DataNotFoundException {
	CrontabEntryBean[] cebra = findAll();
		for (int i = 0; i < cebra.length ; i++) {
			if (cebra[i].equals(ceb)) {
				return cebra[i];
			}
		}
		throw new DataNotFoundException("Unable to find :" + ceb);
    }
    
    /**
     *  This method searches all the CrontabEntries from the DataSource
     *  @return CrontabEntryBean[] the array of CrontabEntryBeans.
     *  @throws CrontabEntryException when it can't parse the line correctly
     *  @throws ClassNotFoundException cause loading the driver can throw an
     *  ClassNotFoundException
     *  @throws SQLException Yep can throw an SQLException too
     */
    public CrontabEntryBean[] findAll() throws  CrontabEntryException, 
                            ClassNotFoundException, SQLException, DataNotFoundException {
                                
        Vector list = new Vector();

		Connection conn = null;
		java.sql.Statement st = null;
		java.sql.ResultSet rs = null;
		try {
		    conn = getConnection();
		    st = conn.createStatement();
		    rs = st.executeQuery(queryAll);
		    if(rs!=null) {
			while(rs.next()) {
                boolean[] bSeconds = new boolean[60];
                boolean[] bYears = new boolean[10];
                int id = rs.getInt("id");
                String second = rs.getString("second");
			    String minute = rs.getString("minute");
			    String hour = rs.getString("hour");
			    String dayofmonth = rs.getString("dayofmonth");
			    String month = rs.getString("month");
			    String dayofweek = rs.getString("dayofweek");
                String year = rs.getString("year");
			    String task = rs.getString("task");
			    String extrainfo = rs.getString("extrainfo");
			    String line = minute + " " + hour + " " + dayofmonth 
				+ " " + month + " " 
				+ dayofweek + " " + task + " " + extrainfo;
                
                String sBusinessDays = rs.getString("businessDays");
                boolean businessDays = false ;
                if (sBusinessDays != null && 
                                    sBusinessDays.equalsIgnoreCase("true")) {
                    businessDays = true ;
                }
                
			    CrontabEntryBean ceb = cp.marshall(line);
                
                cp.parseToken(year, bYears, false);
                ceb.setId(id);
                ceb.setBYears(bYears);
                ceb.setYears(year);

                cp.parseToken(second, bSeconds, false);
                ceb.setBSeconds(bSeconds);
                ceb.setSeconds(second);
                ceb.setBusinessDays(businessDays);
                
			    list.add(ceb);
			}
			rs.close();
		    } else {
			throw new DataNotFoundException("No CrontabEntries available");
		    }
		} finally {
		    try { st.close(); } catch (Exception e) {}
		    try { conn.close(); } catch (Exception e2) {}
		}
                CrontabEntryBean[] result = new CrontabEntryBean[list.size()];
                for (int i = 0; i < list.size(); i++) {
                        result[i] = (CrontabEntryBean)list.get(i);
                }
        return result;
	}
    
    	/**
	 *  This method removes the given Crontab Entries 
	 *  @param CrontabEntryBean bean this method only lets store an 
	 * entryBean each time.
	 *  @throws CrontabEntryException when it can't parse the line correctly
         *  @throws ClassNotFoundException cause loading the driver can throw an
         *  ClassNotFoundException
         *  @throws SQLException Yep can throw an SQLException too
	 */
					
	public void remove(CrontabEntryBean[] beans) 
	    throws  CrontabEntryException, 
	    ClassNotFoundException, SQLException {

	    Connection conn = null;
	    java.sql.PreparedStatement ps = null;
	    try {
		conn = getConnection();
		ps = conn.prepareStatement(queryRemoving);
		for (int i = 0 ; i < beans.length ; i++) {
                ps.setInt(1 , beans[i].getId());
                ps.executeUpdate();
		}
	    } finally {
		try { ps.close(); } catch (Exception e) {}
		try { conn.close(); } catch (Exception e2) {}
	    }
    	}
    
    /**
	 *  This method saves the CrontabEntryBean the actual problem with this
	 *  method is that doesn't store comments and blank lines from the 
	 *  original file any ideas?
	 *  @param CrontabEntryBean bean this method only lets store an 
	 * entryBean each time.
	 *  @throws CrontabEntryException when it can't parse the line correctly
     *  @throws ClassNotFoundException cause loading the driver can throw an
     *  ClassNotFoundException
     *  @throws SQLException Yep can throw an SQLException too
	 */
	public void store(CrontabEntryBean[] beans) throws  CrontabEntryException, 
                            ClassNotFoundException, SQLException {

	    Connection conn = null;
            java.sql.PreparedStatement ps = null;
	    try {
		conn = getConnection();
		ps = conn.prepareStatement(queryStoring);
		for (int i = 0 ; i < beans.length ; i++) {
		    if (beans[i].getId() == -1) 
                    addId(beans[i], conn);
                    ps.setInt(1, beans[i].getId());
                    ps.setString(2 , beans[i].getSeconds());
                    ps.setString(3 , beans[i].getMinutes());
                    ps.setString(4 , beans[i].getHours());
                    ps.setString(5 , beans[i].getDaysOfMonth());
                    ps.setString(6 , beans[i].getMonths());
                    ps.setString(7 , beans[i].getDaysOfWeek());
                    ps.setString(8 , beans[i].getYear());
                    if ("".equals(beans[i].getMethodName())) { 
                        ps.setString(9 , beans[i].getClassName());
                    } else {
                         String classAndMethod = beans[i].getClassName() +
                         "#" + beans[i].getMethodName();
                         ps.setString(9 , classAndMethod);
                    }

                    String extraInfo[] = beans[i].getExtraInfo();
                    String extraInfob = new String();
                    if (extraInfo.length>0) {
                        for (int z = 0; z< extraInfo.length ; z++) {
                            extraInfob += " "+ extraInfo[z];
                        }
                    }
                    ps.setString(10 , extraInfob);
                    ps.setBoolean(11, beans[i].getBusinessDays());
                    ps.executeUpdate();
		}
	    } finally {
		try { ps.close(); } catch (Exception e) {}
		try { conn.close(); } catch (Exception e2) {}
	    }
	}
	
	/**
	 *  This method saves the CrontabEntryBean the actual problem with this
	 *  method is that doesn't store comments and blank lines from the 
	 *  original file any ideas?
	 *  @param CrontabEntryBean bean this method only lets store an 
	 * entryBean each time.
	 *  @throws CrontabEntryException when it can't parse the line correctly
     *  @throws ClassNotFoundException cause loading the driver can throw an
     *  ClassNotFoundException
     *  @throws SQLException Yep can throw an SQLException too
	 */
	public void store(CrontabEntryBean bean) throws  CrontabEntryException, 
                            ClassNotFoundException, SQLException {
                            CrontabEntryBean[] list = {bean};
                            store(list);
	}
    /**
     * Retrieves a connection to the database.  May use a Connection Pool 
     * DataSource or JDBC driver depending on the properties.
     *
     * @return a <code>Connection</code>
     * @exception SQLException if there is an error retrieving the Connection.
     */
    protected Connection getConnection() throws SQLException {
	Crontab crontab = Crontab.getInstance();
	String dbUser = crontab.getProperty(
			    "org.jcrontab.data.GenericSQLSource.username");
	String dbPwd = crontab.getProperty(
			    "org.jcrontab.data.GenericSQLSource.password");
	String dbUrl = crontab.getProperty(
			    "org.jcrontab.data.GenericSQLSource.url");
	if(dbDriver == null) {
	    dbDriver = loadDatabaseDriver(
		crontab.getProperty("org.jcrontab.data.GenericSQLSource.dbDataSource"));
	}
	if(dbDriver instanceof javax.sql.DataSource) {
        if (dbUser != null && dbPwd != null) {
            return ((javax.sql.DataSource)dbDriver).getConnection(dbUser, dbPwd);
        } else {
            return ((javax.sql.DataSource)dbDriver).getConnection();
	    }
	} else {
	    return DriverManager.getConnection(dbUrl, dbUser, dbPwd);
	}
    }

    /** 
     * Initializes the database engine/data source.  It first tries to load 
     * the given DataSource name.  If that fails it will load the database 
     * driver.  If the driver cannot be loaded it will check the DriverManager 
     * to see if there is a driver loaded that can server the URL.
     *
     * @param srcName is the JDBC DataSource name or null to load the driver.
     * 
     * @exception SQLExcption if there is no valid driver.
     */
    protected Object loadDatabaseDriver(String srcName) throws SQLException {
	String dbDataSource = srcName;
	Crontab crontab = Crontab.getInstance();

	if(dbDataSource == null) {
	    String dbDriver = 
	      crontab.getProperty("org.jcrontab.data.GenericSQLSource.driver");
	    Log.info("Loading dbDriver: " + dbDriver);
	    try {
		return Class.forName(dbDriver).newInstance();
	    } catch (Exception ie) {
		Log.error("Error loading " + dbDriver, ie);
		return DriverManager.getDriver( crontab.getProperty( 
								"org.jcrontab.data.GenericSQLSource.url"));
	    }
	} else {
	    try {
		javax.sql.DataSource dataSource = null;
		Log.info("Loading dataSource: " + dbDataSource);
		Context ctx = null;

		ctx = new InitialContext();
		try {
		    dataSource = 
			(javax.sql.DataSource)ctx.lookup(dbDataSource);
		} catch (NameNotFoundException nnfe) {
		    Log.info(nnfe.getExplanation());
		    Log.info("Checking Tomcat Context");
		    Context tomcatCtx = (Context)ctx.lookup("java:comp/env");
		    dataSource = 
			(javax.sql.DataSource)tomcatCtx.lookup(dbDataSource);
		}
		Log.debug("DataSource loaded. ");
		return dataSource;
	    } catch (Exception e) {
		String msg = e.getMessage();
		if(e instanceof NamingException) 
		    msg = ((NamingException)e).getExplanation();
		Log.debug(msg);
		Log.info(msg + " will try to use dbDriver...");
		return loadDatabaseDriver(null);
	    }
	}
    }
    /** 
     * This method adds the correct id to the Bean. This method is could be 
     * replaced by other methods if you need to do this as protected plz let 
     * me know
     *
     * @param CrontabEntryBean The CrontabEntryBean to add Id
     * @param Connection the conn to access to the data
     * 
     * @exception SQLExcption if smth is wrong
     */
    private void addId(CrontabEntryBean bean, Connection conn) 
                                                throws SQLException {
            java.sql.Statement st = conn.createStatement();
		    java.sql.ResultSet rs = st.executeQuery(nextSequence);
		    if(rs!=null) {
			while(rs.next()) {
                int id = rs.getInt("id");
                bean.setId(id + 1 );
            }
            }
            return;
    }
}
