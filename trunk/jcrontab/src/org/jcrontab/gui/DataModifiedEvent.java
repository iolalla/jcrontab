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


/**
 * This class is an Interface of the different Events of the JcrontabGUI
 * @author $Author: iolalla $
 * @version $Revision: 1.2 $
 */

public class DataModifiedEvent implements Event {
    
    final static String ALL = "ALL";
    
    final static String CONFIG = "CONFIG";
    
    final static String DATA = "DATA";
    
    private String command;
    
    private Object source;
    
    public DataModifiedEvent(String command, Object source) {
        this.command = command;
        this.source = source;
    }
    
    public String getCommand() {
        return command;
    }
    
    public Object getSource() {
        return source;
    }
}
