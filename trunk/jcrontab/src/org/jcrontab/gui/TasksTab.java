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

import org.jcrontab.*;
import org.jcrontab.data.*;
import org.jcrontab.log.*;
import java.io.FileOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.table.*;
/**
 * This class is done to makeeasier to manage menus, in the future this class
 * could create the menus from an xml.
 * @author $Author: iolalla $
 * @version $Revision: 1.1 $
 */

public class TasksTab extends JPanel {
    
    private int width = 600;
    
    String sourceName = null;
    
    TasksTableModel tableModel = null;
    
    Object[] data = null;

    public TasksTab() {
        setLayout(null);
        
        Component tasksContainer = getTasksContainer();

        add(tasksContainer);
    }
    
    public Component getTasksContainer() {
        JScrollPane scrollPane = null;
        try {
        
        tableModel = new TasksTableModel();
        
        JTable table = new JTable(tableModel);
        table.addMouseListener(new MouseHandler());
        Dimension dim = new Dimension(width, width/2);
        table.setPreferredScrollableViewportSize(dim);

        scrollPane = new JScrollPane(table);

        scrollPane.setSize(dim);
        
        } catch (Exception e) {
            BottomController.getInstance().setError(e.toString());
            Log.error("Error", e);
            scrollPane = new JScrollPane();
        }
        return scrollPane;
    }
    
    private class TasksTableModel extends AbstractTableModel {
        
        String[] columnNames = { "Name" };
        
        public TasksTableModel() throws Exception {
           
           data = org.jcrontab.data.CrontabEntryDAO.getInstance().findAll();
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
            fireTableCellUpdated(row, 0);
        }
        public boolean isCellEditable(int row) {
            return false;
        }
        public void remove(int row) {
            Object[] result = new Object[data.length - 1];
            int counter = 0;
            for (int i = 0; i < data.length; i++) {
                if (i != row){
                    result[counter] = data[i];
                    counter++;
                }
            }
        }
        public void update(Object obj, int row) {
            data[row] = obj;
        }
    }
    
    private class MouseHandler extends MouseAdapter {
        
        int editingRow = 0;
        
         JTable table = null;
        
        public void mouseClicked(MouseEvent e) {
            JMenuItem menuItem;
            //Create the popup menu.
            JPopupMenu popup = new JPopupMenu();
            menuItem = new JMenuItem("Add");
            menuItem.addMouseListener(new PopUpHandler());
            popup.add(menuItem);
            menuItem = new JMenuItem("Remove");
            menuItem.addMouseListener(new PopUpHandler());
            popup.add(menuItem);
            //Gets the Jtable with the TableModel
            table = (JTable)e.getSource();
            editingRow = table.getSelectedRow();
           
           if (editingRow != -1) {
                int mask = InputEvent.BUTTON1_MASK - 1;
                int mods = e.getModifiers() & mask;
                if (mods == 0) {
                  if (e.getClickCount() >= 2) {
                  CrontabEntryBean ceb = (CrontabEntryBean)table.getValueAt(editingRow, 0);
                  TaskDialog dialog = new TaskDialog(ceb, true);
                  CrontabEntryBean ceb2 = dialog.getCrontabEntryBean();
                  tableModel.update(ceb2, editingRow);
                  }
                } else {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
           }
        }
        private class PopUpHandler extends MouseAdapter {
            
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }
    
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }
    
             public void maybeShowPopup(MouseEvent e) {
               JMenuItem menuItem = (JMenuItem)e.getSource();
               String text = menuItem.getText();
               if (text.equals("Add")) {
                   CrontabEntryBean ceb = new CrontabEntryBean();
                   TaskDialog dialog = new TaskDialog(ceb, true);
                   CrontabEntryBean ceb2 = dialog.getCrontabEntryBean();
                   Object[] data2 = new Object[data.length + 1];
                   for (int i = 0; i < data.length; i++) {
                       data2[i] = data[i];
                   }
                   data2[data2.length] = ceb2;
                   //tableModel.update(ceb2, data2.length);
               } else if (text.equals("Remove")) {
                   try {
                   CrontabEntryBean ceb = (CrontabEntryBean)table.getValueAt(editingRow, 0);
                   CrontabEntryBean[] cebs = {ceb};
                   CrontabEntryDAO.getInstance().remove(cebs);
                   tableModel.remove(editingRow);
                   } catch (Exception ex) {
                        BottomController.getInstance().setError(ex.toString());
                        Log.error("Error", ex);
                   }
               }
            }
        }
    }
}
