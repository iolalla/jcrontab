/**
 *  This file is part of the jcrontab package
 *  Copyright (C) 2001-2004 Israel Olalla
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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.jcrontab.Crontab;
import org.jcrontab.CronTask;
import org.jcrontab.log.Log;

/**
 * This class is only a generic example and doesn't aim to solve all the needs
 * for the differents system's. if you want to make this class to fit your needs
 * feel free to do it and remember the license.
 	CREATE TABLE process (
	id INTEGER NOT NULL PRIMARY KEY,
	name VARCHAR(255),
	isconcurrent VARCHAR(5) DEFAULT 'false',
	isfaulttolerant VARCHAR(5) DEFAULT 'false',
	isrunning VARCHAR(5) DEFAULT 'false',
	lastrun DATETIME
	);
	#drop table task
	CREATE TABLE task (
	id INTEGER NOT NULL PRIMARY KEY,
	task VARCHAR(255),
	parameters VARCHAR(255)
	);
	#drop table tasksinprocess;
	CREATE TABLE tasksinprocess(
	processid INTEGER NOT NULL,
	taskid INTEGER NOT NULL,
	PRIMARY KEY(processid, taskid)
	);
 * @author $Author: iolalla $
 * @version $Revision: 1.2 $
 */
public class ProcessSQLSource implements ProcessSource {
	
    /** This is the database driver being used. */
    private static Object dbDriver = null;
    private static ProcessSQLSource instance;
    
    /** This Query gets all the Process entries from the
     * events table
     */
    public static String queryAllProcess = "SELECT id, name, isconcurrent, "
    				                + " isfaulttolerant, "
                                    + " isrunning,"
                                    + " lastrunt " 
                                    + " FROM process";

    /** This Query gets all the Process entries from the
     * process table but searching by the id
     */
    public static String querySearching = "SELECT id, name, isconcurrent, "
    				                + " isfaulttolerant, "
                                    + " isrunning, "
                                    + " lastrunt " 
                                    + " FROM process "
                                    + " where id = ? ";
    /** This Query gets all the Tasks entries from the
     * process table but searching by the id
     */
    public static String queryAllTasks = "SELECT t.id, t.task, t.method, t.parameters "
                                    + " tip.precedence FROM task t, tasksinprocess tip where "
                                    + " t.id = tip.taskid and "
                                    + " tip.processid = ? order by precedence ";
    /** This Query stores the Process entries
     */
   public static String storeProcess = "INSERT into process(id, name, isconcurrent, "
    				                + " isfaulttolerant, "
                                    + " isrunning,"
                                    + " lastrunt) VALUES (?, ?, ?, ?, ?, ?)";
    /** This Query stores the Tasks entries
     */
   public static String storeTask = "INSERT into task(id, task, method, parameters) "
                                    + " VALUES (?, ?, ?, ? )";
    /** This Query stores the Relation betwen tasks and processes
     */
   public static String storeRelation = "INSERT into tasksinprocess(processid, "
                                    + " taskid) VALUES (?, ?) ";

    /** This Query removes the given Process Entries
     */
    public static String removeProcess = "DELETE FROM process WHERE "
                                      + " id = ? ";
    /** This Query removes the given Task Entries
     */
    public static String removeTask = "DELETE FROM task t, tasksinprocess tip "
                                    + " where t.id = tip.taskid and "
                                    + " tip.processid = ? ";
    /** This Query removes the given relation Entries
     */
    public static String removeRelation = "DELETE FROM tasksinprocess where"
                                    + " processid = ? ";

	/** This Query finds the next value in the sequence 
     */
    public static String nextSequence = "SELECT MAX(id) id FROM PROCESS";
    
    /** Creates new ProcessSQLSource */
	
    protected ProcessSQLSource() {
    }

    /** This method grants this class to be a singleton
     * and grants data access integrity
     * @return returns the instance
     */    
    public ProcessSource getInstance() {
		if (instance == null) {
		    instance = new ProcessSQLSource();
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
    public Process find(Process ps) throws  Exception {
	Process[] processes = findAll();
		for (int i = 0; i < processes.length ; i++) {
			if (processes[i].equals(ps)) {
				return processes[i];
			}
		}
		throw new DataNotFoundException("Unable to find :" + ps);
    }
    
    /**
     *  This method searches all the CrontabEntries from the DataSource
     *  @return CrontabEntryBean[] the array of CrontabEntryBeans.
     *  @throws CrontabEntryException when it can't parse the line correctly
     *  @throws ClassNotFoundException cause loading the driver can throw an
     *  ClassNotFoundException
     *  @throws SQLException Yep can throw an SQLException too
     */
    public Process[] findAll() throws Exception {
                                
	    	Vector list = new Vector();

		Connection conn = null;
		java.sql.Statement st = null;
		java.sql.ResultSet rs = null;
        java.sql.PreparedStatement pstmt = null;
        Process[] processList = null;
		try {
		    conn = getConnection();
		    st = conn.createStatement();
		    rs = st.executeQuery(queryAllProcess);
		    if(rs!=null) {
			while(rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                boolean isconcurrent =  rs.getBoolean("isconcurrent");
			    boolean isfaulttolerant = rs.getBoolean("isfaulttolerant");
			    boolean isrunning = rs.getBoolean("isrunning");
			    java.sql.Date lastrun = rs.getDate("lastrun");
                Process process = new Process();
                process.setId(id);
                process.setIsConcurrent(isconcurrent);
                process.setIsFaultTolerant(isfaulttolerant);
                process.setIsRunning(isrunning);
                process.setLastRun(lastrun);
			    list.add(process);
			}
			rs.close();
		    } else {
			throw new DataNotFoundException("No Processes available");
		    }
            processList = Process.toArray(list.toArray());
            Crontab crontab = Crontab.getInstance();
            for (int i = 0; i < processList.length; i++) {
                
                pstmt = conn.prepareStatement(queryAllTasks);
                pstmt.setInt(1, processList[i].getId());
                rs = pstmt.executeQuery();
                
                if(rs!=null) {
                while(rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("task");
                    String method =  rs.getString("method");
                    String parameters =  rs.getString("parameters");
                    int order = rs.getInt("precedence");
                    String[] extraInfo = parameters.split(" ");
                    CronTask task = new CronTask();
                    task.setParams(crontab, id, 
                                name, method, 
                                extraInfo);
                    task.setOrder(order);
                    processList[i].addTask(task);
                }
                rs.close();
                } else {
                throw new DataNotFoundException("No Task available for proces: " 
                                                + processList[i]);
                }
            }
		} finally {
		    try { st.close(); } catch (Exception e) {}
		    try { conn.close(); } catch (Exception e2) {}
		}
        return processList;
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
	public void remove(Process[] process) throws Exception {
	    Connection conn = null;
	    java.sql.PreparedStatement ps = null;
	    try {
            conn = getConnection();
            ps = conn.prepareStatement(removeProcess);
            for (int i = 0 ; i < process.length ; i++) {
                    ps.setInt(1 , process[i].getId());
                    ps.executeUpdate();
            }
            ps = conn.prepareStatement(removeTask);
            for (int i = 0 ; i < process.length ; i++) {
                    ps.setInt(1 , process[i].getId());
                    ps.executeUpdate();
            }
            ps = conn.prepareStatement(removeRelation);
            for (int i = 0 ; i < process.length ; i++) {
                    ps.setInt(1 , process[i].getId());
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
	public void store(Process[] process) throws Exception {
	    throw new Exception("Unsuported opeartion");
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
    private void addId(Process process, Connection conn) throws Exception {
            java.sql.Statement st = conn.createStatement();
		    java.sql.ResultSet rs = st.executeQuery(nextSequence);
		    if(rs!=null) {
			while(rs.next()) {
			int id = rs.getInt("id");
			process.setId(id + 1 );
		    }
            }
            return;
    }
}
