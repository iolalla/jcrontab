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
import javax.swing.border.*;
/**
 * This class is done to makeeasier to manage menus, in the future this class
 * could create the menus from an xml.
 * @author $Author: iolalla $
 * @version $Revision: 1.4 $
 */

public class TasksTab extends JPanel implements Listener {
    
    private int width = 600;
    
    String sourceName = null;
    
    TasksTableModel tableModel = null;
    
    Object[] data = null;

    JTable table = null;
    
    
    public TasksTab() {
        JcrontabGUI.getInstance().addListener(this);
        setLayout(null);
        
        Component tasksContainer = getTasksContainer();

        add(tasksContainer);
        }
    
    public Component getTasksContainer() {
        JScrollPane scrollPane = null;
        try {
        
        tableModel = new TasksTableModel();
        
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
    
    public void processEvent(Event event) {
        if (event instanceof DataModifiedEvent) {
            DataModifiedEvent dmEvent = (DataModifiedEvent)event;
            String command = dmEvent.getCommand();
                if ( command == DataModifiedEvent.ALL || 
                     command == DataModifiedEvent.DATA) {
                    try {
                        tableModel.refresh();
                    } catch (Exception e) {
                        BottomController.getInstance().setError(e.toString());
                        e.printStackTrace();
                        Log.error("Error", e);
                    }
                }
        }
    }
    
    private class TasksTableModel extends AbstractTableModel {
        
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
        public void add(Object obj){
            Object[] result = new Object[data.length + 1];
            for (int i = 0; i < data.length; i++) {
                    result[i] = data[i];
            }
            result[data.length] = obj;
            data = result;
            System.out.println(data.length);
            fireTableDataChanged();
        }
        
        public void refresh() throws Exception {
            data = new DataSourceProxy(
                                      JcrontabGUI.
                                      getInstance().
                                      getConfig().
                                      getProperty("org.jcrontab.data.datasource")).
                                      getDataSource().
                                      findAll();
                                                  
            fireTableDataChanged();
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
                   TaskDialog dialog = new TaskDialog(ceb, false, editingRow);
                   CrontabEntryBean ceb2 = dialog.getCrontabEntryBean();
                   Object[] data2 = new Object[data.length + 1];
                   for (int i = 0; i < data.length; i++) {
                       data2[i] = data[i];
                   }
                   data2[data.length] = ceb2;
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
    
class TaskDialog extends JDialog {
    
	private JTextField minute;
	private JTextField hour;
	private JTextField dayOfMonth;
	private JTextField month;
	private JTextField dayOfWeek;
	private JTextField task;
	private JTextField parameters;
	private JButton ok;
	private JButton cancel;
    private boolean isUpdate = false;
    private CrontabEntryBean ceb;
    private int id;
    private int position;
    
    /**
	 *	Default constructor of the TaskDialog class
	 * @param CrontabEntryBean
	 * @param boolean to know if its update or not
	 */
	public TaskDialog(CrontabEntryBean ceb2, boolean update, int position){
       
            
        if (update) this.id = ceb2.getId();
        
        this.ceb = ceb2;
        this.isUpdate = update;
          
		JPanel panel = new JPanel(new GridLayout(7,2,0,6));
		panel.setBorder(new EmptyBorder(12,12,6,12));
		JLabel label = new JLabel("minutes",JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(minute = new JTextField(ceb.getMinutes()));
		label = new JLabel("hours", JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(hour = new JTextField(ceb.getHours()));
		label = new JLabel("daysOfMonth",JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(dayOfMonth = new JTextField(ceb.getDaysOfMonth()));
		label = new JLabel("month",JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(month = new JTextField(ceb.getMonths()));
		label = new JLabel("daysOfWeek",JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(dayOfWeek = new JTextField(ceb.getDaysOfWeek()));
		label = new JLabel("className",JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(task = new JTextField(ceb.getClassName()));
		label = new JLabel("parameters",JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
        String params = new String();
		if ( ceb2.getExtraInfo() != null) {
			for (int i = 0; i< ceb2.getExtraInfo().length ;i++) {
				params += ceb2.getExtraInfo()[i] + " ";
			}
		}
		panel.add(parameters = new JTextField(params));

		getContentPane().add(BorderLayout.CENTER,panel);
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel.setBorder(new EmptyBorder(6,12,12,12));
		panel.add(Box.createGlue());
		ok = new JButton("Ok");
		ok.addActionListener(new ActionHandler());
		getRootPane().setDefaultButton(ok);
		panel.add(ok);
		panel.add(Box.createHorizontalStrut(6));
		cancel = new JButton("cancel");
		cancel.addActionListener(new ActionHandler());
		panel.add(cancel);
		panel.add(Box.createGlue());
		getContentPane().add(BorderLayout.SOUTH,panel);
        
		Dimension screen = getToolkit().getScreenSize();
		pack();
		setLocation((screen.width - getSize().width) / 2,
			(screen.height - getSize().height) / 2);
		show();
	}


	public void ok() {
        String line = new String();
        try {
        line += minute.getText()+ " " ;
        line += hour.getText()+ " " ;
        line += dayOfMonth.getText()+ " " ;
        line += month.getText()+ " " ;
        line += dayOfWeek.getText()+ " " ;
        line += task.getText()+ " " ;
        line += parameters.getText();
        CrontabParser parser = new CrontabParser();
        CrontabEntryBean ceb3 = parser.marshall(line);
        if (isUpdate) {
                CrontabEntryBean[] cebList = new CrontabEntryBean[1];
                ceb.setId(this.id);
                cebList[0] = ceb;
                tableModel.remove(position);
                CrontabEntryDAO.getInstance().remove(cebList);
                tableModel.setValueAt(ceb3, position, 0);
        }
                CrontabEntryDAO.getInstance().store(ceb3);
                this.ceb = ceb3;
                if (!isUpdate) tableModel.add(ceb3);
        } catch (Exception ex) {
            BottomController.getInstance().setError(ex.toString());
            Log.error("Error", ex);
        }
        dispose();
	}
    
    public void cancel() {
        dispose();
    }

    public CrontabEntryBean getCrontabEntryBean() {
        return ceb;
    }


	class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			if(evt.getSource() == ok)
				ok();
			else
				cancel();
		}
	}
}
}
