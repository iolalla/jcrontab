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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException; 
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.jcrontab.Crontab;
import org.jcrontab.log.Log;

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
				if (cebra != null) {
					for (int i = 0; i < cebra.length; i++) {
						CrontabEntryBean crontabEntryBean = cebra[i];
						if (ceb != null && ceb.equals(crontabEntryBean)) {
							return crontabEntryBean;
				}
			}
		}
		throw new DataNotFoundException("Unable to find :" + ceb +"   {storedId=="+storeId+"}");
    }

	protected synchronized InputStream createCrontabStream(String name)
		throws IOException {
		File file = new File(name);
		if (!file.exists()) {
			if (file.getParentFile() != null) {
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
		}
		return new FileInputStream(file);
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
			List<CrontabEntryBean> listOfBeans = new ArrayList<>();
			String filename = Crontab.getInstance().getProperty(
					"org.jcrontab.data.file");
			if (filename == null || filename.trim().isEmpty()) {
				filename = crontab_file;
			}
	
			if (isChanged(filename)) {
				List<String> listOfLines = readAll(filename);
				if (!listOfLines.isEmpty()) {
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < listOfLines.size(); i++) {
						String lineTmp = listOfLines.get(i);
						if (lineTmp == null)
							continue;
						String trimmed = lineTmp.trim();
						// Skips blank lines 
						if (trimmed.isEmpty()) {
							continue;
						// store comments	 
						} else if (trimmed.charAt(0) == '#') {
							sb.append(lineTmp);
							sb.append("\n");
						} else {
							try {
								boolean[] bSeconds = new boolean[60];
								boolean[] bYears = new boolean[100];
								CrontabEntryBean entry = cp.marshall(trimmed);
								entry.setHeader(sb.toString());
								sb = new StringBuilder();
								entry.setId(listOfBeans.size());
								cp.parseToken("*", bYears, false);
								entry.setBYears(bYears);
								entry.setYears("*");

								cp.parseToken("0", bSeconds, false);
								entry.setBSeconds(bSeconds);
								entry.setSeconds("0");

								listOfBeans.add(entry);
							} catch (Throwable ex) {
								Log.error("Error in crontab file (" + filename + ") at line " + (i + 1) + " [" + lineTmp + "]: "
										+ ex.getMessage());
							}
						}
					}
				} else {
					if (THROW_EX_WHEN_EMPTY)
						throw new DataNotFoundException("No CrontabEntries available");
				}
	
				int sizeOfBeans = listOfBeans.size();
				if (sizeOfBeans == 0) {
					if (THROW_EX_WHEN_EMPTY) throw new DataNotFoundException("No CrontabEntries  available");
					else
						cachedBeans = new CrontabEntryBean[0];
				} else {
					CrontabEntryBean[] finalBeans = new CrontabEntryBean[sizeOfBeans];
					for (int i = 0; i < sizeOfBeans; i++) {
						finalBeans[i] = listOfBeans.get(i);
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
			return cachedBeans != null ? cachedBeans : new CrontabEntryBean[0];
	}

    private synchronized List<String> readAll(String filename) throws IOException {
        List<String> listOfLines = new ArrayList<>();
        synchronized (FileSource.class) {
            try (InputStream fis = createCrontabStream(filename);
                 BufferedReader input = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8))) {
                String strLine;
                while ((strLine = input.readLine()) != null) {
                    listOfLines.add(strLine.trim());
                }
            } catch (IOException e) {
                Log.error("Error reading crontab file " + filename + ": " + e.getMessage(), e);
            }
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
			Set<CrontabEntryBean> result = new LinkedHashSet<>(Arrays.asList(thelist)); 
			boolean isTmp = result.removeAll(Arrays.asList(ceb));
			if (isTmp)
				flushCron(result);
    	}
	}
    
	/**
	 *	This method saves the CrontabEntryBean array
	 *  @param CrontabEntryBean bean this method stores the array of beans
	 *  @throws CrontabEntryException when it can't parse the line correctly
	 *  @throws IOException If it can't access correctly to the File
	 *  @throws DataNotFoundException whe it can't find nothing in the file
	 */
    public synchronized void storeAll(CrontabEntryBean[] list)
			throws CrontabEntryException, FileNotFoundException, IOException, DataNotFoundException {

    	// read and merge
			CrontabEntryBean[] current = null;
			try {
				current = findAll();
			} catch (DataNotFoundException dnfe) {
				current = new CrontabEntryBean[0];
			}
    	 
    	Set<CrontabEntryBean> merged = new LinkedHashSet<>(); 
    	if (current != null) {
    		for (CrontabEntryBean b : current) {
        		merged.add(b);
    		}
    	}
    	if (list != null) {
    		for (CrontabEntryBean b : list) {
        		merged.add(b);
    		}
    	}  
    	
		flushCron(merged);
	
		findAll();
	}

	private void flushCron(Set<CrontabEntryBean> merged) throws IOException,
			FileNotFoundException, CrontabEntryException {
		Crontab instance2 = Crontab.getInstance();
		String fileNameTmp = instance2.getProperty(
				"org.jcrontab.data.file");
		Path targetPath = new File(fileNameTmp).toPath().toAbsolutePath();
		Path parentDir = targetPath.getParent();
		if (parentDir != null) {
			Files.createDirectories(parentDir);
		}
		Path lockPath = (parentDir != null) ? parentDir.resolve(".lock") : Path.of(".lock");
		synchronized (FileSource.class) {
			if (!Files.exists(lockPath)) {
				Path fTmp = Files.createTempFile(parentDir, "cron", "tab");
				try (PrintStream out = new PrintStream(Files.newOutputStream(fTmp), true, StandardCharsets.UTF_8)) {
					for (CrontabEntryBean bean : merged) {
						if (bean == null) continue;
						String header = bean.getHeader();
						if (header != null && !header.isEmpty()) {
							out.print(header);
							if (!header.endsWith("\n")) {
								out.println();
							}
						}
						String unmarshallTmp = cp.unmarshall(bean);
						out.println(unmarshallTmp);
					}
				}
				Files.move(fTmp, targetPath, StandardCopyOption.REPLACE_EXISTING);
				storeId++;
			} else {
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
