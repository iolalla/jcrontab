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

import org.jcrontab.data.*;
import org.jcrontab.log.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 * This class represents the Tasks Tab. The Tasks tab holds all the information
 * necesary to show, modify and delete all the Tasks from the given 
 * configuration.
 * @author $Author: iolalla $
 * @version $Revision: 1.5 $
 */

public class TasksTab extends JPanel {
    
    private int width = 600;
    
    String sourceName = null;
    
    TasksTableModel tableModel = null;

    JTable table = null;
    
    
    public TasksTab() {
        setLayout(null);
        
        Component tasksContainer = getTasksContainer();
        add(tasksContainer);
    }
    
    public Component getTasksContainer() {
        JScrollPane scrollPane = null;
        try {
        
        tableModel = new TasksTableModel();
        JcrontabGUI.getInstance().addListener(tableModel);
        
        table = new JTable(tableModel);
        
        table.addMouseListener(new MouseHandler());
        Dimension dim = new Dimension(width, width/2);
        table.setPreferredScrollableViewportSize(dim);

        scrollPane = new JScrollPane(table);

        scrollPane.setSize(dim);
        
        } catch (Exception e) {
            e.printStackTrace();
            BottomController.getInstance().setError(e.toString());
            Log.error("Error", e);
            scrollPane = new JScrollPane();
        }
        return scrollPane;
    }
    
    private class MouseHandler extends MouseAdapter {
        
        int editingRow = 0;
        
        JTable table = null;
        
        public void mouseClicked(MouseEvent e) {
            JMenuItem menuItem;
            //Create the popup menu.
            JPopupMenu popup = new JPopupMenu();
            menuItem = new JMenuItem("Add");
            menuItem.setName("add");
            menuItem.addMouseListener(new PopUpHandler());
            popup.add(menuItem);
            menuItem = new JMenuItem("Remove");
            menuItem.setName("remove");
            menuItem.addMouseListener(new PopUpHandler());
            popup.add(menuItem);
            menuItem = new JMenuItem("Copy");
            menuItem.setName("copy");
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
                  TaskDialog dialog = new TaskDialog(ceb, true, editingRow);
                  CrontabEntryBean ceb2 = dialog.getCrontabEntryBean();
                  tableModel.setValueAt(ceb2, editingRow,0);
                  }
                } else {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
           }
        }
        private class PopUpHandler extends MouseAdapter {
    
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }
    
             public void maybeShowPopup(MouseEvent e) {
               JMenuItem menuItem = (JMenuItem)e.getSource();
               String text = menuItem.getName();
               if (text.equals("add")) {
                   CrontabEntryBean ceb = new CrontabEntryBean();
                   TaskDialog dialog = new TaskDialog(ceb, false, editingRow);
               } else if (text.equals("remove")) {
                   try {
                       // removed using the DAO
                       CrontabEntryBean ceb = (CrontabEntryBean)table.getValueAt(editingRow, 0);
                       CrontabEntryBean[] cebs = {ceb};
                       CrontabEntryDAO.getInstance().remove(cebs);
                       // Sending the event
                       org.jcrontab.gui.Event event = new DataModifiedEvent(DataModifiedEvent.DATA, this);
                       JcrontabGUI.getInstance().notify(event);
                   } catch (Exception ex) {
                        BottomController.getInstance().setError(ex.toString());
                        Log.error("Error", ex);
                   }
               } else if (text.equals("copy")) {
                   CrontabEntryBean ceb = (CrontabEntryBean)table.getValueAt(editingRow, 0);
                   CrontabEntryBean cebCopy = new CrontabEntryBean(ceb);
                   
                   TaskDialog dialog = new TaskDialog(ceb, false, editingRow);
               }
            }
        }
    }
}
