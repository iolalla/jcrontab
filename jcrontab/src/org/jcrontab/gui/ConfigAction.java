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

import org.jcrontab.log.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * This class is an Action to modify the configuration of Jcrontab
 * @author $Author: iolalla $
 * @version $Revision: 1.2 $
 */

public class ConfigAction extends GenericAction {
    
    public String getActionCommand() {
        return "Config Action";
    }
    
    public void performAction(ActionEvent event) throws Exception {
        
        JButton button = (JButton)event.getSource();
        JPanel panel = (JPanel)button.getParent();
        
        JComboBox combox = (JComboBox)panel.getComponent(0);
        JTextField textField = (JTextField)panel.getComponent(1);
        String name = (String)combox.getSelectedItem();
        String value = (String)textField.getText();
        
        JcrontabGUI.getInstance().storeProperty(name, value);
        
        org.jcrontab.gui.Event modifiedEvent = new DataModifiedEvent(DataModifiedEvent.CONFIG, this);
        JcrontabGUI.getInstance().notify(modifiedEvent);
    }
}
