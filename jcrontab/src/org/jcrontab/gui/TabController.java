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

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
/**
 * This class is done to makeeasier to manage menus, in the future this class
 * could create the menus from an xml.
 * @author $Author: iolalla $
 * @version $Revision: 1.4 $
 */

public class TabController {
    
    JPanel panel = null;
    
    JTabbedPane tabbedPane = null;
    
    {
        
       panel  = new JPanel();
        
       tabbedPane = new JTabbedPane();
    }
    
    public JPanel getPanel() {
        panel = new JPanel();
        tabbedPane = new JTabbedPane(); 
        //Sets the minimum size of the Panel
        Dimension minimumSize = new Dimension(780, 260);
        panel.setMinimumSize(minimumSize);
        // Be carefull this should be initiliazed first
        // basically cause when the system is reloaded the 
        // Configuration should be reloaded first.... 
        // It's quite tricky and nasty, and in the future
        // Should be done in other way
        ConfigTab configFrame = new ConfigTab();
        
        TasksTab tasksFrame = new TasksTab();
        //
        tabbedPane.addTab("Tasks", tasksFrame);
        tabbedPane.setSelectedIndex(0);
        tabbedPane.addTab("Config", configFrame);
        
        panel.setLayout(new GridLayout(1, 1));
        panel.add(tabbedPane);
        return panel;
    }
    
    public JTabbedPane getTAbbedPanel() {
        return tabbedPane;
    }
    
    protected Component makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.add(filler);
        return panel;
    }
}
