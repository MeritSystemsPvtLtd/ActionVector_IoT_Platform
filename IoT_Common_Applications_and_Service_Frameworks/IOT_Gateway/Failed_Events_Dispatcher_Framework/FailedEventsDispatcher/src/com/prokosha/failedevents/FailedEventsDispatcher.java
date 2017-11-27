/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.failedevents;

import com.prokosha.emailsmsutility.EMailSMSUtility;
import com.prokosha.ssl.tcp.SSLClient;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import com.prokosha.http.HTTPEventDispatcher;
import java.io.FileInputStream;
import java.util.Properties;

/**
 *
 * @author naveen
 */
public class FailedEventsDispatcher {

    private static Logger logger = Logger.getLogger(FailedEventsDispatcher.class.getName());
    private static final Logger EVENTIDLOGGER = Logger.getLogger("eventsIdLogger");
    private final String propertyFile;
    public static SSLClient FrontControllerSSLClient = null;
    public static String failedeventsFile = null;
    private static String eventSendMode = null;
    private static boolean sendEmail;
    private static boolean sendSMS;
    private static Properties DispatcherProperties = null;

    public FailedEventsDispatcher(String propertyFileName) {
        logger.debug("Constructing  FailedEventsDispatcher...");
        this.propertyFile = propertyFileName;
    }

    public boolean initialize() {
        logger.debug("Loading FailedEventsDispatcher properties from property file: " + propertyFile);
        FileInputStream fps = null;
        try {
            DispatcherProperties = new Properties();
            fps = new FileInputStream(propertyFile);
            DispatcherProperties.load(fps);
            fps.close();
            fps = null;
            sendEmail = Boolean.parseBoolean(DispatcherProperties.getProperty("SendErrorMail"));
            sendSMS = Boolean.parseBoolean(DispatcherProperties.getProperty("SendErrorSMS"));
            if (sendEmail) {
                if (!EMailSMSUtility.initializeEmail(DispatcherProperties.getProperty("AlertEmailIDs"), 
                        DispatcherProperties.getProperty("FromEmailID"), DispatcherProperties.getProperty("SMTPAddress"),
                        Integer.parseInt(DispatcherProperties.getProperty("SMTPPort")), DispatcherProperties.getProperty("SMTPUsername"), 
                        DispatcherProperties.getProperty("SMTPPassword"))) {
                    logger.error("*** ERROR *** Could not initialize MailModule correctly...");
                    return false;
                }
            }
            if (sendSMS) {
                if (!EMailSMSUtility.initializeSMS(DispatcherProperties.getProperty("AlertMobileNos").split(","), 
                        DispatcherProperties.getProperty("SMSServerURL"),
                        Integer.parseInt(DispatcherProperties.getProperty("SMSServerPort")))) {
                    logger.error("*** ERROR *** Could not initialize SMSModule correctly...");
                    return false;
                }
            }
            logger.debug("FailedEventsDispatcher properties loaded successfully!!");
            return true;

        } catch (Exception ex) {
            logger.error("*** ERROR **** error while loading FailedEventsDispatcher properties and requests configuration: \n" + ex);
            logger.error("Send msg and e-mail");
            if (sendSMS) {
                EMailSMSUtility.sendSMS("Error while loading FailedEventsDispatcher properties and "
                        + "requests configuration in FailedEventsDispatcher " + ex.toString());
            }
            String szSubject = "FailedEventsDispatcherError";
            if (sendEmail) {
                String szBody = "Error while loading FailedEventsDispatcher properties and requests configuration"
                        + ex.toString();
                EMailSMSUtility.sendMail(szSubject, szBody);
            }

            ex.printStackTrace();
            return false;
        }

    }

    public void processFailedEvents() {

        logger.debug("Entering FailedEventSender mainloop...");
        eventSendMode = DispatcherProperties.getProperty("event_send_mode");
        if (eventSendMode.equalsIgnoreCase("TCP")) {
            FrontControllerSSLClient = new SSLClient();
            //set up the FrontController connector to package and send CEP events
            FrontControllerSSLClient.initialize(DispatcherProperties.getProperty("SSL-host"), 
                    Integer.parseInt(DispatcherProperties.getProperty("SSL-port")), 
                    DispatcherProperties.getProperty("SSL.line-separator"));

            logger.debug("Connecting to FrontController....");

            if (!FrontControllerSSLClient.isServerReady()) {
                logger.error("Some error occurred in FailedEventSender while connecting to FrontController. Will retry later...");
            }
        }
        //reading file from adopter properties
        failedeventsFile = DispatcherProperties.getProperty("failedEvents.log");
        FileReader failedFile = null;
        BufferedReader failedBReader = null;
        String failedFileLine = null;
        boolean evntSent = false;
        boolean found = false;
        String eventId = null;
        String eventLine = null;
        String EvtString = null;
        String[] splite = null;
        String[] spliteEquals = null;
        FileReader filedIdFile = null;
        BufferedReader failedIdBReader = null;
        String[] splitefailedFileLine = null;
        File folder = null;
        File[] listOfFiles = null;
        String[] spliteEventLine = null;
        String spliteID = null;
        try {
            logger.info("FailedEvents files read from Folder==>>" + failedeventsFile);
            folder = new File(failedeventsFile);
            listOfFiles = folder.listFiles();
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    logger.info("FailedEvents read from file==>>" + file.getName());
                    //Create object of FileReader
                    failedFile = new FileReader(failedeventsFile + File.separator + file.getName());
                    //Instantiate the BufferedReader Class
                    failedBReader = new BufferedReader(failedFile);
                    // Read file line by line and print on the console
                    while ((failedFileLine = failedBReader.readLine()) != null) {
                        logger.info("line=====>" + failedFileLine);
                        if (failedFileLine.contains("eventID=")) {
                            //logger.info("line contains equal");
                            //splitting String to get eventId
                            spliteEquals = failedFileLine.split("=");
                            for (int i = 0; i < spliteEquals.length; i++) {
                                if (spliteEquals[i].contains("eventID")) {
                                    splite = spliteEquals[i + 1].split(",");
                                    eventId = splite[0];
                                }
                            }
                            eventId = "id:" + eventId;
                            logger.info("event id====>" + eventId);
                            //reading the eventId log file
                            filedIdFile = new FileReader("eventidlog/failedeventid.log");
                            failedIdBReader = new BufferedReader(filedIdFile);
                            //froniuseventid.log fie must contain atleast one space or else while condition will not execute
                            while ((eventLine = failedIdBReader.readLine()) != null) {
                                spliteEventLine = eventLine.split(" ");
                                spliteID = spliteEventLine[3];
                                //logger.info("Spliteid "+spliteID);
                                if (eventId.equalsIgnoreCase(spliteID)) {
                                    found = true;
                                    evntSent = false;
                                    logger.info("eventid found in eventIDlog file");
                                    break;
                                } else {
                                    //logger.info("eventid not found in eventIDlog file");
                                }
                            }
                            if (found != true) {
                                splitefailedFileLine = failedFileLine.split("stream");
                                EvtString = splitefailedFileLine[1];
                                logger.info("Sending CEP event from FailedEventSender ==>>");
                                if (eventSendMode.equalsIgnoreCase("TCP")) {
                                    evntSent = FrontControllerSSLClient.sendMessage(EvtString);
                                } else if (eventSendMode.equalsIgnoreCase("HTTP")) {
                                    evntSent = HTTPEventDispatcher.sendMessage(EvtString);
                                }
                                logger.info(EvtString);
                            }
                            found = false;
                            //logger.info("File returnig from frontcontroller "+evntSent);
                            if (evntSent) {
                                logger.info("frontcontroller event sent successfully from FailedEventSender");
                                EVENTIDLOGGER.info(eventId);
                                logger.info("event id writing in to eventID log file");
                            }
                            if (!evntSent) {
                                logger.info("frontcontroller event not sent, logging the event to send later from FailedEventSender");
                            }
                        }
                        //Close the buffer reader
                        if (filedIdFile != null) {
                            filedIdFile.close();
                        }
                        if (failedIdBReader != null) {
                            failedIdBReader.close();
                        }
                    }
                }
            }
            //Close the buffer reader
            failedBReader.close();
            failedFile.close();
        } catch (Exception e) {
            logger.info("Error while reading file line by line in FailedEventSender :"
                    + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                FrontControllerSSLClient = null;
                failedeventsFile = null;
                eventLine = null;
                EvtString = null;
                failedFileLine = null;
                eventId = null;
                splite = null;
                splitefailedFileLine = null;
                spliteEquals = null;
                listOfFiles = null;
                spliteEventLine = null;
                spliteID = null;
                if (folder != null) {
                    folder.delete();
                    folder = null;
                }
                if (failedFile != null) {
                    failedFile.close();
                    failedFile = null;
                }
                if (failedBReader != null) {
                    failedBReader.close();
                    failedBReader = null;
                }
                if (filedIdFile != null) {
                    filedIdFile.close();
                    filedIdFile = null;
                }
                if (failedIdBReader != null) {
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
        FailedEventsDispatcher failedEventDis = new FailedEventsDispatcher("config" + File.separator + "FailedEventsDispatcher.properties");
        if (failedEventDis.initialize()) {
            failedEventDis.processFailedEvents();
        } else {
            logger.error("Could not start FailedEventSender properly. Exiting.....");
            System.exit(1);
        }
    }
}
