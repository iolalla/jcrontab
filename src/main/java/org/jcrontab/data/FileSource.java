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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException; 
import java.io.PrintStream; 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import org.jcrontab.Crontab;

/**
 * This class Is the implementation of DataSource to access 
 * Info in a FileSystem
 * @author $Author: iolalla $
 * @version $Revision: 1.43 $
 */
public class FileSource implements DataSource {
	
	private static final boolean THROW_EX_WHEN_EMPTY = false;

	private CrontabParser cp = new CrontabParser();

	private static FileSource instance = new FileSource();
    
	private CrontabEntryBean[] cachedBeans = null;
	
	protected long lastModified;
	
	private String crontab_file = "crontab";
    
    /** 
	* Creates new FileSource  
	*/
	
    protected FileSource() {
			if (Crontab.getInstance().getProperty(
								"org.jcrontab.data.file") == null) 
				Crontab.getInstance().setProperty(
								"org.jcrontab.data.file", crontab_file);
    }	
    /**
	 *	This method returns the singleton is very important to grant
	 *  That only a Thread accesses at a time
	 */
    public synchronized DataSource getInstance() {
		return instance;
    }
	
    /**
	 *	This method searches the given Bean  from the File
	 *  @return CrontabEntryBean beans Array the result of the search
	 *  @param CrontabEntryBean the CrontabEntryBean you want to search
	 *  @throws CrontabEntryException when it can't parse the line correctly
	 *  @throws IOException If it can't access correctly to the File
	 *  @throws DataNotFoundException whe it can't find nothing in the file 
	 */
    public synchronized CrontabEntryBean find(final CrontabEntryBean ceb) 
    	throws CrontabEntryException, IOException, DataNotFoundException {
        final CrontabEntryBean[] cebra = findAll();
        System.out.println("SEARCH----- #"+storeId+"#   ----:"+ceb);
		for (int i = 0; i < cebra.length ; i++) {
			CrontabEntryBean crontabEntryBean = cebra[i];
			if (ceb!=null && ceb.equals(crontabEntryBean)) {
//System.out.println("cebra encontrada : " + cebra[i]);
				return crontabEntryBean;
			}else{
				System.out.println("skiped:" +crontabEntryBean);
			}
		}
		throw new DataNotFoundException("Unable to find :" + ceb +"   {storedId=="+storeId+"}");
    }

	protected synchronized InputStream createCrontabStream(String name)
		throws IOException {
		return new FileInputStream(name);
	}

	protected synchronized boolean isChanged(String name) {
            // Don't like those three lines. But are the only way i have to grant
            // It works in any O.S.
		final File filez = new File(name);
		synchronized (FileSource.class) {
			if (lastModified != filez.lastModified()) {
					// This line is added to avoid reading the file if it didn't 
					// change
				lastModified = filez.lastModified();
				return true;
			}
		}
		return false;
	}
   /**
	 *	This method searches all the CrontabEntryBean from the File
	 *  @return CrontabEntryBean beans Array the result of the search
	 *  @throws CrontabEntryException when it can't parse the line correctly
	 *  @throws IOException If it can't access correctly to the File
	 *  @throws DataNotFoundException whe it can't find nothing in the file 
	 */
    public synchronized CrontabEntryBean[] findAll()
			throws CrontabEntryException, IOException, DataNotFoundException {
    	synchronized (FileSource.class) {
			boolean[] bSeconds = new boolean[60];
			boolean[] bYears = new boolean[10];
	
			
			Vector listOfBeans = new Vector();
			// Class cla = FileSource.class;
			// BufferedReader input = new BufferedReader(new FileReader(strFileName));
			// This Line allows the crontab to be included in a jar file
			// and accessed from anywhere
			String filename = Crontab.getInstance().getProperty(
					"org.jcrontab.data.file");
	
			if (isChanged(filename)) {
				
				Vector listOfLines = readAll(filename);
				if (listOfLines.size() > 0) {
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < listOfLines.size(); i++) {
						String lineTmp = (String) listOfLines.get(i);
						// Skips blank lines 
						if (lineTmp.equals("") || lineTmp  == "\n") {
						// store comments	 
						} else if ( lineTmp.trim().charAt(0) == '#') {
							sb.append(lineTmp);
							sb.append("\n");
						} else {
							//System.out.println(strLines); 
							CrontabEntryBean entry = cp.marshall(lineTmp);
							entry.setHeader(sb);
							sb =  new StringBuffer();
							entry.setId(listOfBeans.size());
							cp.parseToken("*", bYears, false);
							entry.setBYears(bYears);
							entry.setYears("*");
	
							cp.parseToken("0", bSeconds, false);
							entry.setBSeconds(bSeconds);
							entry.setSeconds("0");
	
							listOfBeans.add(entry);
						}
					}
				} else {
					if (THROW_EX_WHEN_EMPTY)throw new DataNotFoundException("No CrontabEntries available");
				}
	
				int sizeOfBeans = listOfBeans.size();
				if (sizeOfBeans == 0) {
					if (THROW_EX_WHEN_EMPTY) throw new DataNotFoundException("No CrontabEntries  available");
				} else {
					CrontabEntryBean[] finalBeans = new CrontabEntryBean[sizeOfBeans];
					for (int i = 0; i < sizeOfBeans; i++) {
						//Added to have different Beans identified
						finalBeans[i] = (CrontabEntryBean) listOfBeans.get(i);
						finalBeans[i].setId(i);
					}
					cachedBeans = finalBeans;
				}
			}
			if (cachedBeans != null) {
				return cachedBeans;
			} else {
				if (THROW_EX_WHEN_EMPTY) throw new DataNotFoundException("No  CrontabEntries  available");
			}
    	}
		return cachedBeans;
	}
/**
 * @author vipup
 * @param filename
 * @return
 * @throws IOException
 */
private synchronized Vector readAll(String filename) throws IOException {
	Vector listOfLines = new Vector();
	synchronized (FileSource.class) {
		// open the file
		final InputStream fis = createCrontabStream(filename);
		BufferedReader input = new BufferedReader(
				new InputStreamReader(fis));
	
		String strLine;
	
		while ((strLine = input.readLine()) != null) {
			//System.out.println(strLine);
			strLine = strLine.trim();
			listOfLines.add(strLine);
		}
		input.close();
		fis.close();
	}
	return listOfLines;
}
		
    /**
	 *	This method removes the CrontabEntryBean array from the File
	 *  @param CrontabEntryBean bean teh array of beans to remove
	 *  @throws Exception 
	 */
	 
    public synchronized void remove(CrontabEntryBean[] ceb) throws Exception {
    	synchronized (FileSource.class) {
			CrontabEntryBean[] thelist = findAll();
			Set<CrontabEntryBean> result = new HashSet<CrontabEntryBean>(); 
			result.addAll(Arrays.asList( thelist ));
			boolean isTmp = result.removeAll( Arrays.asList( ceb ));//result.contains(ceb[0])
			if (isTmp)
				flushCron(result);
    	}
	}
    
	/**
	 *	This method saves the CrontabEntryBean array the actual problem with this
	 *  method is that doesn�t store comments and blank lines from the original
	 *  file any ideas?
	 *  @param CrontabEntryBean bean this method stores the array of beans
	 *  @throws CrontabEntryException when it can't parse the line correctly
	 *  @throws IOException If it can't access correctly to the File
	 * @throws DataNotFoundException 
	 *  @throws DataNotFoundException whe it can't find nothing in the file usually 
	 *  Exception should'nt this 
	 */
    public synchronized void storeAll(CrontabEntryBean[] list)
			throws CrontabEntryException, FileNotFoundException, IOException, DataNotFoundException {

    	// read and merge
    	CrontabEntryBean[] current =  findAll();
    	 
    	Set<CrontabEntryBean> merged = new  HashSet<CrontabEntryBean>(); 
    	if (current!=null)
    		for (CrontabEntryBean b:current)	{
        		if (!merged.contains(b))
        			merged.add (b);
    		}
    	if (list!=null)
    		for (CrontabEntryBean b:list)	{
        		if (!merged.contains(b))
    			merged.add (b);
    	}  
    	
		flushCron(merged);
	
		findAll();
	}
	/**
	 * @author vipup
	 * @param merged
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws CrontabEntryException
	 */
	private void flushCron(Set<CrontabEntryBean> merged) throws IOException,
			FileNotFoundException, CrontabEntryException {
		Crontab instance2 = Crontab.getInstance();
		String fileNameTmp = instance2.getProperty(
				"org.jcrontab.data.file");
		File fl = new File(fileNameTmp);
		File lockTmp = new File(fl.getParentFile(),".lock");		
		synchronized (FileSource.class) {
			if (!lockTmp.exists()){
				System.out.println("--------"+storeId+"--------------------");
				File fTmp = File.createTempFile("cron", "tab", fl.getParentFile());
				FileOutputStream fileOutputStream = new FileOutputStream(fTmp );
				PrintStream out = new PrintStream(fileOutputStream,true);
				for (CrontabEntryBean bean: merged ) { 
						if (bean==null)continue;
						out.println(bean.getHeader());
						String unmarshallTmp = cp.unmarshall(bean);
						out.println(unmarshallTmp);
						//System.out.println(unmarshallTmp);
				}
				out.flush();
				out.close();
				fileOutputStream.flush();
				fileOutputStream.close();
				System.out.println("--------"+storeId+"--------------------");

				// old -> lock 
				// new -> old
				// lock -X
				File nameTMp = fl;
				fl.renameTo(lockTmp);
				fTmp.renameTo( nameTMp  );
				lockTmp.delete();
				storeId++;
			}else{
				throw new CrontabEntryException("CrontabEntries locked.");
			}
		}
	}
    
    static int storeId = 0;
    
	/**
	 * This method saves the CrontabEntryBean array the actual problem with this
	 * method is that doesn�t store comments and blank lines from the original
	 * file any ideas?
	 * 
	 * @param CrontabEntryBean
	 *            bean this method stores the array of beans
	 * @throws CrontabEntryException
	 *             when it can't parse the line correctly
	 * @throws IOException
	 *             If it can't access correctly to the File
	 * @throws DataNotFoundException
	 *             whe it can't find nothing in the file usually Exception
	 *             should'nt this
	 */
	public synchronized void store(CrontabEntryBean[] beans) throws CrontabEntryException, 
			IOException, DataNotFoundException {
		storeAll(beans);
	}
	
	/**
	 *	This method saves the CrontabEntryBean the actual problem with this
	 *  method is that doesn�t store comments and blank lines from the original
	 *  file any ideas?
	 *  @param CrontabEntryBean bean this method only lets store an entryBean
	 *  each time.
	 *  @throws CrontabEntryException when it can't parse the line correctly
	 *  @throws IOException If it can't access correctly to the File
	 *  @throws DataNotFoundException whe it can't find nothing in the file usually 
	 *  Exception should'nt this 
	 */
	public synchronized void store(CrontabEntryBean bean) throws CrontabEntryException, 
			IOException, DataNotFoundException {
		synchronized (FileSource.class) {
			store(new CrontabEntryBean[]{bean});
		}
	}
}
