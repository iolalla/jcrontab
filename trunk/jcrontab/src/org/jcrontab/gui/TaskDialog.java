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
 
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import org.jcrontab.data.*;
import org.jcrontab.*;
import org.jcrontab.log.*;

/**
 *	This class is the Dialog to write or change a CrontabEntryBean.
 *  Sends a Message to update the JTable in the EditorPane
 */

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
	private boolean isOK;
    private boolean isUpdate = false;
    private CrontabEntryBean ceb;
    private int id;
    
    /**
	 *	Default constructor of the TaskDialog class
	 * @param CrontabEntryBean
	 * @param boolean to know if its update or not
	 */
	public TaskDialog(CrontabEntryBean ceb2, boolean update){
       
            
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
                CrontabEntryDAO.getInstance().remove(cebList);
        }
                CrontabEntryDAO.getInstance().store(ceb3);
		this.ceb = ceb3;
        } catch (Exception ex) {
            BottomController.getInstance().setError(ex.toString());
            Log.error("Error", ex);
        }
        isOK = true;
        dispose();
	}
    
    public void cancel() {
        dispose();
    }

	public boolean isOK() {
		return isOK;
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
