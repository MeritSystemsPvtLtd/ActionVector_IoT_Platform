/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.watchdog;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 *
 * @author gopal
 */
public class ConfigValues implements Serializable {

    private static final Logger log = Logger.getLogger(ConfigValues.class.getName());
    protected static Properties properties = null;
    protected static Properties watchprops = null;
    protected static boolean initcfg = false;
    protected static int isleepInterval = 0;
    protected static String customerId = null;
    protected static boolean isEnabled = false;
    protected static String ResConfigAPIURL = null;
    protected static ArrayList<WatchConfigProps> watchProps = new ArrayList<WatchConfigProps>();
    protected static HashMap<String, String> hostAddrs = new HashMap<String, String>();
    protected static HashMap<String, Long> eventComparMap = new HashMap<String, Long>();
    protected static HashMap<String, Integer> upTimeMap = new HashMap<String, Integer>();

    public static HashMap<String, Integer> getUpTimeMap() {
        return upTimeMap;
    }

    public static HashMap<String, Long> getEventComparMap() {
        return eventComparMap;
    }

    public static ArrayList<WatchConfigProps> getWatchProps() {
        return watchProps;
    }

    public static Properties getWatchprops() {
        return watchprops;
    }

    public static int getIsleepInterval() {
        return isleepInterval;
    }

    public static String getCustomerId() {
        return customerId;
    }
    
    public static Boolean isEnabled(){
        return isEnabled;
    }

    public static String getResConfigURL() {
        return ResConfigAPIURL;
    }

    public static boolean initialized() {
        String tmp;
        String userHome = System.getProperty("user.home");
        String fs = System.getProperty("file.separator");
        log.info("Initializing");
        try {
            properties = new Properties();
            properties.load(new FileInputStream(userHome + fs + "WatchDogConfig" + fs + "parameters.properties"));

            //number of seconds to sleep between each watchdog event
            tmp = properties.getProperty("watchdog-sleep");
            if (tmp != null) {

                isleepInterval = Integer.parseInt(tmp) * 1000;

            } else {//default threshold is no sleep
                log.debug("watchdog-sleep property not specified - using 60 seconds");
                isleepInterval = 60 * 1000;
            }

            tmp = properties.getProperty("CustomerId");
            if (tmp != null) {
                customerId = tmp;
            } else {//default threshold is no sleep
                log.debug("CustomerId not set in properties. Setting to default:: merit");
                customerId = "merit";
            }
            tmp = properties.getProperty("enable");
            if (tmp != null) {
                if (!(tmp.equals(""))) {
                    isEnabled = Boolean.parseBoolean(tmp);
                }
            } else {
                log.debug("enable not set in properties. Setting to default:: merit");
            }

            tmp = properties.getProperty("ResConfigAPIURL");
            if (tmp != null) {
                ResConfigAPIURL = tmp;
            } else {
                log.error("ResConfigAPIURL not set in properties.");
                return false;
            }

            watchprops = new Properties();
            watchprops.load(new FileInputStream(userHome + fs + "WatchDogConfig" + fs + "watchconfig.properties"));
            initcfg = initWatchProps(watchprops);

        } catch (Exception e) {
            log.error(" ERROR while initializing*** ******************- cannot continue!!");
            e.printStackTrace();
            return false;
        }
        if (initcfg) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean initWatchProps(Properties watchprops) {

        try {
            String keyVal = null;
            String paramVal = null;
            String arparamVal[] = null;
            String AsourcePort[] = null;
            Set se = watchprops.entrySet();
            WatchConfigProps owcprops = null;
            Long curDate = new Date().getTime();
            Iterator it = se.iterator();
            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                keyVal = (String) me.getKey();
                paramVal = (String) me.getValue();
                arparamVal = paramVal.split(",");
                if (arparamVal.length > 0) {

                    for (int i = 1; i < arparamVal.length; i++) {
                        if (arparamVal[i] != null) {

                            //ArrayList of objects to store each instace of adapters
                            //which holds the adpter event properties
                            owcprops = new WatchConfigProps();
                            owcprops.setResourceName(keyVal);
                            owcprops.setResourceType(arparamVal[0]);
                            //splitting the resourceId,port/source

                            AsourcePort = arparamVal[i].split(":");
                            if (AsourcePort.length == 2) {
                                owcprops.setResourceId(AsourcePort[0]);
                                owcprops.setInstanceId(AsourcePort[0]);
                                owcprops.setInstancePort(AsourcePort[1]);

                                //populating Map for Comparing Timestamp between events
                                eventComparMap.put(keyVal + ":" + AsourcePort[0] + ":" + AsourcePort[1], curDate);

                                upTimeMap.put(keyVal + ":" + AsourcePort[0] + ":" + AsourcePort[1], 0);

                            }
                            if (owcprops != null) {
                                watchProps.add(owcprops);
                                owcprops = null;
                            }
                            AsourcePort = null;
                        }//if
                    }//for
                }
                arparamVal = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
