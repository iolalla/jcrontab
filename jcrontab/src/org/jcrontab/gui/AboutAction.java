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
import java.awt.event.*;
import java.awt.*;

/**
 * This class is an Action to give a short About the app
 * @author $Author: iolalla $
 * @version $Revision: 1.1 $
 */

public class AboutAction extends GenericAction {
    
    public String getActionCommand() {
        return "About Action";
    }
    
    public void performAction(ActionEvent event) throws Exception {
       AboutWindow about = new AboutWindow(JcrontabGUI.getInstance(), "Hola mundo");
    }
    
    class AboutWindow extends JDialog implements ActionListener {
        
        public AboutWindow(JFrame frame, String title) {
            super(frame, title, true);
            if ( frame != null) {
                Dimension parentSize = frame.getSize();
                Point p = frame.getLocation();
                setLocation(p.x+parentSize.width/4, p.y+parentSize.height/4);
            }
            
            JPanel messagePane = new JPanel();
            messagePane.add(new JLabel("Hola Mundo"));
            getContentPane().add(messagePane);
            
            JPanel buttonPane = new JPanel();
            JButton button = new JButton("OK");
            buttonPane.add(button);
            button.addActionListener(this);
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            pack();
            setVisible(true);
        }
     
      public void actionPerformed(ActionEvent event) {
          setVisible(false);
          dispose();
      }
    }
}
