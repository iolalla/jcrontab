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
import org.jcrontab.Crontab;
import org.jcrontab.log.Log;
import javax.swing.WindowConstants;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.util.Map;
import java.util.Collections;
import java.util.Iterator;
import java.util.HashMap;

/** 
 * This class is the aim of the the Jcrontab swing gui. Nobody should extend
 * this class, basically is the end of the chain
 * @author $Author: iolalla $
 * @version $Revision: 1.6 $
 */
public final class JcrontabGUI extends JFrame {
    
    /**
     * This variable is here to grant that there's only one instance of this
     * class. Singleton Pattern
     */
    private static JcrontabGUI instance = null;

    private static JTabbedPane tabbedPane = null;
    
    private static String config = null;
     /** 
      * This Map holds the list of the Listeners of the Changes
      */
    private static Map listeners = Collections.synchronizedMap(new HashMap());
    /**
     * This Constructor is private to grant that there's only one instance 
     * of this class. Singleton Pattern.
     */
    private JcrontabGUI() {
        super("Jcrontab Editor");
    }
    public static JcrontabGUI getInstance() {
        if (instance == null) instance = new JcrontabGUI();
        return instance;
    }    
    /**
     * This method is here to get the Properties given by the user
     * @return Properties The properties of Jcrontab
     */
    public Properties getConfig()  {
        return  Crontab.getInstance().getConfig();
    }
     /**
     * This method is here to set the Properties given by the user. 
     * This method very important cause changes the configuration in the gui
     * and in  the Jcrontab
     * @param String the file to load the Properties
     */
    public void setConfig(String file) throws Exception {
        config = file;
        Crontab crontab = Crontab.getInstance();
        crontab.setConfig(config);
        crontab.loadConfig();
    }
	/**
	 *	This method Stores in the properties File the given property and all the
	 *  "live" properties
	 *	@param name
	 *  @param value
	 */
	 public void storeProperty(String name, String value) {
            Crontab.getInstance().storeProperty(name, value);
	}
    /**
	 *	This method removes the given property
	 *	@param name
	 */
	 public void removeProperty(String name) {
            Crontab.getInstance().removeProperty(name);
	}
    /**
     * Creates The default MenuBar
     * @return JMenuBar The App Menu bar
     */
    public JMenuBar createMenuBar() {
        MenuController menucontroller = new MenuController();
        return menucontroller.createMenuBar();
    }
    /**
     * This method creates the Tabbed Panels Config, Crontab
     * @return JPanel The JPanel with the default Tabs
     */
    public JPanel createTabbedPanel() {
        TabController tabController = new TabController();
        tabbedPane = tabController.getTAbbedPanel();
        return tabController.getPanel();
    }
    /**
     * This method creates the Bottom Line Panel
     * @return JPanel The JPanel with the default Tabs
     */
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
        instance.getContentPane().add( createTabbedPanel(), 
                                   BorderLayout.CENTER);
        instance.getContentPane().add(createBottomPanel(), 
                                   BorderLayout.SOUTH);
        //Display the window.
        instance.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //This gets the size of the screen
        Dimension screen = getToolkit().getScreenSize();
        instance.setSize(screen.width, screen.height);
        instance.setVisible(true);
        
    }
    /**
     * Returns the number of the selected Panel
     * @return int the number of the selected Panel
     */
    public int getSelectedPane() {
        return tabbedPane.getSelectedIndex();
    }    
    /**
     * Sets the number of the selected Panel
     * @param int the number of the selected Panel
     */
    public void setSelectedPane(int selected) {
        tabbedPane.setSelectedIndex(selected);
    }
    /**
     * This method receives the different listeners of the App
     * @param Listener
     */
     public void addListener(Object listener) {
         listeners.put(listener, listener);
         Log.debug("Added new Listener");
     }
     /**
     * This method receives the different listeners of the App
     * @param Event the event to be processed
     */
     public void notify(Event event) {
         Log.debug("Processing new Event " + event.getCommand());
        Iterator iter = listeners.values().iterator();
        while (iter.hasNext()) {
            Listener listener = (Listener)iter.next();
            listener.processEvent(event);
        }
     }
    /**
     * No comments :-)
     */
    public static void main(String args[]) throws Exception {

        if (args.length == 2) {
            if (args[0].equals("-f")) {
                    config = args[1];
            } else {
                System.out.println("Usage: java JcrontabGUI -f thefilewiththe.properties");
                System.exit(0);
            }
        }
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               JcrontabGUI gui = JcrontabGUI.getInstance();
               gui.createAndShowGUI();
            }
        });
    }
}
