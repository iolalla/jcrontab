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
import javax.swing.*;
import java.awt.*;
import java.util.*;
import javax.swing.table.*;
/**
 * This class is done to makeeasier to manage menus, in the future this class
 * could create the menus from an xml.
 * @author $Author: iolalla $
 * @version $Revision: 1.1 $
 */

public class ConfigTab extends JPanel {
    
    String[] allTheProperties = null;

    private int width = 0;
    
    public ConfigTab() {
        allTheProperties = Crontab.getInstance().getAllThePropertiesNames();
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
        JTable table = new JTable(new PropertiesTableModel());
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
        actionPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        //
        JComboBox comboBox = new JComboBox(allTheProperties);
        actionPanel.add(comboBox);
        //
        JTextField text = new JTextField(12);
        actionPanel.add(text);
        //
        JButton button = new JButton("Add");
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
    
    private class PropertiesTableModel extends AbstractTableModel {
        
    private Object[][] data = null;
    String[] columnNames = { "Name", "Value" };
    
    
    public PropertiesTableModel() {
    Properties propz = JcrontabGUI.getInstance().getProperties();
    int size = propz.size();
    Enumeration keys = propz.propertyNames();
    
    data = new Object[size] [2];
    int i = 0;
     while (keys.hasMoreElements()) {
         String key = (String)keys.nextElement();
         data[i][0] = key;
         data[i][1] = (String)propz.getProperty(key);
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
    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }
    public boolean isCellEditable(int row, int col) {
        return false;
    }
    }


}
