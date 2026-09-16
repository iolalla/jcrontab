/**
 *  This file is part of the jcrontab package
 *  Copyright (C) 2001-2026 Israel Olalla
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
 *  iolalla@gmail.com
 *
 */
package org.jcrontab;

import org.jcrontab.log.Log;
import org.jcrontab.web.JcrontabWebServer;

import java.util.concurrent.CountDownLatch;

/**
 * Starts Jcrontab as a background service or standalone daemon.
 * Supports running with configuration files, CLI options, and built-in Web
 * Management Console.
 *
 * Usage:
 * java -jar jcrontab.jar [options] [config-file]
 *
 * Options:
 * --web [port] / -web [port] : Start built-in Web Management Console (default
 * port 8080)
 *
 * @author $Author: iolalla $
 * @version $Revision: 2.0 $
 */
public class Jcrontab {
    private static Crontab crontab = null;
    private static JcrontabWebServer webServer = null;
    private static final CountDownLatch stopLatch = new CountDownLatch(1);

	/**
	 * main method
	 * @param args String[] the params passed from the console
	 */
	public static void main(String[] args) {
        String configFile = null;
        boolean enableWeb = false;
        int webPort = 8080;
        String authUser = null;
        String authPassword = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("--web".equalsIgnoreCase(arg) || "-web".equalsIgnoreCase(arg)) {
                enableWeb = true;
                if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                    try {
                        webPort = Integer.parseInt(args[i + 1]);
                        i++;
                    } catch (NumberFormatException ignored) {
                    }
                }
            } else if ("--auth".equalsIgnoreCase(arg) || "-auth".equalsIgnoreCase(arg)) {
                if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                    String authVal = args[i + 1];
                    i++;
                    int colon = authVal.indexOf(':');
                    if (colon >= 0) {
                        authUser = authVal.substring(0, colon);
                        authPassword = authVal.substring(colon + 1);
                    } else {
                        authUser = "admin";
                        authPassword = authVal;
                    }
                }
            } else if ("--password".equalsIgnoreCase(arg) || "-password".equalsIgnoreCase(arg)) {
                if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                    authPassword = args[i + 1];
                    i++;
                }
            } else if ("--user".equalsIgnoreCase(arg) || "-user".equalsIgnoreCase(arg)) {
                if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                    authUser = args[i + 1];
                    i++;
                }
            } else if (!arg.startsWith("-")) {
                configFile = arg;
            }
        }

        crontab = Crontab.getInstance();

        try {
            ShutdownHook();
            crontab.setDaemon(false);
            if (configFile != null && !configFile.isEmpty()) {
                crontab.init(configFile);
                Log.info("Jcrontab initialized with configuration: " + configFile);
            } else {
                crontab.init();
                Log.info("Jcrontab initialized with default configuration.");
            }

            // Check if web console is requested via properties if not set via CLI
            if (!enableWeb) {
                String webProp = crontab.getProperty("org.jcrontab.web.enable");
                if ("true".equalsIgnoreCase(webProp)) {
                    enableWeb = true;
                    String portProp = crontab.getProperty("org.jcrontab.web.port");
                    if (portProp != null && !portProp.isEmpty()) {
                        try {
                            webPort = Integer.parseInt(portProp);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }

            // Check if auth credentials are provided via properties if not set via CLI
            if (authUser == null) {
                authUser = crontab.getProperty("org.jcrontab.web.auth.user");
            }
            if (authPassword == null) {
                authPassword = crontab.getProperty("org.jcrontab.web.auth.password");
            }
            String authEnabledProp = crontab.getProperty("org.jcrontab.web.auth.enabled");
            if ("false".equalsIgnoreCase(authEnabledProp) || "none".equalsIgnoreCase(authPassword)) {
                authPassword = null;
            }

            if (enableWeb) {
                webServer = new JcrontabWebServer(webPort, authUser, authPassword);
                webServer.start();
            }

            Log.info("Jcrontab service is running. Press Ctrl+C to terminate.");
        } catch (Throwable e) {
            Log.error("Error starting Jcrontab: " + e.getMessage(), e);
        }

        // Keep service alive until shutdown signal
        try {
            stopLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
	}

	/**
     * This method sets a ShutdownHook to the system
     * This traps the CTRL+C or kill signal and shuts down
     * the system cleanly.
     */ 
    public static void ShutdownHook() throws Exception {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Log.info("Shutting down Jcrontab...");
            if (webServer != null) {
                webServer.stop();
            }
            if (crontab != null) {
                crontab.uninit(200);
            }
            stopLatch.countDown();
            Log.info("Jcrontab stopped.");
        }));
    }

    public static void stop() {
        if (webServer != null) {
            webServer.stop();
        }
        if (crontab != null) {
            crontab.uninit(200);
        }
        stopLatch.countDown();
    }

    public static JcrontabWebServer getWebServer() {
        return webServer;
    }
}
