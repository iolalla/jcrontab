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
 * This class is the generic Action to quit the app
 * @author $Author: iolalla $
 * @version $Revision: 1.3 $
 */

public abstract class GenericAction  implements ActionListener {
    
    public abstract void performAction(ActionEvent event) throws Exception;
    
    public abstract String getActionCommand();
    
    public void actionPerformed(ActionEvent event) {
        try {
            Log.debug("Swing gui action called " + getActionCommand());
            BottomController.getInstance().setText(getActionCommand());
            performAction(event);
        } catch (Exception e) {
            BottomController.getInstance().setError(e.toString());
            Log.error("Error", e);
        }
    }
}
