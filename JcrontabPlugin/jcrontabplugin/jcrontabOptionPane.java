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
import java.util.Vector;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;

import org.jcrontab.Crontab;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.*;

/** 
 *  This class is the plugin's OptionPane, it has the configuration and the 
 * refresh frequency as the Jlist with all the available tasks to execute.
 * @author $Author: iolalla $
 * @version $Revision: 1.5 $
 */
 
public class jcrontabOptionPane extends AbstractOptionPane {
    private JTextField properties;
    private JTextField frequency;

    public jcrontabOptionPane() {
        super(jEdit.getProperty("options.jcrontabplugin.jcrontabOptionPane.label"));
	}
	
	public void _init() {
        addComponent(jEdit.getProperty("options.jcrontabplugin.jcrontabOptionPane.label.Frequency"), frequency = 
				new JTextField(jEdit.getProperty("options.jcrontabplugin.JcrontabPlugin.Frequency"), 3));
        addComponent(jEdit.getProperty("options.jcrontabplugin.jcrontabOptionPane.label.Properties"), properties = 
				new JTextField(jEdit.getProperty("options.jcrontabplugin.JcrontabPlugin.Properties"), 15));
    }
	
    /**
     * Called when the options dialog's `OK' button is pressed.
     * This should save any properties saved in this option pane.
     */
    public void _save() {
            if (frequency.getText() != jEdit.getProperty("options.jcrontabplugin.JcrontabPlugin.Frequency")) {
        jEdit.setProperty("options.jcrontabplugin.JcrontabPlugin.Frequency", frequency.getText());
            }
            if (properties.getText() != jEdit.getProperty("options.jcrontabplugin.JcrontabPlugin.Properties")) {
        jEdit.setProperty("options.jcrontabplugin.JcrontabPlugin.Properties", properties.getText());
            }
            try {
            Crontab.getInstance().uninit(0);
            Crontab.getInstance().init(
                jEdit.getProperty("options.jcrontabplugin.JcrontabPlugin.Properties"),
                Integer.parseInt(jEdit.getProperty("options.jcrontabplugin.JcrontabPlugin.Frequency")));
            } catch (Exception e) {
               Log.log(Log.ERROR, jcrontabOptionPane.class, e.toString());
            }
    }
}
