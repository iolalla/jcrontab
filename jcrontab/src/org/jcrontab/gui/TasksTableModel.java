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
package org.jcrontab.gui;

import org.jcrontab.data.DataSourceProxy;
import org.jcrontab.data.CrontabEntryBean;
import javax.swing.table.AbstractTableModel;
import org.jcrontab.log.Log;

/**
 * This class 
 * @author $Author: iolalla $
 * @version $Revision: 1.1 $
 */
class TasksTableModel extends AbstractTableModel implements Listener  {
    
        Object[] data = null;
        
        String[] columnNames = { "Name" };
        
        public TasksTableModel() throws Exception {
            refresh();
        }
         
        public int getColumnCount() {
            return columnNames.length;
        }
    
        public int getRowCount() {
            return data.length;
        }
    
        public String getColumnName(int col) {
            return columnNames[col];
        }
    
        public Object getValueAt(int row, int col) {
            return data[row];
        }
    
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
        public void setValueAt(Object value, int row, int col) {
            data[row] = value;
            fireTableDataChanged();
        }
        public boolean isCellEditable(int row) {
            return false;
        }
        public String getName() {
            return "AbstractTableModel";
        }
        public void add(Object obj){
            Object[] result = new Object[data.length + 1];
            for (int i = 0; i < data.length; i++) {
                    result[i] = data[i];
            }
            result[data.length] = obj;
            data = result;
            fireTableDataChanged();
        }
        
        public void refresh() throws Exception {
            try {
            data = new DataSourceProxy(
                                      JcrontabGUI.
                                      getInstance().
                                      getConfig().
                                      getProperty("org.jcrontab.data.datasource")).
                                      getDataSource().
                                      findAll();
            } catch (Exception ex) {
                if (ex instanceof org.jcrontab.data.DataNotFoundException ) {
                    CrontabEntryBean bean = new CrontabEntryBean();
                    data = new CrontabEntryBean[0];
                } else {
                    throw ex;
                }
            }
            fireTableDataChanged();
        }
        
       public void processEvent(Event event) {
        if (event instanceof DataModifiedEvent) {
            DataModifiedEvent dmEvent = (DataModifiedEvent)event;
            String command = dmEvent.getCommand();
                if ( command == DataModifiedEvent.ALL || 
                     command == DataModifiedEvent.DATA) {
                    try {
                        refresh();
                    } catch (Exception e) {
                        BottomController.getInstance().setError(e.toString());
                        e.printStackTrace();
                        Log.error("Error", e);
                    }
                }
            }
       }
}
