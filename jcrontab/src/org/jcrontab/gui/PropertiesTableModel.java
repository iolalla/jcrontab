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
import org.jcrontab.log.Log;
import javax.swing.table.*;
import java.io.*;
import java.util.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.table.*;;

/**
 * This class 
 * @author $Author: iolalla $
 * @version $Revision: 1.1 $
 */

class PropertiesTableModel extends AbstractTableModel  implements TableModelListener, Listener  {
        
        static Properties props = null;
        
        private Object[][] data = null;
        
        String[] columnNames = { "Name", "Value" };
        
        public PropertiesTableModel() {
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
            return data[row][col];
        }
    
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);   
        }
        public boolean isCellEditable(int row, int col) {
            if (col == 1) return true;
            return false;
        }
        public String getName() {
            return "PropertiesTableModel";
        }
        public Enumeration propertyNames() {
            return JcrontabGUI.getInstance().getConfig().propertyNames();
        }
        public void remove(int row) {
            fireTableRowsDeleted(row, row);
        }
        /**
         * This method does the whole thing
         *
         */
        
        public void tableChanged(TableModelEvent e) {
            if ( e.getType() == TableModelEvent.UPDATE &&
                 e.getColumn() !=  TableModelEvent.ALL_COLUMNS) {
                int row = e.getLastRow();
                int col = e.getColumn();
                
                TableModel model = (TableModel)e.getSource();

                String value = (String)model.getValueAt(row, 1);
                String name = (String)model.getValueAt(row, 0);
                // This does the real storage
                JcrontabGUI.getInstance().storeProperty(name, value);
                
                org.jcrontab.gui.Event modifiedEvent = new DataModifiedEvent(DataModifiedEvent.CONFIG, this);
                JcrontabGUI.getInstance().notify(modifiedEvent);
            } else if (e.getType() == TableModelEvent.DELETE && 
               e.getColumn() == TableModelEvent.ALL_COLUMNS &&
               e.getLastRow() == e.getFirstRow()) {
                int row = e.getLastRow();
                // The config parameter to delete
                String key = (String)getValueAt(row, 0);
                //This is the real  delete call
                JcrontabGUI.getInstance().removeProperty(key);
                // Maybe this could be done just by calling refresh(), but
                // i like to use it like that to make it more unified.
                org.jcrontab.gui.Event modifiedEvent = new DataModifiedEvent(DataModifiedEvent.CONFIG, this);
                JcrontabGUI.getInstance().notify(modifiedEvent);
               }
        
        }
        private void refresh() {
             try {
                props = JcrontabGUI.getInstance().getConfig();
                data = null;
                int size = props.size();
                Enumeration keys = props.propertyNames();
                
                data = new Object[size] [2];
                int i = 0;
                 while (keys.hasMoreElements()) {
                     String key = (String)keys.nextElement();
                     data[i][0] = key;
                     data[i][1] = (String)props.getProperty(key);
                     i++;
                 }
                 /**
                 TableModelEvent event = new TableModelEvent(
                                                this, 
                                                0, 
                                                i, 
                                                TableModelEvent.ALL_COLUMNS, 
                                                TableModelEvent.UPDATE);
                 fireTableChanged(event);
                 */
                  fireTableDataChanged();
             } catch (Exception ex) {
                BottomController.getInstance().setError(ex.toString());
                Log.error("Error", ex);
             }
        }
        public void processEvent(Event event) {
             if (event instanceof DataModifiedEvent) {
                DataModifiedEvent dmEvent = (DataModifiedEvent)event;
                String command = dmEvent.getCommand();
                Log.debug("Processing the Event for the command " + event.getCommand());
                    if ( command == DataModifiedEvent.ALL ||
                         command == DataModifiedEvent.CONFIG) {
                            refresh();
                    }
             }
        }
}
