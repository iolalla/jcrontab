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
 
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import org.gjt.sp.jedit.gui.*;

import org.jcrontab.data.*;

import org.gjt.sp.jedit.*;

public class jcrontabOptionPane extends AbstractOptionPane 
{
    private JTextField properties;
    private JTextField frequency;

	private JList events;
	private DefaultListModel eventsListModel;
	
	private JButton edit;
	private JButton add;
	private JButton remove;
	
	
    public jcrontabOptionPane() {
        super("jcrontab");
	}
	
	public void init() {
        setBorder(new EmptyBorder(5,5,5,5));

        addComponent("Frequency", frequency = 
				new JTextField(jEdit.getProperty("options.jcrontab.Frequency"), 3));
        addComponent("Properties File", properties = 
				new JTextField(jEdit.getProperty("options.jcrontab.Properties"), 15));
		addSeparator("");
    }
	
    /**
     * Called when the options dialog's `OK' button is pressed.
     * This should save any properties saved in this option pane.
     */
    public void save() {            
        jEdit.setProperty("options.jcrontab.Frequency", frequency.getText());            
        jEdit.setProperty("options.jcrontab.Properties", properties.getText());            
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
}
class EventsDialog extends EnhancedDialog
{
	//{{{ ErrorMatcherDialog constructor
	public EventsDialog(Component comp, CrontabEntryBean ceb)
	{
		super(JOptionPane.getFrameForComponent(comp),
			"titulo" ,true);
		/** this.matcher = matcher;

		JPanel panel = new JPanel(new GridLayout(7,2,0,6));
		panel.setBorder(new EmptyBorder(12,12,6,12));
		JLabel label = new JLabel("name",JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(name = new JTextField(matcher.name));
		label = new JLabel(jEdit.getProperty(
			"options.console.errors.match"),JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(error = new JTextField(matcher.error));
		label = new JLabel(jEdit.getProperty(
			"options.console.errors.warning"),JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(warning = new JTextField(matcher.warning));
		label = new JLabel(jEdit.getProperty(
			"options.console.errors.extra"),JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(extra = new JTextField(matcher.extra));
		label = new JLabel(jEdit.getProperty(
			"options.console.errors.filename"),JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(filename = new JTextField(matcher.filename));
		label = new JLabel(jEdit.getProperty(
			"options.console.errors.line"),JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(line = new JTextField(matcher.line));
		label = new JLabel(jEdit.getProperty(
			"options.console.errors.message"),JLabel.RIGHT);
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel.add(label);
		panel.add(message = new JTextField(matcher.message));

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
		*/

		Dimension screen = getToolkit().getScreenSize();
		pack();
		setLocation((screen.width - getSize().width) / 2,
			(screen.height - getSize().height) / 2);
		show();
	} //}}}

	//{{{ ok() method
	public void ok()
	{
		dispose();
	} //}}}

	//{{{ cancel() method
	public void cancel()
	{
		dispose();
	} //}}}

	//{{{ isOK() method
	public boolean isOK()
	{
		return isOK;
	} //}}}

	private JTextField name;
	private JTextField error;
	private JTextField warning;
	private JTextField extra;
	private JTextField filename;
	private JTextField line;
	private JTextField message;
	private JButton ok;
	private JButton cancel;
	private boolean isOK;
	//}}}

	//{{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			if(evt.getSource() == ok)
				ok();
			else
				cancel();
		}
	} //}}}
}
