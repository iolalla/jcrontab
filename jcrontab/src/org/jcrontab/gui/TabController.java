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
 * @version $Revision: 1.3 $
 */

public class TabController {
    
    public JPanel getTabbedPanel() {
        JPanel panel = new JPanel();
        JTabbedPane tabbedPane = new JTabbedPane();
        //Sets the minimum size of the Panel
        Dimension minimumSize = new Dimension(780, 260);
        panel.setMinimumSize(minimumSize);
        
        
        TasksTab tasksFrame = new TasksTab();
        tabbedPane.addTab("Tasks", tasksFrame);
        tabbedPane.setSelectedIndex(0);
        
        ConfigTab configFrame = new ConfigTab();
        tabbedPane.addTab("Config", configFrame);
        
        panel.setLayout(new GridLayout(1, 1));
        panel.add(tabbedPane);
        return panel;
    }
    
    protected Component makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.add(filler);
        return panel;
    }
}
