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

import java.util.Properties;
import java.io.FileInputStream;
import org.jcrontab.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/** 
 * This class starts the swing gui.
 * @author $Author: iolalla $
 * @version $Revision: 1.2 $
 */
public class JcrontabGUI extends JFrame {
    
    private static JcrontabGUI instance = null;
    
    private static Properties props = null;
    
    private JcrontabGUI() {
        super("Jcrontab Editor");
    }
    
    public static JcrontabGUI getInstance() {
        if (instance == null) instance = new JcrontabGUI();
        return instance;
    }
    
    public Properties getProperties() {
        return props;
    }
    /**
     *
     *
     */
    public JMenuBar createMenuBar() {
        MenuController menucontroller = new MenuController();
        return menucontroller.createMenuBar();
    }
    
    public JPanel createTabbedPanel() {
        TabController tabController = new TabController();
        return tabController.getTabbedPanel();
    }
    
    public JPanel createBottomPanel() {
        BottomController botController = BottomController.getInstance();
        return botController.getPanel();
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        //
        instance.setUndecorated(true);
        instance.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        instance.setJMenuBar(createMenuBar());
        instance.setContentPane(new JPanel(new BorderLayout()));
        instance.getContentPane().add(createTabbedPanel(), 
                                   BorderLayout.CENTER);
        instance.getContentPane().add(createBottomPanel(), 
                                   BorderLayout.SOUTH);
        
        //Display the window.
        instance.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        instance.setSize(600, 260);
        instance.setVisible(true);
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws Exception {

        if (args.length == 3) {
            if (args[1].equals("-f")) {
                FileInputStream is = new FileInputStream(args[2]);
                props = new Properties();
                props.load(is);
            } else {
                System.out.println("Usage: java JcrontabGUI -f thefilewiththe.properties");
                System.exit(0);
            }
        } else {
            Crontab crontab = Crontab.getInstance();
            crontab.loadConfig();
            props = crontab.getConfig();
        }

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               JcrontabGUI gui = JcrontabGUI.getInstance();
               gui.createAndShowGUI();
            }
        });
    }
}
