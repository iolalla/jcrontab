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
 * This class is done to make easier to manage The Bottom line, of the app
 * @author $Author: iolalla $
 * @version $Revision: 1.1 $
 */
public class BottomController {
    
    // Initial message
    String value = "Welcome to Jcrontab Editor";
    
    // The whole panel
    JPanel panel = null;
    // The panel label to whoe info to the user
    JLabel leftLabel =null;
    
    // Sets the color to white
    Color backGroundColor = new Color(0xf9f9f9);
    
    private static BottomController instance = null;
    
    public static BottomController getInstance() {
        if ( instance == null) instance = new BottomController();
        return instance;
    }
    
    private BottomController() {}
    
    public JPanel getPanel() {
        panel = new JPanel(false);
        leftLabel = new JLabel(value);
        
        leftLabel.setHorizontalAlignment(JLabel.LEFT);
        
        panel.setLayout(new GridLayout(1, 1));
        panel.setBackground(backGroundColor);
        panel.add(leftLabel);
        return panel;
    }
    
    public void setText(String text) {
        leftLabel.setForeground(new Color(0x000000));
        leftLabel.setText(text);
    }
    
    public void setError(String text) {
        leftLabel.setForeground(new Color(0xfc0505));
        leftLabel.setText(text);
    }
}
