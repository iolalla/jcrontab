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
 * @version $Revision: 1.2 $
 */

public class ConfigTab extends JPanel {
    
    String[] allTheProperties = null;

    String[] usedProperties = null;
    private int width = 0;
    
    JTextField text = null;
    
    JButton button = null;
    
    static Properties props = null;
    
    public ConfigTab() {
        allTheProperties  = Crontab.getInstance().getAllThePropertiesNames();
        props = JcrontabGUI.getInstance().getConfig();
        
        setLayout(null);
        
        Component actionableContainer = getActionableContainer();
        Component propertiesContainer = getPropertiesContainer();

        add(actionableContainer);
        add(propertiesContainer);
        
        Insets insets = this.getInsets();
        
        Dimension actionableSize = actionableContainer.getSize();
        Dimension propertiesSize = propertiesContainer.getSize();
        
        actionableContainer.setBounds(5 + insets.left, 5 + insets.top,
                     actionableSize.width + 5, actionableSize.height + 5);
                     
        propertiesContainer.setBounds(5 + insets.left, 5 + insets.top + actionableSize.height + 5 , propertiesSize.width + 5, propertiesSize.height + 5);
    }
    
    public Component getPropertiesContainer() {
        // This point is a little bit tricky cause  i am using for two things
        // the same class. Maybe should write a new class but i like it like
        // that
        TableModelListener tableModel = new PropertiesTableModel();
        
        JTable table = new JTable(new PropertiesTableModel());
        table.getModel().addTableModelListener(tableModel);
        //table.addMouseListener(new MouseHandler());
        Dimension dim = new Dimension(width, width/2);
        table.setPreferredScrollableViewportSize(dim);

        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setSize(dim);
        return scrollPane;
    }
    
    public Component getActionableContainer() {
        //
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(null);
        //
        Enumeration keys = props.propertyNames();
        usedProperties = new String[props.size()];
            int i = 0;
             while (keys.hasMoreElements()) {
                 usedProperties[i] = (String)keys.nextElement();;
                 i++;
             }
        JComboBox comboBox = new JComboBox(getTheRightProperties(
                                            allTheProperties , usedProperties));
        actionPanel.add(comboBox);
        //
        text = new JTextField(32);
        actionPanel.add(text);
        //
        button = new JButton("Add");
        button.addActionListener(new AddPropertyAction());
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
        return actionPanel;
    }
    
    private String[] getTheRightProperties(String[] all, String[] toExclude) {
        String[] result = new String[all.length - toExclude.length + 1];
        int resultIndex = 0;
        boolean token = false;
        for (int i = 0; i < all.length; i++) {
            for (int z = 0; z < toExclude.length; z++) {
                if (all[i].equals(toExclude[z])) {
                    token = false;
                    break;
                } else {
                    token = true;
                }
            }
            if (token) {
                result[resultIndex] = all[i];
                resultIndex++;
            }
        }
        return result;
    }
    
    private class PropertiesTableModel extends AbstractTableModel  implements TableModelListener {
        
        private Object[][] data = null;
        String[] columnNames = { "Name", "Value" };
        
        
        public PropertiesTableModel() {
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
        public void tableChanged(TableModelEvent e) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            TableModel model = (TableModel)e.getSource();
            String columnName = model.getColumnName(column);
            String value = (String)model.getValueAt(row, column);
            String name = (String)model.getValueAt(row, 0);
            props.setProperty(name, value);
            try {
                FileOutputStream file = new FileOutputStream((String)props.getProperty("org.jcrontab.config"));
            props.store(file, "#");
            } catch (Exception ex) {
                BottomController.getInstance().setError(ex.toString());
                Log.error("Error", ex);
            }
        }
    }
}
