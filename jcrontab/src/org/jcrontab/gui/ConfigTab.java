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
 * @version $Revision: 1.6 $
 */

public class ConfigTab extends JPanel implements Listener {
    
    private int width = 0;
    
    private JTextField text = null;
    
    private JButton button = null;
    
    private PropertiesTableModel tableModel;
    
    private JScrollPane scrollPane = null;
    
    private JTable table = null;
    
    private Object[] data = null;
    
    private DefaultComboBoxModel combomodel = null;
    
    private JComboBox comboBox = null;
    
    public ConfigTab() {
        
        setLayout(null);
        
        Component propertiesContainer = getPropertiesContainer();
        Component actionableContainer = getActionableContainer();
         
        add(actionableContainer);
        add(propertiesContainer);
        
        JcrontabGUI.getInstance().addListener(this);
        
        Insets insets = this.getInsets();
        
        Dimension actionableSize = actionableContainer.getSize();
        Dimension propertiesSize = propertiesContainer.getSize();
        
        actionableContainer.setBounds(5 + insets.left, 5 + insets.top,
                     actionableSize.width + 5, actionableSize.height + 5);
                     
        propertiesContainer.setBounds(5 + insets.left, 5 + insets.top + actionableSize.height + 5 , propertiesSize.width + 5, propertiesSize.height + 5);
    }
    
    public Component getPropertiesContainer() {
        // This is the tableModel to manage the entries in the table
        tableModel = new PropertiesTableModel();
        table = new JTable(tableModel);
        // This is the listener of the mouse actions
        table.addMouseListener(new MouseHandler());
        // This is the listener of the changes in the data of the 
        // TableModel
        table.getModel().addTableModelListener(tableModel);
        // This line adds this TableModel as a valid Listener
        JcrontabGUI.getInstance().addListener(tableModel); 
        
        scrollPane = new JScrollPane(table);

        return scrollPane;
    }
    
    public Component getActionableContainer() {
        //
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(null);
        
       
        combomodel = new DefaultComboBoxModel();
        refresh();
        
        comboBox = new JComboBox(combomodel);
        
        actionPanel.add(comboBox);
        //
        text = new JTextField(32);
        actionPanel.add(text);
        //
        button = new JButton("Add");
        button.addActionListener(new ConfigAction());
        actionPanel.add(button);
        
        Insets insets = actionPanel.getInsets();
        Dimension comboBoxSize = comboBox.getPreferredSize();
        Dimension textSize = text.getPreferredSize();
        Dimension buttonSize = button.getPreferredSize();
        
        comboBox.setBounds(5 + insets.left, 5 + insets.top,
                     comboBoxSize.width + 5, comboBoxSize.height);
        text.setBounds(5 + insets.left + comboBoxSize.width +5 , 5 + insets.top,
                     textSize.width + 5, textSize.height + 5 );
        button.setBounds(5 + insets.left + comboBoxSize.width + 5+ textSize.width + 5, 5 + insets.top,
                     buttonSize.width + 10, buttonSize.height);
        width = 5 + insets.left + comboBoxSize.width + 5+ textSize.width + 5 +  buttonSize.width + 10;
        Dimension dim = new Dimension( width , textSize.height + 10) ;
        
        actionPanel.setSize(dim);
        Dimension dim2 = new Dimension(width, width/2);
        table.setPreferredScrollableViewportSize(dim2);
        scrollPane.setSize(dim2);
        return actionPanel;
    }

     private void refresh() {

        String[] allTheProperties  =                     Crontab.getInstance().getAllThePropertiesNames();

        String[] usedProperties = new String[tableModel.getRowCount()];
    
        Enumeration keys = tableModel.propertyNames();

            int y = 0;
             while (keys.hasMoreElements()) {
                 usedProperties[y] = (String)keys.nextElement();;
                 y++;
             }
        
        String[] result = new String[allTheProperties.length - usedProperties.length + 1];
        int resultIndex = 0;
        boolean token = false;
        for (int i = 0; i < allTheProperties.length; i++) {
            for (int z = 0; z < usedProperties.length; z++) {
                if (allTheProperties[i].equals(usedProperties[z])) {
                    token = false;
                    break;
                } else {
                    token = true;
                }
            }
            if (token) {
                result[resultIndex] = allTheProperties[i];
                resultIndex++;
            }
        }
        data = result;
        combomodel.removeAllElements();
        for (int j = 0; j < data.length; j++) combomodel.addElement(data[j]);
    }
    
    public void processEvent(Event event) {
        if (event instanceof DataModifiedEvent) {
        DataModifiedEvent dmEvent = (DataModifiedEvent)event;
        String command = dmEvent.getCommand();
            if ( command == DataModifiedEvent.CONFIG) {
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
    
    
    private class MouseHandler extends MouseAdapter {
        
        int editingRow = 0;
        
         JTable table = null;
        
        public void mouseClicked(MouseEvent e) {
            JMenuItem menuItem;
            //Create the popup menu.
            JPopupMenu popup = new JPopupMenu();
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
                      //
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

             public synchronized void maybeShowPopup(MouseEvent e) {
               JMenuItem menuItem = (JMenuItem)e.getSource();
               String text = menuItem.getText();
                if (text.equals("Remove")) {
                   try {
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
