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
package jcrontabplugin;
 
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.util.Log;
import org.jcrontab.Crontab;
/**
 *  This class is the Jcrontabplugin. It runs the Crontab and prepares its 
 *  config and starts it.
 * @author $Author: iolalla $
 * @version $Revision: 1.6 $
 */
public class JcrontabPlugin extends EditPlugin {
	
	public static final String NAME = "JcrontabPlugin";

	//This variable defines the Crontab
	static private Crontab crontab = null;
    // This method is invoked from PluginManager ...
	public void start() {
		String events = jEdit.getProperty("options.jcrontabplugin.JcrontabPlugin.Properties");
        
        if (events.indexOf("{$HOME}") != -1) {
            events = generateRightProperties(events);
        }
		int iFrec = Integer.parseInt(	jEdit.getProperty("options.jcrontabplugin.JcrontabPlugin.Frequency"));
			crontab = Crontab.getInstance();
			try {
				crontab.init(events,iFrec);
				Log.log(Log.MESSAGE, JcrontabPlugin.class, 
						"Jcrontab is Working... ok");
			} catch (Exception e) {
				Log.log(Log.ERROR, JcrontabPlugin.class, e.toString());
			}
	}

    public void createOptionPanes(OptionsDialog optionsDialog) {
		OptionGroup group = new OptionGroup(NAME);
		group.addOptionPane(new jcrontabOptionPane());
        group.addOptionPane(new jcrontabEditorPane());
		optionsDialog.addOptionGroup(group);
    }

    private String generateRightProperties(String stringTo) {
		String home = System.getProperty("user.home");
		String FileSeparator = System.getProperty("file.separator");
		stringTo =  home + FileSeparator  +  ".jcrontab" +
							   FileSeparator + "jcrontab.properties";
            if (stringTo.indexOf("\\") != -1) {
                stringTo = stringTo.replace('\\','/');
            }
        jEdit.setProperty("options.jcrontabplugin.JcrontabPlugin.Properties", stringTo);
        return stringTo;
    }
}
