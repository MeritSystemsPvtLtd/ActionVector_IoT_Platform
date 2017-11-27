/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.froniusadapter;

import com.prokosha.froniusadapter.configuration.AdapterProperties;
import com.prokosha.froniusadapter.configuration.FroniusEventMapper;
import com.prokosha.froniusadapter.configuration.FroniusRequests;
import com.prokosha.froniusadapter.configuration.SensorMapping;
import com.prokosha.ssl.tcp.SSLClient;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import com.prokosha.emailsmsutility.EMailSMSUtility;
import com.prokosha.http.HTTPEventDispatcher;

/**
 *
 * @author rekhas
 */
public class FroniusAdapter {

    private static final Logger logger = Logger.getLogger(FroniusAdapter.class.getName());
    private static final Logger evtLogger = Logger.getLogger("eventsLogger");
    private static String propertyFile;
    public static SSLClient FrontControllerSSLClient = null;
    private static final WatchDogEventSender WatchDogEventConnector = new WatchDogEventSender();
    private static long eventID;
    private static final String SUBJECT = "FroniusAdapterError";
    private static String szBody;
    private static String eventSendMode;
    private static String froniusComProtocol;
    private static boolean sendEmail;
    private static boolean sendSMS;
    //private static final String INVERTER = "Inverter";

    public FroniusAdapter(String propertyFileName) {
        logger.debug("Constructing  ...");
        propertyFile = propertyFileName;
    }

    public boolean initialize() {

        logger.debug("Loading FroniusAdapter properties from property file: " + propertyFile);
        try {
            if (!AdapterProperties.loadProperties(propertyFile)) {
                logger.error("*** ERROR *** Coould not load the adapter properties correctly...");
                return false;
            }
            if (!SensorMapping.loadProperties("config/sensorchannelmapping.properties")) {
                logger.error("*** ERROR *** Coould not load the SensorMapping properties correctly...");
                return false;
            }

            if (!FroniusRequests.initializeRequests(AdapterProperties.getXMLConfigFile())) {
                logger.error("*** ERROR *** Coould not initialize the adapter requests configuration correctly...");
                return false;
            }
            if (!FroniusEventMapper.initializeFroniusEvents(AdapterProperties.getEventMapper())) {
                logger.error("*** ERROR *** Coould not initialize FroniusEventMapper correctly...");
                return false;
            }
            if (!FroniusEventMapper.initializeEventStaticData(AdapterProperties.getEventStaticData())) {
                logger.error("*** ERROR *** Coould not initialize EventStaticData correctly...");
                return false;
            }
            if (!FroniusEventMapper.initializeParserMapping(AdapterProperties.getParserMapper())) {
                logger.error("*** ERROR *** Coould not initialize EventStaticData correctly...");
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
            logger.debug("FroniusAdapter properties, FroniusEventMapper and requests configuration loaded successfully!!");
            logger.info("No of events configured==>>" + FroniusEventMapper.getFroniusEvents().size());
            return true;

        } catch (Exception ex) {
            logger.error("*** ERROR **** error while loading adapter properties and requests configuration: \n" + ex.toString());
            logger.error("Send msg and e-mail" + ex.toString());
            ex.printStackTrace();
            return false;
        }

    }

    public void mainLoop() {

        logger.debug("Entering Fronius adapter mainloop...");
        eventSendMode = AdapterProperties.getEventSendMode();
        froniusComProtocol = AdapterProperties.getCommunicationProtocol();
        if (eventSendMode.equalsIgnoreCase("TCP")) {
            FrontControllerSSLClient = new SSLClient();
            //set up the FrontController connector to package and send CEP events
            FrontControllerSSLClient.initialize(AdapterProperties.getSSLHost(), AdapterProperties.getSSLPort(), AdapterProperties.getNewline());
            logger.debug("Connecting to FrontController....");
            if (!FrontControllerSSLClient.isServerReady()) {
                logger.error("Some error occurred while connecting to FrontController. Will retry later...");
            }
        } else if (eventSendMode.equalsIgnoreCase("HTTP")) {
            if (!(HTTPEventDispatcher.initialize(AdapterProperties.getFrontControllerHTTPURL(), AdapterProperties.getHTTPTimeOut()))){
                logger.error("Error in initializing HTTP Event Dispatcher");
            }
        }
        //setup the Fronius JSON downloader
        logger.debug("Setting up the FroniusCollector...");
        int moreRetries = AdapterProperties.getRetryCount() + 1;
        //increment for at least one try

        HashMap froniusRequestsMap = FroniusRequests.getRequestsMap();
        HashMap eventSchemaMap = (HashMap) FroniusEventMapper.getFroniusEvents();
        HashMap eventStaticData = FroniusEventMapper.getEventStaticData();
        Iterator eventsItr;
        String customerID = AdapterProperties.getCustomerID();
        //String szSystemTemp;
        String szDeviceTemp;
        String jsonParserClsName;
        String jsonParserMethodName;
        String[] jsonParserMapping;
        Class jsonParserClass;
        Object jsonParserObj;
        Method jsonParserMethod;
        String timePeriod;
        boolean evntSent = false;
        String cumulationEvent = "InverterCumulationEvent";
        String cCustomer = null;
        String SystemID = AdapterProperties.getInstallationID();
        //int sendRetries = AdapterProperties.getSendRetryCount() + 1;

        boolean forever = true;
        HashMap deviceMap, dataCollMap;
        cCustomer = AdapterProperties.getcCustomerID();
        boolean gotEvent;
        boolean isSystem = false;
        //HashMap evtStringMap = null;
        Long timeL;
        ArrayList dataCollections;
        ArrayList paramList;
        Class[] argsCls;
        long startTime = startTimeInMillis();
        long endTime = endTimeInMillis();
        boolean dataCollected;
        String szEvtString = null;
        boolean download = false;

        while (forever && (moreRetries > 0)) {
            long currentTime = new Date().getTime();
            logger.info("starttime::" + startTime);
            logger.info("endtime::" + endTime);
            logger.info("currenttime::" + currentTime + "\t" + new Date().toString());
            if ((currentTime >= startTime) && (currentTime <= endTime)) {
                //boolean foundCumulation = false;
                Long SystemDayEnergy = 0l;
                Long SystemYearEnergy = 0l;
                Long SystemTotalEnergy = 0l;
                Long SystemACPower = 0l;
                StringBuffer jsonBuff;
                //boolean FrontControllerReady = false;

                //download the Fronius json
                logger.debug("Download Fronius JSON doc...");
                String evtName;
                Object evtStaticData;
                String evtString = "";
                eventsItr = froniusRequestsMap.keySet().iterator();
                while (eventsItr.hasNext()) {
                    evtName = (String) eventsItr.next();
                    evtStaticData = eventStaticData.get(evtName);
                    logger.info("Download json for event " + evtName);
                    deviceMap = (HashMap) froniusRequestsMap.get(evtName);
                    dataCollMap = (HashMap) eventSchemaMap.get(evtName);
                    //logger.info("dataCollMap key entries==>>" + dataCollMap.keySet().toString());
                    Iterator deviceItr = deviceMap.keySet().iterator();
                    String deviceID = null;
                    isSystem = false;

                    dataCollected = true;

                    while (deviceItr.hasNext()) {
                        evtString = "";
                        gotEvent = true;
                        try {
                            evntSent = false;
                            deviceID = (String) deviceItr.next();
                            String insDeviceID = SystemID + "-I" + deviceID;
                            //String insDeviceID = INVERTER + "-" + deviceID;
                            timeL = System.currentTimeMillis() / 1000;
                            //szSystemTemp = "stream=" + evtName + ",CustomerID=" + customerID + ",timestamp1=" + timeL + ",cCustomer="
                            //+ cCustomer;
                            szDeviceTemp = "stream=" + evtName + ",CustomerID=" + customerID + ",timestamp1=" + timeL + ",cCustomer="
                                    + cCustomer + ",HostName=" + insDeviceID + ",HostAddress=" + insDeviceID + ",resourceId=" + insDeviceID
                                    + ",IPAddress=" + insDeviceID + ",service=" + SystemID;
                            logger.info("deviceMap for device " + deviceID + " is==>>" + deviceMap.get(deviceID).toString());
                            dataCollections = (ArrayList) deviceMap.get(deviceID);
                            for (int i = 0; i < dataCollections.size(); i++) {
                                String dataCollection = (String) dataCollections.get(i);
                                timePeriod = dataCollection.split(":")[2];
                                jsonBuff = new StringBuffer();
                                //if (!FroniusModBusJSONCollector.download(jsonBuff, deviceID, dataCollection, timePeriod)) { //error while downloading
                                //if (!FroniusHTTPJSONCollector.download(jsonBuff, deviceID, dataCollection, timePeriod)) { //error while downloading

                                if (froniusComProtocol.equalsIgnoreCase("http")) {
                                    download = FroniusHTTPJSONCollector.download(jsonBuff, deviceID, dataCollection);
                                } else if (froniusComProtocol.equalsIgnoreCase("modbus")) {
                                    download = FroniusModBusJSONCollector.download(jsonBuff, deviceID, dataCollection);
                                } else {
                                    logger.error("Fronius_Communication_Protocol: " + froniusComProtocol + " not supported. Configure either HTTP or modbus");
                                    System.exit(0);
                                }
                                if (!download) { //error while downloading  
                                    //moreRetries--; //decrement counter
                                    dataCollected = false;
                                    gotEvent = false;
                                    logger.error("*** ERROR **** Error while downloading fronius json document.");
                                    logger.error("Error downloading fronius json document for device " + deviceID);
                                    logger.error("Send msg and e-mail" + "Error downloading fronius json document for device " + deviceID);
                                    if (sendSMS) {
                                        EMailSMSUtility.sendSMS("Error for device " + deviceID + "==>>" + "Error downloading fronius json document for device " + deviceID);
                                    }
                                    if (sendEmail) {
                                        szBody = "Error for device " + deviceID + "==>>" + "Error downloading fronius json document for device " + deviceID;
                                        EMailSMSUtility.sendMail(SUBJECT, szBody);
                                        szBody = null;
                                    }
                                    FroniusError.sendErrorEvent("Cannot connect to fronius device id:" + deviceID,
                                            "Cannot connect to fronius device id:" + deviceID);
                                    logger.debug("eventID::" + (++eventID));
                                    /*if (moreRetries > 0) {
                                     logger.error("Will retry download again later...");
                                     } else {
                                     logger.fatal("*** FATAL ERROR *** Re-tried " + AdapterProperties.getRetryCount()
                                     + " times to reconnect to fronius");
                                     logger.fatal("*** FATAL ERROR *** Giving up... Fronius Adapter terminating.....");
                                     return;
                                     }*/
                                } /*else if (jsonBuff.equals("null")) {
                                 logger.info("json response null, hence breaking for device " + deviceID);
                                 gotEvent = false;
                                 FroniusError.sendErrorEvent("Cannot connect to fronius device id:" + deviceID,
                                 "Cannot connect to fronius device id:" + deviceID);
                                 logger.debug("eventID::" + (++eventID));
                                 break;
                                 } else if (jsonBuff.length() < 1) {
                                 logger.info("json response null, hence breaking for device " + deviceID);
                                 gotEvent = false;
                                 FroniusError.sendErrorEvent("Cannot connect to fronius device id:" + deviceID,
                                 "Cannot connect to fronius device id:" + deviceID);
                                 logger.debug("eventID::" + (++eventID));
                                 break;
                                 }*/ else {
                                    //logger.info("modbus response==" + jsonBuff.toString());
                                    //logger.info("jsonBuff.length==" + jsonBuff.length());
                                    dataCollected = true;
                                    //download successfull
                                    moreRetries = AdapterProperties.getRetryCount() + 1; //reset retry counter
                                    logger.info("JSON download successful");
                                    //parse json and send events to CEP
                                    if (dataCollection.contains(":")) {
                                        dataCollection = dataCollection.split(":")[0];
                                    }
                                    logger.info("dataCollection==>>" + dataCollection);
                                    paramList = (ArrayList) dataCollMap.get(dataCollection);
                                    //logger.info("paramList for dataCollection " + dataCollection + " is==>>" + paramList);
                                    String temp = FroniusEventMapper.getParser(evtName, dataCollection);
                                    jsonParserMapping = temp.split("-");
                                    logger.info("ParserMapping for " + evtName + "." + dataCollection + "==>>" + temp);
                                    jsonParserClsName = jsonParserMapping[0];
                                    jsonParserMethodName = jsonParserMapping[1];
                                    jsonParserMapping = null;
                                    jsonParserClass = Class.forName(jsonParserClsName);
                                    jsonParserObj = jsonParserClass.newInstance();
                                    argsCls = new Class[3];
                                    argsCls[0] = String.class;
                                    argsCls[1] = ArrayList.class;
                                    argsCls[2] = String.class;
                                    jsonParserMethod = jsonParserClass.getDeclaredMethod(jsonParserMethodName, argsCls);

                                    if (dataCollection.equalsIgnoreCase("System")) {
                                        //evtStringMap = (HashMap) jsonParserMethod.invoke(jsonParserObj, jsonBuff.toString(), paramList, timePeriod);
                                        isSystem = true;
                                    } else {
                                        evtString += (String) jsonParserMethod.invoke(jsonParserObj, jsonBuff.toString(), paramList, timePeriod);
                                        isSystem = false;
                                    }
                                    jsonParserClass = null;
                                    jsonParserObj = null;
                                    jsonParserMethod = null;
                                    argsCls = null;
                                }
                                jsonBuff = null; //offer the memory for garbage collection
                            }
                            dataCollections = null;
                            if (evtStaticData != null) {
                                evtString += "," + evtStaticData;
                            }
                            /*while ((!FrontControllerReady) && (sendRetries > 0)) {
                             if (!FrontControllerSSLClient.isServerReady()) {
                             logger.error("Some error occurred while connecting to front controller. Will retry...");
                             sendRetries--;
                             } else {
                             FrontControllerReady = true;
                             }
                             }*/
                            if (gotEvent) {
                                if (!isSystem) {
                                    szEvtString = szDeviceTemp + evtString;
                                    //sendEvent
                                    //logger.info("Send CEP event==>>" + szEvtString);
                                    szEvtString = szEvtString + "," + "eventID=" + (new Date().getTime() + "" + (++eventID));
                                    //if (FrontControllerReady) {
                                    szEvtString = addAvailability(szEvtString);
                                    logger.debug("eventID::" + (eventID) + "eventSize::" + szEvtString.length());
                                    if (eventSendMode.equalsIgnoreCase("TCP")) {
                                        evntSent = FrontControllerSSLClient.sendMessage(szEvtString);
                                    } else if (eventSendMode.equalsIgnoreCase("HTTP")) {
                                        evntSent = HTTPEventDispatcher.sendMessage(szEvtString);
                                    }

                                    if (evntSent) {
                                        logger.info("FC event sent successfully");
                                    }
                                    //}
                                    if (!evntSent) {
                                        logger.info("FC event not sent, logging to failed event log");
                                        evtLogger.info(szEvtString);
                                    }

                                    //*********************************************************************************
                                    String szTemp = szEvtString.split("DayEnergy=")[1];
                                    SystemDayEnergy += Long.parseLong(szTemp.substring(0, szTemp.indexOf(",")));
                                    szTemp = szEvtString.split("YearEnergy=")[1];
                                    SystemYearEnergy += Long.parseLong(szTemp.substring(0, szTemp.indexOf(",")));
                                    szTemp = szEvtString.split("TotalEnergy=")[1];
                                    SystemTotalEnergy += Long.parseLong(szTemp.substring(0, szTemp.indexOf(",")));
                                    szTemp = szEvtString.split("ACPower=")[1];
                                    SystemACPower += Long.parseLong(szTemp.substring(0, szTemp.indexOf(",")));
                                    //*********************************************************************************
                                } else {/*
                                     Set deviceIDs = evtStringMap.keySet();
                                     Iterator deivceItr = deviceIDs.iterator();
                                     while (deivceItr.hasNext()) {
                                     evntSent = false;
                                     String szTempDevID = deivceItr.next().toString();
                                     szEvtString = evtStringMap.get(szTempDevID).toString();
                                     //accumulate metric values of all devices
                                     if (evtName.equals(cumulationEvent)) {
                                     foundCumulation = true;
                                     String szTemp = szEvtString.split("DayEnergy=")[1];
                                     SystemDayEnergy += Long.parseLong(szTemp.substring(0, szTemp.indexOf(",")));
                                     szTemp = szEvtString.split("YearEnergy=")[1];
                                     SystemYearEnergy += Long.parseLong(szTemp.substring(0, szTemp.indexOf(",")));
                                     szTemp = szEvtString.split("TotalEnergy=")[1];
                                     SystemTotalEnergy += Long.parseLong(szTemp.substring(0, szTemp.indexOf(",")));
                                     szTemp = szEvtString.split("ACPower=")[1];
                                     SystemACPower += Long.parseLong(szTemp.substring(0, szTemp.indexOf(",")));
                                     }
                                     }
                                     evtStaticData = null;
                                     deivceItr = null;
                                     deviceIDs = null;
                                     evtStringMap.clear();
                                     evtStringMap = null;

                                     if (foundCumulation && dataCollected) {
                                     timeL = System.currentTimeMillis() / 1000;
                                     String InverterCumulationEvent = "stream=" + "InverterCumulationEvent" + ",CustomerID=" + customerID + ",timestamp1=" + timeL
                                     + ",cCustomer=" + cCustomer + ",HostName=" + SystemID + ",HostAddress=" + SystemID + ",resourceId=" + SystemID
                                     + ",IPAddress=" + SystemID + ",service=" + SystemID + ",DayEnergy=" + SystemDayEnergy + ",YearEnergy=" + SystemYearEnergy
                                     + ",TotalEnergy=" + SystemTotalEnergy + ",ACPower=" + SystemACPower + ",resourceType=" + SystemID;
                                     InverterCumulationEvent += "," + eventStaticData.get("InverterCumulationEvent");

                                     InverterCumulationEvent = InverterCumulationEvent + "," + "eventID=" + (new Date().getTime() + "" + (++eventID));

                                     InverterCumulationEvent = addAvailability(InverterCumulationEvent);
                                     logger.debug("eventID::" + (eventID) + "eventSize::" + InverterCumulationEvent.length());
                                     evntSent = FrontControllerSSLClient.sendMessage(InverterCumulationEvent);
                                     if (evntSent) {
                                     logger.info("FC event sent successfully");
                                     }
                                     //}
                                     if (!evntSent) {
                                     logger.info("FC event not sent, logging to failed event log");
                                     evtLogger.info(InverterCumulationEvent);
                                     }
                                     InverterCumulationEvent = null;
                                     }
                                     */

                                }
                            } else {
                                logger.info("JSON download failed for some data collection. Ignoring this event.");
                            }
                            evtString = null;
                            szEvtString = null;
                        } catch (Exception e) {
                            logger.error("Error for device " + deviceID + "==>>" + e.getMessage());
                            logger.error("Send msg and e-mail" + e.toString());
                            if (sendSMS) {
                                EMailSMSUtility.sendSMS("Error for device " + deviceID + "==>>" + e.getMessage());
                            }
                            if (sendEmail) {
                                szBody = "Error for device " + deviceID + "==>>" + e.getMessage();
                                EMailSMSUtility.sendMail(SUBJECT, szBody);
                                szBody = null;
                            }
                            e.printStackTrace();
                        }
                    }
                    //****************************************************************************************************************************************
                    if (dataCollected) {
                        timeL = System.currentTimeMillis() / 1000;
                        String InverterCumulationEvent = "stream=" + cumulationEvent + ",CustomerID=" + customerID + ",timestamp1=" + timeL
                                + ",cCustomer=" + cCustomer + ",HostName=" + SystemID + ",HostAddress=" + SystemID + ",resourceId=" + SystemID
                                + ",IPAddress=" + SystemID + ",service=" + SystemID + ",DayEnergy=" + SystemDayEnergy + ",YearEnergy=" + SystemYearEnergy
                                + ",TotalEnergy=" + SystemTotalEnergy + ",ACPower=" + SystemACPower + ",resourceType=" + SystemID;
                        InverterCumulationEvent += "," + eventStaticData.get(cumulationEvent);

                        InverterCumulationEvent = InverterCumulationEvent + "," + "eventID=" + (new Date().getTime() + "" + (++eventID));

                        InverterCumulationEvent = addAvailability(InverterCumulationEvent);
                        logger.debug("eventID::" + (eventID) + "eventSize::" + InverterCumulationEvent.length());
                        if (eventSendMode.equalsIgnoreCase("TCP")) {
                            evntSent = FrontControllerSSLClient.sendMessage(InverterCumulationEvent);
                        } else if (eventSendMode.equalsIgnoreCase("HTTP")) {
                            evntSent = HTTPEventDispatcher.sendMessage(InverterCumulationEvent);
                        }
                        if (evntSent) {
                            logger.info("FC event sent successfully");
                        }
                        //}
                        if (!evntSent) {
                            logger.info("FC event not sent, logging to failed event log");
                            evtLogger.info(InverterCumulationEvent);
                        }
                        InverterCumulationEvent = null;
                    }
                    //****************************************************************************************************************************************
                    deviceMap = null;
                    szDeviceTemp = null;
                    dataCollMap = null;
                }
                eventsItr = null;

                /*sending watchdog event 09-04-2013
                 if (AdapterProperties.sendToWatchDog()) {                    
                 WatchDogEventConnector.sendMessage("type=InverterAdapter:" + cCustomer + ":" +  SystemID + ",status=Alive");
                 logger.info("dogwatch event sent successfully");                    
                 }*/
                if (!AdapterProperties.isCRON()) {
                    try {
                        Thread.sleep(AdapterProperties.getSleepInterval());
                    } catch (InterruptedException ex) {
                        logger.error("Fronius adapter sleep between JSON downloads interrupted. Exiting" + ex.toString());
                        logger.error("Send msg, e-mail" + ex.toString());
                        if (sendSMS) {
                            EMailSMSUtility.sendSMS("Fronius adapter sleep between JSON downloads interrupted. Exiting" + ex.toString());
                        }
                        if (sendEmail) {
                            szBody = "Fronius adapter sleep between JSON downloads interrupted. Exiting" + ex.toString();
                            EMailSMSUtility.sendMail(SUBJECT, szBody);
                            ex.printStackTrace();
                            forever = false;
                            szBody = null;
                        }
                    }
                }
            } else if (!AdapterProperties.isCRON()) {
                if (currentTime < startTime) {
                } else if (currentTime > endTime) {
                    startTime += 24 * 60 * 60 * 1000;
                    endTime += 24 * 60 * 60 * 1000;
                }
                long sleepTime = (startTime - currentTime);
                try {
                    Thread.sleep(sleepTime);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                continue;
            }
            if (AdapterProperties.isCRON()) {
                forever = false;
            }
        }
    }

    private long startTimeInMillis() {
        Calendar now = Calendar.getInstance();
        logger.info("current time in starttime::" + now.toString());
        String startTime = AdapterProperties.getStartTime();
        String[] data = startTime.split(":");
        int hours = Integer.parseInt(data[0]);
        int minutes = Integer.parseInt(data[1]);
        int seconds = Integer.parseInt(data[2]);
        //now.set(Calendar.HOUR, hours-12);
        now.set(Calendar.HOUR_OF_DAY, hours);
        now.set(Calendar.MINUTE, minutes);
        now.set(Calendar.SECOND, seconds);
        return now.getTimeInMillis();
    }

    private long endTimeInMillis() {
        Calendar now = Calendar.getInstance();
        logger.info("current time in endtime::" + now.toString());

        String endTime = AdapterProperties.getEndTime();
        String[] data = endTime.split(":");
        int hours = Integer.parseInt(data[0]);
        int minutes = Integer.parseInt(data[1]);
        int seconds = Integer.parseInt(data[2]);
        //now.set(Calendar.HOUR, hours-12);
        now.set(Calendar.HOUR_OF_DAY, hours);
        now.set(Calendar.MINUTE, minutes);
        now.set(Calendar.SECOND, seconds);
        return now.getTimeInMillis();
    }

    public static void main(String[] args) {

        //setup the logger properties
        PropertyConfigurator.configure("config/logger.properties");

        //TODO: pass the property file name as a program argument
        FroniusAdapter fronius = new FroniusAdapter("config/froniusadapter.properties");

        if (fronius.initialize()) {

            if (AdapterProperties.sendToWatchDog()) {
                WatchDogEventConnector.start();
            }
            fronius.mainLoop();
            logger.info("Exited loop, terminate watchdog thread");
            WatchDogEventConnector.interrupt();
            System.exit(0);
        } else {
            logger.error("Could not start Fronius adapter properly. Exiting.....");
            System.exit(1);
        }

    }

    private String addAvailability(String szEvent) {
        int availability = 1;
        String[] szStrArray;
        try {
            szStrArray = szEvent.split("DeviceStatus=");

            if (szStrArray != null) {
                if (szStrArray.length > 1) {
                    int deviceStatus = Integer.parseInt(szStrArray[1].split(",")[0]);
                    if (deviceStatus == 7) {
                        availability = 1;
                    } else {
                        availability = 0;
                    }
                    //szEvent += ",availability=" + availability;
                }
            }
            szEvent += ",availability=" + availability;
            return szEvent;
        } catch (Exception ex) {
            logger.error("Error in adding availabiliy" + ex.toString());
            if (sendSMS) {
                EMailSMSUtility.sendSMS("Fronius Error while adding availabiliy==>>" + ex.getMessage());
            }
            if (sendEmail) {
                szBody = "Fronius Error while adding availabiliy"
                        + ex.toString();
                EMailSMSUtility.sendMail(SUBJECT, szBody);
                ex.printStackTrace();
                szBody = null;
            }
        } finally {
            szStrArray = null;
        }
        return null;
    }
}
