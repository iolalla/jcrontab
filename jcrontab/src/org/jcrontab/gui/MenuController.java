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
 * @version $Revision: 1.1 $
 */

public class MenuController {
    
    public JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menuFile, menuEdit, menuHelp;
        JMenuItem menuOpen, menuConfig, menuQuit, menuAdd, menuCopy, menuPaste, menuSelectAll, menuHelpItem, menuAbout;

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
        menuFile.add(menuOpen);
        
        menuConfig = new JMenuItem("Config");
        menuFile.add(menuConfig);

        menuFile.addSeparator();

        menuQuit = new JMenuItem("Quit", KeyEvent.VK_Q);
        menuQuit.addActionListener(new QuitAction());
        menuFile.add(menuQuit);
        
        menuAdd = new JMenuItem("Add");
        menuEdit.add(menuAdd);
        
        menuCopy = new JMenuItem("Copy", KeyEvent.VK_C);
        menuEdit.add(menuCopy);
        
        menuPaste = new JMenuItem("Paste", KeyEvent.VK_P);
        menuEdit.add(menuPaste);
        
        menuEdit.addSeparator();
        
        menuSelectAll = new JMenuItem("Select All", KeyEvent.VK_A);
        menuEdit.add(menuSelectAll);
        
        
        menuHelpItem = new JMenuItem("Help");
        menuHelp.add(menuHelpItem);
        
        menuAbout = new JMenuItem("About");
        menuAbout.addActionListener(new AboutAction());
        menuHelp.add(menuAbout);
        
        return menuBar;
    }
}