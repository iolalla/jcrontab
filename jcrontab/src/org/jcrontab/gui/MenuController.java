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
 * This class is done to make easier to manage menus, in the future this class
 * could create the menus from an xml.
 * @author $Author: iolalla $
 * @version $Revision: 1.6 $
 */

public class MenuController {
    /**
     * This method creates the basic menubar
     * @return JMenuBar returns the basic menubar
     */
    
    public JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menuFile, menuEdit, menuHelp;
        JMenuItem menuOpen, menuQuit, menuAdd, menuDelete, menuHelpItem, menuAbout;

        //Create the menu bar.
        menuBar = new JMenuBar();

        //Build the menus
        menuFile = new JMenu("File");                  
        menuFile.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menuFile);
        
        menuEdit = new JMenu("Edit");
        menuEdit.setMnemonic(KeyEvent.VK_E);
        menuBar.add(menuEdit);

        menuHelp = new JMenu("Help");
        menuHelp.setMnemonic(KeyEvent.VK_H);
        menuBar.add(menuHelp);

        menuOpen = new JMenuItem("Open", KeyEvent.VK_O);
        menuOpen.addActionListener(new OpenAction());
        menuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menuFile.add(menuOpen);
        
        menuFile.addSeparator();

        menuQuit = new JMenuItem("Quit", KeyEvent.VK_Q);
        menuQuit.addActionListener(new QuitAction());
        menuQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        menuFile.add(menuQuit);
        
        menuAdd = new JMenuItem("Add");
        menuAdd.addActionListener(new AddAction());
        menuAdd.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        menuEdit.add(menuAdd);
        
        menuHelpItem = new JMenuItem("Help");
        menuHelpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
        menuHelp.add(menuHelpItem);
        
        menuAbout = new JMenuItem("About");
        menuAbout.addActionListener(new AboutAction());
        menuAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.CTRL_MASK));
        menuHelp.add(menuAbout);
        
        return menuBar;
    }
}
