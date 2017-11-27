/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.adapter.ganglia;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author Abraham
 */
public class AdapterProperties {

    private static final Logger log = Logger.getLogger(AdapterProperties.class.getName());
    private static String newline = null;
    private static String eventMapper = null;
    private static int downloadSleepInterval = 0;
    private static int retryXmlDownload = 0;
    private static String loopControl = null;
    private static String sslHost = null;
    private static int sslPort = 0;
    private static int xmlBuffSize = 0;
    private static boolean enableRT = false;
    private static String customerID = null;
    private static String aRgmondHost[];
    private static String aRgmondPort[];
    //dogwatch config
    private static int watchDogPort = 0;
    private static String watchDogHost = null;
    private static String sendToWatchDog = null;
    private static String eventSendMode;
    private static String frontControllerHTTPURL;
    private static int HTTPTimeOut;

    public AdapterProperties() {
    }

    public static boolean loadProperties(String fileName) throws IOException {
        String tmp;

        log.debug("Loading properties file: " + fileName);

        Properties props = new Properties();
        FileInputStream fps = new FileInputStream(fileName);
        props.load(fps);
        fps.close();

        //ganglia.gmond-host  host address or ip
        tmp = props.getProperty("ganglia.gmond-host").trim();
        if (tmp == null || tmp.trim().length() == 0) {
            log.error("*** ERROR *** ganglia.gmond-host property not specified - cannot continue!!");
        } else {
            aRgmondHost = tmp.split(",");
        }

        //ganglia.gmond-port port no
        tmp = props.getProperty("ganglia.gmond-port").trim();
        if (tmp == null || tmp.trim().length() == 0) {
            log.error("*** ERROR *** ganglia.gmond-port property not specified - cannot continue!!");
        } else {
            aRgmondPort = tmp.split(",");
        }

        //CEP engine host address or ip
        sslHost = props.getProperty("SSL-host");
        if (sslHost == null || sslHost.trim().length() == 0) {
            log.error("*** ERROR *** SSL-host property not specified - cannot continue!!");
            return false;
        }

        //CEP engine port number
        tmp = props.getProperty("SSL-port");
        if (tmp != null) {
            sslPort = Integer.parseInt(tmp.trim());
        } else {
            log.debug("SSL-port property not specified - using 7102");
            sslPort = 7102; //default gmond port
        }

        //CEP event protocol requires a line terminator after every CEP event
        newline = props.getProperty("CEP.line-separator"); //cep socket protocol line terminator
        if (newline == null) {
            log.debug("CEP.line-seperator property not specified - using newline (\\n)");
            newline = "\n";
        }

        //number of seconds to sleep between each Ganglia XML file collection and parsing cycle
        tmp = props.getProperty("ganglia.download-sleep");
        if (tmp != null) {
            downloadSleepInterval = Integer.parseInt(tmp) * 1000; // sleep between gmond reads
        } else {//default threshold is no sleep
            log.debug("ganglia.download-sleep property not specified - using 60 seconds");
            downloadSleepInterval = 60 * 1000; //sleep for 1 minute
        }

        //Ganglia XML to CEP event mapping defintion XML
        eventMapper = props.getProperty("ganglia.cep-event-mapper");
        if (eventMapper == null || eventMapper.trim().length() == 0) {
            log.error("*** ERROR *** ganglia.cep-event-mapper property not specified - cannot continue!!");
            return false;
        }

        loopControl = props.getProperty("adapter.loop-control");
        if (loopControl == null || loopControl.trim().length() == 0) {
            log.debug("adapter.loop-control property not specified - using GANGLIA-CEP!!");
            loopControl = "GANGLIA-CEP";
        }

        tmp = props.getProperty("ganglia.retry-count");
        if (tmp != null) {
            retryXmlDownload = Integer.parseInt(tmp); // retry XML download
        } else {//default no retries
            log.debug("ganglia.retry-count property not specified - will not retry on error");
            retryXmlDownload = 0;
        }
        //the Ganglia XML document buffer intial size
        tmp = props.getProperty("ganglia.xml-buffer-size");
        if (tmp != null) {
            xmlBuffSize = Integer.parseInt(tmp) * 1024; //in KBs
        } else {
            log.debug("ganglia.xml-buffer-size property not specified - using 10Kb");
            xmlBuffSize = 10 * 1024; //default 10Kb
        }
        tmp = props.getProperty("eventRT.enable");
        if (tmp != null) {
            enableRT = Boolean.valueOf(tmp);
        } else {
            log.debug("eventRT.enable property not specified - using false");
            enableRT = false;
        }
        tmp = props.getProperty("CustomerID");
        if (tmp != null) {
            customerID = tmp;
        } else {
            log.debug("CustomerID property not specified - using null");
            customerID = null;
        }

        //watchdog properties(host, port) reading
        tmp = props.getProperty("WatchDog-port");
        if (tmp != null) {
            watchDogPort = Integer.parseInt(tmp); // watchdog port
        } else {//default no retries
            log.debug("ganglia.retry-count property not specified - will not retry on error");
            watchDogPort = 0;
        }
        watchDogHost = props.getProperty("WatchDog-host");
        if (watchDogHost == null || watchDogHost.trim().length() == 0) {
            log.error("*** ERROR *** ganglia.cep-event-mapper property not specified - cannot continue!!");
            return false;
        }
        tmp = props.getProperty("Send-WatchDog");
        if (tmp != null) {
            sendToWatchDog = tmp;
        } else {
            log.debug("ganglia.retry-count property not specified - will not retry on error");
            sendToWatchDog = "false";
        }
        //watchdog ends

        tmp = props.getProperty("event_send_mode");
        if (tmp != null) {
            eventSendMode = tmp;
        } else {
            log.error("event_send_mode property not specified");
            eventSendMode = "";
            return false;
        }

        tmp = props.getProperty("Front_Controller_HTTP_URL");
        if (tmp != null) {
            frontControllerHTTPURL = tmp;
        } else {
            log.error("Front_Controller_HTTP_URL property not specified");
            frontControllerHTTPURL = "";
            if (eventSendMode.equalsIgnoreCase("http")) {
                return false;
            }
        }

            tmp = props.getProperty("HTTP_Timeout");
            if (tmp != null) {
                HTTPTimeOut = Integer.parseInt(tmp);
            } else {
                if (eventSendMode.equalsIgnoreCase("http")) {
                    log.error("HTTP_Timeout property not specified");
                    return false;
                }
            }
        return true;
    }

    //watchdog event Setup 25-07-2013
    public static boolean sendToWatchDog() {
        if (sendToWatchDog.equalsIgnoreCase("false")) {
            return false;
        }
        return true;
    }

    public static String getNewline() {
        return newline;
    }

    public static String getSSLHost() {
        return sslHost;
    }

    public static String getEventMapper() {
        return eventMapper;
    }

    public static int getSleepInterval() {
        return downloadSleepInterval;
    }

    public static int getSSLPort() {
        return sslPort;
    }

    public static String getLoopControl() {
        return loopControl;
    }

    public static int getRetryCount() {
        return retryXmlDownload;
    }

    public static int getXmlBuffSize() {
        return xmlBuffSize;
    }

    public static boolean getEnableRT() {
        return enableRT;
    }

    public static String getCustomerID() {
        return customerID;
    }

    public static String[] getaRgmondHost() {
        return aRgmondHost;
    }

    public static String[] getaRgmondPort() {
        return aRgmondPort;
    }

    public static String getWatchDogHost() {
        return watchDogHost;
    }

    public static int getWatchDogPort() {
        return watchDogPort;
    }

    public static String getFrontControllerHTTPURL() {
        return frontControllerHTTPURL;
    }

    public static String getEventSendMode() {
        return eventSendMode;
    }
    
    public static int getHTTPTimeOut(){
        return HTTPTimeOut;
    }
}
