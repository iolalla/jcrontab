/**
 *  This file is part of the jcrontab package
 *  Copyright (C) 2001-2002 Israel Olalla
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
package jcrontabplugin;
 
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import org.gjt.sp.jedit.gui.*;

import org.jcrontab.data.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

public class jcrontabEditorPane extends AbstractOptionPane  {

	private JList events;
	private DefaultListModel eventsListModel;
	
	private JButton edit;
	private JButton add;
	private JButton remove;
	
	
    public jcrontabEditorPane() {
        super("JcrontabPlugin Editor");
	}
	
	public void init() {
        addComponent(new JLabel("Editing Events"));

		addComponent(Box.createVerticalStrut(6));

		JPanel eventsPanel = new JPanel(new BorderLayout());
		eventsListModel = getEventsList();
		eventsPanel.add(BorderLayout.CENTER,new JScrollPane(
			events = new JList(eventsListModel)));
		events.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		events.addListSelectionListener(new ListHandler());
		events.addMouseListener(new MouseHandler());
		events.setVisibleRowCount(5);

		JPanel buttons = new JPanel();
		buttons.setBorder(new EmptyBorder(6,0,0,0));
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));

		buttons.add(Box.createGlue());

		buttons.add(add = new JButton("add"));
		add.addActionListener(new ActionHandler());
		buttons.add(Box.createHorizontalStrut(6));

		buttons.add(edit = new JButton("edit"));
		edit.addActionListener(new ActionHandler());
		buttons.add(Box.createHorizontalStrut(6));

		buttons.add(remove = new JButton("remove"));
		remove.addActionListener(new ActionHandler());
		buttons.add(Box.createHorizontalStrut(6));

		buttons.add(Box.createGlue());

		eventsPanel.add(BorderLayout.SOUTH,buttons);

		updateButtons();

		GridBagConstraints cons = new GridBagConstraints();
		cons.gridy = y++;
		cons.gridheight = cons.REMAINDER;
		cons.gridwidth = cons.REMAINDER;
		cons.fill = GridBagConstraints.BOTH;
		cons.weightx = cons.weighty = 1.0f;

		gridBag.setConstraints(eventsPanel,cons);
		add(eventsPanel);
    }
	
	
	private  DefaultListModel getEventsList() {
		DefaultListModel listModel = new DefaultListModel();
		CrontabEntryBean[] listEvents;
		try {
			listEvents = CrontabEntryDAO.getInstance().findAll();
					for (int i = 0; i < listEvents.length; i++) {
						listModel.addElement(listEvents[i]);
					}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listModel;
	}
    
    private void updateButtons() {
		int index = events.getSelectedIndex();
		if(index == -1) {
			edit.setEnabled(false);
			remove.setEnabled(false);
		} else {
			edit.setEnabled(true);
			CrontabEntryBean ceb = (CrontabEntryBean)events.getSelectedValue();
			remove.setEnabled(true);
		}
	}


class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			Object source = evt.getSource();
			if(source == edit) {
				CrontabEntryBean ceb = (CrontabEntryBean)events
					.getSelectedValue();
				new EventsDialog(jcrontabEditorPane.this,ceb, true);
				events.repaint();
			} else if(source == add) {
                CrontabEntryBean ceb = null;
                try {
				ceb = new CrontabEntryBean("* * * * * org.jcrontab.NativeExec your Program");
                } catch(Exception e) {
                    Log.log(Log.ERROR,this,e);
                    Object[] pp = { "add", e.toString() };
                    GUIUtilities.error(null,
                        "jcrontabplugin.error-DAO",pp);
                }
				if(new EventsDialog(jcrontabEditorPane.this,ceb,false).isOK()){
					int index = events.getSelectedIndex() + 1;

					eventsListModel.insertElementAt(ceb,index);
					events.setSelectedIndex(index);
				}
			} else if(source == remove) {
                try {
                CrontabEntryBean ceb = (CrontabEntryBean)events
					.getSelectedValue();
                CrontabEntryBean[] cebList = new CrontabEntryBean[1];
                cebList[0] = ceb;
                CrontabEntryDAO.getInstance().remove(cebList);
				eventsListModel.removeElementAt(events.getSelectedIndex());
				updateButtons();
                } catch (Exception e) {
                    Log.log(Log.ERROR,this,e);
                    Object[] pp = { "remove", e.toString() };
                    GUIUtilities.error(null,
                        "jcrontabplugin.error-DAO",pp);
                }
                
			}
		}
	}


	class ListHandler implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent evt) {
			updateButtons();
		}
	} 

    class MouseHandler extends MouseAdapter {
		public void mouseClicked(MouseEvent evt) {
			if(evt.getClickCount() == 2){
				CrontabEntryBean ceb = (CrontabEntryBean)events
					.getSelectedValue();
				new EventsDialog(jcrontabEditorPane.this,ceb, true);
				events.repaint();
			}
		}
	}
}


class EventsDialog extends EnhancedDialog {
    
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
    
    
	public EventsDialog(Component comp, CrontabEntryBean ceb, boolean update){
       
		super(JOptionPane.getFrameForComponent(comp),
			"titulo" ,true);

          this.ceb = ceb;
          this.isUpdate = update;
          
		JPanel panel = new JPanel(new GridLayout(7,2,0,6));
		panel.setBorder(new EmptyBorder(12,12,6,12));
		JLabel label = new JLabel("Minutes",JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(minute = new JTextField(ceb.getMinutes()));
		label = new JLabel("Hours", JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(hour = new JTextField(ceb.getHours()));
		label = new JLabel("Day Of Month",JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(dayOfMonth = new JTextField(ceb.getDaysOfMonth()));
		label = new JLabel("Month",JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(month = new JTextField(ceb.getMonths()));
		label = new JLabel("Days of Week",JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(dayOfWeek = new JTextField(ceb.getDaysOfWeek()));
		label = new JLabel("Class",JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(task = new JTextField(ceb.getClassName()));
		label = new JLabel("parameters",JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
        String params = new String();
        for (int i = 0; i< ceb.getExtraInfo().length ;i++) {
            params += ceb.getExtraInfo()[i] + " ";
        }
		panel.add(parameters = new JTextField(params));

		getContentPane().add(BorderLayout.CENTER,panel);
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel.setBorder(new EmptyBorder(6,12,12,12));
		panel.add(Box.createGlue());
		ok = new JButton(jEdit.getProperty("common.ok"));
		ok.addActionListener(new ActionHandler());
		getRootPane().setDefaultButton(ok);
		panel.add(ok);
		panel.add(Box.createHorizontalStrut(6));
		cancel = new JButton(jEdit.getProperty("common.cancel"));
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
        String line = new String() + " " ;
        try {
        line += minute.getText()+ " " ;
        line += hour.getText()+ " " ;
        line += dayOfMonth.getText()+ " " ;
        line += month.getText()+ " " ;
        line += dayOfWeek.getText()+ " " ;
        line += task.getText()+ " " ;
        line += parameters.getText()+ " " ;
        ceb.setLine(line);
        if (isUpdate) {
                CrontabEntryBean[] cebList = new CrontabEntryBean[1];
                cebList[0] = ceb;
                CrontabEntryDAO.getInstance().remove(cebList);
        }
            CrontabEntryDAO.getInstance().store(ceb);
        } catch (CrontabEntryException cee) {
            Log.log(Log.ERROR,this,cee);
           	Object[] pp = { line , cee.toString() };
            GUIUtilities.error(null,
                "jcrontabplugin.error-parsing",pp);
            return;
        } catch (Exception e) {
            Log.log(Log.ERROR,this,e);
           	Object[] pp = { line, e.toString() };
            GUIUtilities.error(null,
                "jcrontabplugin.error-parsing",pp);
            return;
        }
        isOK = true;
        this.ceb = ceb;
		dispose();
	}

	public void cancel(){
		dispose();
	}

	public boolean isOK() {
		return isOK;
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
