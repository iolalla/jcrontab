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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * This class is an Action to Add a new CrontabEntryBean
 * @author $Author: iolalla $
 * @version $Revision: 1.2 $
 */

public class AddAction extends GenericAction {
    /**
     * This is the text that appears in the Bottom Line Controller
     * @return String The name of the Action Command
     */
    public String getActionCommand() {
        return "Add New CrontabEntryBean Action";
    }
    
    public void performAction(ActionEvent event) throws Exception {
        //
    }
}
