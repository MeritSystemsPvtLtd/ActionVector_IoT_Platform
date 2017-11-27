/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.adapter.ganglia;

import com.prokosha.ssl.tcp.SSLClient;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.io.IOException;
import java.util.Collection;
import com.prokosha.ganglia.datacollector.GangliaProtocolHandler;
import com.prokosha.ganglia.dataparser.EventMapperXmlDocParser;
import com.prokosha.ganglia.dataparser.GmondXmlDocParser;
import com.prokosha.http.HTTPEventDispatcher;

/**
 *
 * @author Abraham
 */
public class GangliaAdapter {

    private static final Logger log = Logger.getLogger(GangliaAdapter.class.getName());
    private static final Logger evtLogger = Logger.getLogger("eventsLogger");
    private String propertyFile;
    private String gHost;
    private int gPort;
    private String agmondHost[];
    private String agmondPort[];
    private GangliaXmlCollectorMap gCollectorMap;
    public static SSLClient FrontControllerSSLClient = null;
    public static SSLClient WatchDogEventConnector = null;
    private static String eventSendMode;

    public GangliaAdapter(String propertyFileName) {
        log.debug("Constructing  GangliaAdapter...");
        this.propertyFile = propertyFileName;
    }

    public boolean initialize() {

        log.debug("Loading GangliaAdapter properties from property file: " + propertyFile);
        try {
            if (!AdapterProperties.loadProperties(propertyFile)) {
                log.error("*** ERROR *** Coould not load the adapter properties correctly...");
                return false;
            } else {
                agmondHost = AdapterProperties.getaRgmondHost();
                agmondPort = AdapterProperties.getaRgmondPort();
                gCollectorMap = new GangliaXmlCollectorMap(agmondHost, agmondPort);
                log.debug("GangliaAdapter properties loaded successfully!!");
                return true;
            }
        } catch (IOException ex) {
            log.error("*** ERROR **** error while loading adapter properties: \n" + ex);
            return false;
        }

    }

    public void mainLoop() {

        log.debug("Entering Ganglia adapter mainloop...");
        eventSendMode = AdapterProperties.getEventSendMode();
        if (eventSendMode.equalsIgnoreCase("TCP")) {
            FrontControllerSSLClient = new SSLClient();
            //set up the FrontController connector to package and send CEP events
            FrontControllerSSLClient.initialize(AdapterProperties.getSSLHost(), AdapterProperties.getSSLPort(), AdapterProperties.getNewline());
            log.debug("Connecting to FrontController....");
            if (!FrontControllerSSLClient.isServerReady()) {
                log.error("Some error occurred while connecting to FrontController. Will retry later...");
            }
        } else if (eventSendMode.equalsIgnoreCase("HTTP")) {
            HTTPEventDispatcher.initialize(AdapterProperties.getFrontControllerHTTPURL(), AdapterProperties.getHTTPTimeOut());
        }
        if (AdapterProperties.sendToWatchDog()) {
            WatchDogEventConnector = new SSLClient();
            WatchDogEventConnector.initialize(AdapterProperties.getWatchDogHost(), AdapterProperties.getWatchDogPort(), "\n");
        }

        //set up the Ganglia XML parser and NodeWatcher to create CEP events
        EventMapperXmlDocParser eventMapper = new EventMapperXmlDocParser();
        try {
            eventMapper.parseDocument(AdapterProperties.getEventMapper());
        } catch (Exception ex) {
            log.error("Ganglia adapter error while parsing event mapper XML. Exiting......\n" + ex);
            return;
        }

        // setup the Ganglia XML document parser
        GmondXmlDocParser gangliaParser = new GmondXmlDocParser();

        //setup the Ganglia XML downloader
        log.debug("Setting up the GangliaXmlCollector...");
        int moreRetries = AdapterProperties.getRetryCount() + 1;
        //increment for at least one try

        //makes a list<GangliaXmlCollector> here and pass array of ghost and gport
        //GangliaXmlCollector gCollector = new GangliaXmlCollector(gHost, gPort);
        StringBuffer xmlBuff = null;
        Collection<GangliaProtocolHandler> coll = gCollectorMap.getListGangliaXmlCollector().values();

        boolean forever = true;
        while (forever && (moreRetries > 0)) {
            xmlBuff = null;
            xmlBuff = new StringBuffer(AdapterProperties.getXmlBuffSize());

            //DEBUG TEST START- comment from here onwards
            //download the Ganglia XML
            log.debug("Download ganglia XML doc...");
            //iterate aloop of for(GangliaXmlCollector gCollector : list<GangliaXmlCollector>){}
            for (GangliaProtocolHandler gCollector : coll) {
                if (!gCollector.download(xmlBuff)) { //error while downloading
                    moreRetries--; //decrement counter
                    log.error("*** ERROR **** Error while downloading ganglia XML document.");
                    if (moreRetries > 0) {
                        log.error("Will retry download again later...");
                    } else {
                        log.fatal("*** FATAL ERROR *** Re-tried " + AdapterProperties.getRetryCount()
                                + " times to reconnect to Ganglia at server(" + gHost
                                + ") and port {" + gPort + ")");
                        log.fatal("*** FATAL ERROR *** Giving up... Ganglia Adapter terminating.....");
                    }
                } else { //download successfull
                    moreRetries = AdapterProperties.getRetryCount() + 1; //reset retry counter
                    //parse XML and send events to CEP
                    gangliaParser.parseDocFromBuffer(xmlBuff);

                    //sending watchdog event 09-04-2013
                    if (AdapterProperties.sendToWatchDog()) {
                        log.debug("Connecting to WatchDog....");
                        if (!WatchDogEventConnector.isServerReady()) {
                            WatchDogEventConnector.initialize(AdapterProperties.getWatchDogHost(), AdapterProperties.getWatchDogPort(), "\n");
                        }
                        if (!WatchDogEventConnector.isServerReady()) {
                            log.error("Some error occurred while connecting to WatchDog. Will retry later...");
                            System.out.println("Some error occurred while connecting to WatchDog. Will retry later...");
                        } else {
                            WatchDogEventConnector.sendMessage("type=gmetad:" + gCollector.getgHost() + ":" + gCollector.getgPort() + ",status=Alive");
                            log.info("dogwatch event sent successfully");
                            System.out.println("dogwatch event sent successfully");

                        }
                        // watchdog event sent

                    }
                }
            }
            //DEBUG TEST END- comment until here

            /*
             * TO DEBUG WITHOUT GANGLIA
             *  - comment out all lines above tagged from DEBUG TEST START to DEBUG TEST END
             *  - uncomment line below tagged DEBUG FROM FILE
             *  - execute this class
             */

 /*
             gangliaParser.parseDocFromFile("./config/gmond-xml-money.xml"); //DEBUG FROM FILE
             */
            //now sleep for sometime before next download
            try {

                //sending watchdog event message
                //set up the CEP engine connector to package and send CEP events
                /*WatchDogEventConnector.initialize();
                 log.debug("Connecting to WatchDog....");
                 if (!WatchDogEventConnector.isWatchDogReady()) {
                 log.error("Some error occurred while connecting to WatchDog. Will retry later...");
                 System.out.println("Some error occurred while connecting to WatchDog. Will retry later...");
                 } else {
                 WatchDogEventConnector.sendMessage("type=gmetad,host=" + gHost + ",status=Alive");
                 log.info("dogwatch event sent successfully");
                 System.out.println("dogwatch event sent successfully");
                
                 }*/
                xmlBuff = null; //offer the memory for garbage collection
                Thread.sleep(AdapterProperties.getSleepInterval());
            } catch (InterruptedException ex) {
                log.error("Ganglia adapter sleep between XML downloads interrupted. Exiting......\n", ex);
                forever = false;
            }
        }

    }

    public static void sendMessage(String event) {
        boolean evntSent = false;
        if (eventSendMode.equalsIgnoreCase("TCP")) {
            evntSent = FrontControllerSSLClient.sendMessage(event);
        } else if (eventSendMode.equalsIgnoreCase("HTTP")) {
            evntSent = HTTPEventDispatcher.sendMessage(event);
        }
        if (evntSent) {
            log.info("FC event sent successfully");
        }
        if (!evntSent) {
            log.info("FC event not sent, logging to failed event log");
            evtLogger.info(event);
        }
    }

    public static void main(String[] args) {

        //setup the logger properties
        PropertyConfigurator.configure("config/logger.properties");

        //TODO: pass the property file name as a program argument
        GangliaAdapter ganglia = new GangliaAdapter("config/gangliaadapter.properties");
        if (ganglia.initialize()) {
            ganglia.mainLoop();
        } else {
            log.error("Could not start Ganglia adapter properly. Exiting.....");
            System.exit(1);
        }
    }
}
