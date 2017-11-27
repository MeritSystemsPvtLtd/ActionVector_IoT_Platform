/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.modbusadapter;

import com.prokosha.emailsmsutility.EMailSMSUtility;
import com.prokosha.modbusadapter.configuration.AdapterProperties;
import com.prokosha.ssl.tcp.SSLClient;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author naveen
 */
public class ModBusFailedEventSender {

    private static Logger logger = Logger.getLogger(ModBusFailedEventSender.class.getName());
    private static Logger evtIdLogger = Logger.getLogger("eventsIdLogger");
    private String propertyFile;
    public static SSLClient FrontControllerSSLClient = null;
    public static SSLClient WatchDogEventConnector = null;
    public static String failedevents = null;
    private static boolean sendEmail;
    private static boolean sendSMS;

    public ModBusFailedEventSender(String propertyFileName) {
        logger.debug("Constructing  ModBusAdapter of ModBusFailedEventSender...");
        this.propertyFile = propertyFileName;
    }

    public boolean initialize() {
        logger.debug("Loading ModBusAdapter properties from property file: " + propertyFile);
        try {
            if (!AdapterProperties.loadProperties(propertyFile)) {
                logger.error("*** ERROR *** Coould not load the adapter properties in ModBusFailedEventSender correctly...");
                return false;

            }
            sendEmail = AdapterProperties.getSendErrorMail();
            sendSMS = AdapterProperties.getSendErrorSMS();
            if (sendEmail) {
                if (!EMailSMSUtility.initializeEmail(AdapterProperties.getEmailIDs(), AdapterProperties.getFromEmailID(), AdapterProperties.getSMTPAddress(),
                        AdapterProperties.getSMTPPort(), AdapterProperties.getSMTPUserName(), AdapterProperties.getSMTPPassword())) {
                    logger.error("*** ERROR *** Coould not initialize MailModule correctly...");
                    return false;
                }
            }
            if (sendSMS) {
                if (!EMailSMSUtility.initializeSMS(AdapterProperties.getMobileNos(), AdapterProperties.getSMSServerURL(), AdapterProperties.getSMSServerPort())) {
                    logger.error("*** ERROR *** Coould not initialize MailModule correctly...");
                    return false;
                }
            }
            logger.debug("ModBusAdapter properties in ModBusFailedEventSender, ModBusEventMapper and requests configuration loaded successfully!!");
            return true;

        } catch (Exception ex) {
            logger.error("*** ERROR **** error while loading adapter properties and requests configuration: \n" + ex);
            ex.printStackTrace();
            return false;
        }

    }

    public void mainLoop() {

        logger.debug("Entering ModBusFailedEventSender mainloop...");
        FrontControllerSSLClient = new SSLClient();
        //set up the FrontController connector to package and send CEP events
        FrontControllerSSLClient.initialize(AdapterProperties.getSSLHost(), AdapterProperties.getSSLPort(), AdapterProperties.getNewline());

        if (AdapterProperties.sendToWatchDog()) {
            WatchDogEventConnector = new SSLClient();
            WatchDogEventConnector.initialize(AdapterProperties.getWatchDogHost(), AdapterProperties.getWatchDogPort(), AdapterProperties.getNewline());
        }

        logger.debug("Connecting to FrontController....");

        if (!FrontControllerSSLClient.isServerReady()) {
            logger.error("Some error occurred in ModBusFailedEventSender while connecting to FrontController. Will retry later...");
        }
        //reading file from adopter properties
        failedevents = AdapterProperties.getFailedEventsLog();
        FileReader failedFile = null;
        BufferedReader failedBReader = null;
        String failedFileLine = null;
        boolean evntSent = false;
        boolean found = false;
        String eventId = null;
        String[] splite = null;
        String[] spliteEquals = null;
        FileReader filedIdFile = null;
        BufferedReader failedIdBReader = null;
        String eventLine = null;
        String EvtString = null;
        String[] splitefailedFileLine=null;
        try {
            System.out.println("FailedEvents read from file==>>" + failedevents);
            //Create object of FileReader
            failedFile = new FileReader(failedevents);
            //Instantiate the BufferedReader Class
            failedBReader = new BufferedReader(failedFile);
            // Read file line by line and print on the console
            while ((failedFileLine = failedBReader.readLine()) != null) {

                System.out.println("line=====>" + failedFileLine);
                
                if(failedFileLine.contains("=")){
                //System.out.println("line contains equal");
                //splitting String to get eventId
                spliteEquals = failedFileLine.split("=");
                for (int i = 0; i < spliteEquals.length; i++) {

                    if (spliteEquals[i].contains("eventID")) {
                        splite = spliteEquals[i + 1].split(",");
                        eventId = splite[0];
                        
                    }
                }
                eventId="id:"+eventId;
                System.out.println("event id====>" + eventId);
                //reading the eventId log file
                filedIdFile = new FileReader("eventidlog/modbuseventid.log");

                failedIdBReader = new BufferedReader(filedIdFile);

                //modbuseventid.log fie must contain atleast one space or else while condition will not execute

                while ((eventLine = failedIdBReader.readLine()) != null) {
                    String[] spliteEventLine=eventLine.split(" ");
                    String spliteID=spliteEventLine[3];
                    //System.out.println("Spliteid "+spliteID);
                    
                    if (eventId.equalsIgnoreCase(spliteID)) {
                        found = true;
                        evntSent=false;
                        System.out.println("eventid found in eventIDlog file");
                        break;
                    } else {
                        //System.out.println("eventid not found in eventIDlog file");
                    }
                    
                }
                
                if (found != true) {
                    splitefailedFileLine=failedFileLine.split("stream");
                    
                    EvtString = splitefailedFileLine[1];
                    System.out.println("Sending CEP event from FailedEventSender ==>>");
                    evntSent = FrontControllerSSLClient.sendMessage(EvtString);
                    System.out.println(EvtString);
                }
                found = false;
                //System.out.println("File returnig from frontcontroller "+evntSent);
                if (evntSent) {
                    System.out.println("frontcontroller event sent successfully from ModBusFailedEventSender");
                    evtIdLogger.info(eventId);
                    System.out.println("event id writing in to eventID log file");

                }
                if (!evntSent) {
                    System.out.println("frontcontroller event not sent, logging the event to send later from ModBusFailedEventSender");

                }
            }
                //Close the buffer reader
                filedIdFile.close();
                failedIdBReader.close();

            }
            //Close the buffer reader
            failedBReader.close();
            failedFile.close();
        } catch (Exception e) {
            System.out.println("Error while reading file line by line in ModBusFailedEventSender :"
                    + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                    FrontControllerSSLClient = null;
                    WatchDogEventConnector = null;
                    failedevents = null;
                    eventLine = null;
                    EvtString = null;
                    failedFileLine = null;
                    eventId = null;
                    splite = null;
                    splitefailedFileLine=null;
                    spliteEquals = null;
                if (failedFile != null) {
                    failedFile.close();
                    failedFile = null;
                } else if (failedBReader != null) {
                    failedBReader.close();
                    failedBReader = null;
                 } else if (filedIdFile != null) {
                    filedIdFile.close();
                    filedIdFile = null;
                } else if (failedIdBReader != null) {
                    failedIdBReader.close();
                    failedIdBReader = null;
                } 
                
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e);
            }

        }

    }

    public static void main(String[] args) {

        //setup the logger properties
        PropertyConfigurator.configure("config/logger.properties");
        //TODO: pass the property file name as a program argument
        ModBusFailedEventSender modbus = new ModBusFailedEventSender("config/modbusadapter.properties");
        if (modbus.initialize()) {
            modbus.mainLoop();
        } else {
            logger.error("Could not start ModBus adapter in ModBusFailedEventSender properly. Exiting.....");
            System.exit(1);
        }
    }
}
