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
 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import org.gjt.sp.jedit.*;

public class jcrontabOptionPane extends AbstractOptionPane 
{
    private JTextField properties;
    private JTextField frequency;

    public jcrontabOptionPane() 
	{
        super("jcrontab");
        setBorder(new EmptyBorder(5,5,5,5));

        addComponent("Frequency", frequency = 
				new JTextField(jEdit.getProperty("options.jcrontab.Frequency"), 3));
        addComponent("Properties File", properties = 
				new JTextField(jEdit.getProperty("options.jcrontab.Properties"), 15));
				
    }
	
    /**
     * Called when the options dialog's `OK' button is pressed.
     * This should save any properties saved in this option pane.
     */
    public void save() 
	{            
        jEdit.setProperty("options.jcrontab.Frequency", frequency.getText());            
        jEdit.setProperty("options.jcrontab.Properties", properties.getText());            
    }
	
}
