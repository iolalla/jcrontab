/**
 *  This file is part of the jcrontab package
 *  Copyright (C) 2001-2002 Israel Olalla
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
 
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.util.Log;
import org.jcrontab.Crontab;

public class jcrontabPlugin extends EditPlugin {
	
	public static final String NAME = "jcrontab";

	//This variable defines the Crontab
	static private Crontab crontab = null;
	public void start() {
		String events = jEdit.getProperty("options.jcrontab.Properties");
		int iFrec = Integer.parseInt(
						jEdit.getProperty("options.jcrontab.Frequency"));
			crontab = Crontab.getInstance();
			try {
				crontab.init(events,iFrec);
				Log.log(Log.MESSAGE, jcrontabPlugin.class, 
						"Jcrontab is Working... ok");
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

    public void createOptionPanes(OptionsDialog optionsDialog) {
			optionsDialog.addOptionPane(new jcrontabOptionPane());
    }
}
