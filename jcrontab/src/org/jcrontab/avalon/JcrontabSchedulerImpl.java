package org.jcrontab.avalon;

/**
 *  This file is part of the jcrontab package
 *  Copyright (C) 2001-2002 Eric Pugh and Israel Olalla
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
 *  epugh@upstate.com
 *
 */

import java.io.File;
import java.util.Properties;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.jcrontab.Crontab;
import org.jcrontab.data.CrontabEntryDAO;
/**
 * <p>This class implements the JContabScheduler in the Avalon envirionment.</p>
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id: JcrontabSchedulerImpl.java,v 1.1 2003-09-30 08:03:39 dep4b Exp $
 */
public class JcrontabSchedulerImpl
    extends AbstractLogEnabled
    implements
        JcrontabScheduler,
        Configurable,
        Initializable,        
        Contextualizable,
        Disposable
{

    /** Key to lookup the config file from */
    private static final String JCRONTAB_CONFIG_FILE_KEY =
        "jcrontab-config-file";
    /** Key to lookup the shutdown time to wait in milliseconds.  Optional */
    private static final String JCRONTAB_SHUTDOWN_TTW_KEY =
        "shutdown_time_to_wait";
    /** The crontab instance */
    private Crontab crontab = null;

    /** The root of the application */
    private String applicationRoot;
    
    /** the Shutdown Time to Wait */
	private int shutdownTimeToWait;

    /** The Avalon Context */
    private Context context = null;
    /**
     * Creates a new instance.
     */
    public JcrontabSchedulerImpl()
    {

    }
    /**
     *  Gets the scheduler attribute of the JCrontabScheduler object
     *
     *@return    The scheduler value
     */
    public Crontab getCrontab()
    {
        return crontab;
    }

    /** 
     * Get the CrontabEntryDAO for looking at tasks	 
     *
     *@return    The CrontabEntryDAO.
     */
    public CrontabEntryDAO getContrabEntryDAO()
    {
        return CrontabEntryDAO.getInstance();

    }

    public void configure(Configuration conf) throws ConfigurationException
    {
        getLogger().info("JCrontabScheduler ....starting!");
        try
        {
            applicationRoot =
                (context == null)
                    ? null
                    : (String) context.get("componentAppRoot");
        }
        catch (ContextException ce)
        {
            getLogger().error("Could not load Application Root from Context");
        }

        String propertyPath =
            conf.getChild(JCRONTAB_CONFIG_FILE_KEY, false).getValue();
		shutdownTimeToWait =
					conf.getChild(JCRONTAB_SHUTDOWN_TTW_KEY, true).getValueAsInteger(0);            

        File propertyFile =
            new File(applicationRoot, propertyPath).getAbsoluteFile();
        if (!propertyFile.exists())
        {
            throw new ConfigurationException(
                "Jcrontab property file " + propertyFile + " doesn't exist.");
        }
        try
        {
            PropertiesConfiguration configuration =
                new PropertiesConfiguration(propertyFile.toString());

            // We want to set a few values in the configuration so
            // that ${variable} interpolation will work for us
            //
            // ${applicationRoot}

            configuration.setProperty(
                APPLICATION_ROOT_KEY,
                (applicationRoot == null) ? "." : applicationRoot);

            getLogger().info("Starting Crontab with path:" + propertyFile);

            Properties properties =
                ConfigurationConverter.getProperties(configuration);
            crontab = Crontab.getInstance();
            crontab.init(properties);
        }
        catch (Exception e)
        {
            throw new ConfigurationException("Could not start crontab", e);
        }
        getLogger().info("JCrontabScheduler ....started!");

    }
    /**
     * Called the first time the Service is used.
     */
    public void initialize() throws Exception
    {

    }

    public void contextualize(Context context) throws ContextException
    {
        this.context = context;
    }

    /**
     * Avalon component lifecycle method
     */
    public void dispose()
    {
		getLogger().info("JCrontabScheduler ....Shutting down in " + shutdownTimeToWait);
        crontab.uninit(shutdownTimeToWait);
    }
}