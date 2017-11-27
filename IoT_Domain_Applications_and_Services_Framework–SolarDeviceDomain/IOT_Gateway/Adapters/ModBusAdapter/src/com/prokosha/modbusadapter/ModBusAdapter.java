/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.modbusadapter;

import com.prokosha.modbusadapter.configuration.AdapterProperties;
import com.prokosha.modbusadapter.configuration.ModBusEventMapper;
import com.prokosha.modbusadapter.configuration.ModBusRequests;
import com.prokosha.modbusadapter.configuration.SensorMapping;
import com.prokosha.ssl.tcp.SSLClient;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import com.prokosha.emailsmsutility.EMailSMSUtility;

/**
 *
 * @author rekhas
 */
public class ModBusAdapter {

    private static final Logger logger = Logger.getLogger(ModBusAdapter.class.getName());
    private static final Logger evtLogger = Logger.getLogger("eventsLogger");
    private static String propertyFile;
    public static SSLClient FrontControllerSSLClient = null;
    public static SSLClient WatchDogEventConnector = null;
    private static long eventID;
    private static String szSubject = "ModBusAdapterError";
    private static String szBody;
    private static final String INVERTER = "Inverter";
    private static boolean sendEmail;
    private static boolean sendSMS;

    public ModBusAdapter(String propertyFileName) {
        logger.debug("Constructing  ...");
        propertyFile = propertyFileName;
    }

    public boolean initialize() {

        logger.debug("Loading ModBusAdapter properties from property file: " + propertyFile);
        try {
            if (!AdapterProperties.loadProperties(propertyFile)) {
                logger.error("*** ERROR *** Coould not load the adapter properties correctly...");
                return false;
            }
            if (!SensorMapping.loadProperties("config/sensorchannelmapping.properties")) {
                logger.error("*** ERROR *** Coould not load the SensorMapping properties correctly...");
                return false;
            }

            if (!ModBusRequests.initializeRequests(AdapterProperties.getXMLConfigFile())) {
                logger.error("*** ERROR *** Coould not initialize the adapter requests configuration correctly...");
                return false;
            }
            if (!ModBusEventMapper.initializeModBusEvents(AdapterProperties.getEventMapper())) {
                logger.error("*** ERROR *** Coould not initialize ModBusEventMapper correctly...");
                return false;
            }
            if (!ModBusEventMapper.initializeEventStaticData(AdapterProperties.getEventStaticData())) {
                logger.error("*** ERROR *** Coould not initialize EventStaticData correctly...");
                return false;
            }
            if (!ModBusEventMapper.initializeParserMapping(AdapterProperties.getParserMapper())) {
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
            logger.debug("ModBusAdapter properties, ModBusEventMapper and requests configuration loaded successfully!!");
            logger.info("No of events configured==>>" + ModBusEventMapper.getModBusEvents().size());
            return true;

        } catch (Exception ex) {
            logger.error("*** ERROR **** error while loading adapter properties and requests configuration: \n" + ex.toString());
            ex.printStackTrace();
            return false;
        }

    }

    public void mainLoop() {

        logger.debug("Entering ModBus adapter mainloop...");
        FrontControllerSSLClient = new SSLClient();
        //set up the FrontController connector to package and send CEP events
        FrontControllerSSLClient.initialize(AdapterProperties.getSSLHost(), AdapterProperties.getSSLPort(), AdapterProperties.getNewline());
        if (AdapterProperties.sendToWatchDog()) {
            WatchDogEventConnector = new SSLClient();
            WatchDogEventConnector.initialize(AdapterProperties.getWatchDogHost(), AdapterProperties.getWatchDogPort(), AdapterProperties.getNewline());
        }
        logger.debug("Connecting to FrontController....");
        if (!FrontControllerSSLClient.isServerReady()) {
            logger.error("Some error occurred while connecting to FrontController. Will retry later...");
        }

        //setup the ModBus JSON downloader
        logger.debug("Setting up the ModBusCollector...");
        int moreRetries = AdapterProperties.getRetryCount() + 1;
        //increment for at least one try

        HashMap modbusRequestsMap = ModBusRequests.getRequestsMap();
        HashMap eventSchemaMap = (HashMap) ModBusEventMapper.getModBusEvents();
        HashMap eventStaticData = ModBusEventMapper.getEventStaticData();
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
        int sendRetries = AdapterProperties.getSendRetryCount() + 1;

        boolean forever = true;
        HashMap deviceMap, dataCollMap;
        cCustomer = AdapterProperties.getcCustomerID();
        boolean gotEvent;
        boolean isSystem = false;
        HashMap evtStringMap = null;
        Long timeL;
        ArrayList dataCollections;
        ArrayList paramList;
        Class[] argsCls;
        long startTime = startTimeInMillis();
        long endTime = endTimeInMillis();

        while (forever && (moreRetries > 0)) {
            long currentTime = new Date().getTime();
            logger.info("starttime::" + startTime);
            logger.info("endtime::" + endTime);
            logger.info("currenttime::" + currentTime + "\t" + new Date().toString());
            if ((currentTime >= startTime) && (currentTime <= endTime)) {
                boolean foundCumulation = false;
                Long SystemDayEnergy = 0l;
                Long SystemYearEnergy = 0l;
                Long SystemTotalEnergy = 0l;
                Long SystemACPower = 0l;
                StringBuffer jsonBuff;
                boolean FrontControllerReady = false;

                //download the ModBus json
                logger.debug("Download ModBus JSON doc...");
                String evtName;
                Object evtStaticData;
                String evtString = "";
                eventsItr = modbusRequestsMap.keySet().iterator();
                while (eventsItr.hasNext()) {
                    evtName = (String) eventsItr.next();
                    evtStaticData = eventStaticData.get(evtName);
                    logger.info("Download json for event " + evtName);
                    deviceMap = (HashMap) modbusRequestsMap.get(evtName);
                    dataCollMap = (HashMap) eventSchemaMap.get(evtName);
                    //logger.info("dataCollMap key entries==>>" + dataCollMap.keySet().toString());
                    Iterator deviceItr = deviceMap.keySet().iterator();
                    String deviceID = null;
                    isSystem = false;

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
                                if (!ModBusJSONCollector.download(jsonBuff, deviceID, dataCollection, timePeriod)) { //error while downloading
                                    //if (!ModBusHTTPJSONCollector.download(jsonBuff, deviceID, dataCollection, timePeriod)) { //error while downloading
                                    moreRetries--; //decrement counter
                                    gotEvent = false;
                                    logger.error("*** ERROR **** Error while downloading ModBus json document.");
                                    if (moreRetries > 0) {
                                        logger.error("Will retry download again later...");
                                    } else {
                                        logger.fatal("*** FATAL ERROR *** Re-tried " + AdapterProperties.getRetryCount()
                                                + " times to reconnect to ModBus");
                                        logger.fatal("*** FATAL ERROR *** Giving up... ModBus Adapter terminating.....");
                                        return;
                                    }
                                } else if (jsonBuff.length() < 1) {
                                    logger.info("json response null, hence breaking for device " + deviceID);
                                    gotEvent = false;
                                    ModBusError.sendErrorEvent("Cannot connect to ModBus device id:" + deviceID,
                                            "Cannot connect to ModBus device id:" + deviceID);
                                    logger.debug("eventID::" + (++eventID));
                                    break;
                                } else {
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
                                    String temp = ModBusEventMapper.getParser(evtName, dataCollection);
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
                                        evtStringMap = (HashMap) jsonParserMethod.invoke(jsonParserObj, jsonBuff.toString(), paramList, timePeriod);
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
                                String szEvtString;
                                if (!isSystem) {
                                    szEvtString = szDeviceTemp + evtString;
                                    //sendEvent
                                    //logger.info("Send CEP event==>>" + szEvtString);
                                    szEvtString = szEvtString + "," + "eventID=" + (new Date().getTime() + "" + (++eventID));
                                    //if (FrontControllerReady) {
                                    szEvtString = addAvailability(szEvtString);
                                    logger.debug("eventID::" + (eventID) + "eventSize::" + szEvtString.length());
                                    evntSent = FrontControllerSSLClient.sendMessage(szEvtString);

                                    if (evntSent) {
                                        logger.info("frontcontroller event sent successfully");
                                    }
                                    //}
                                    if (!evntSent) {
                                        logger.info("frontcontroller event not sent, logging the event to failed event log");
                                        evtLogger.info(szEvtString);
                                    }

                                    //*********************************************************************************
                                    /*String szTemp = szEvtString.split("DayEnergy=")[1];
                                    SystemDayEnergy += Long.parseLong(szTemp.substring(0, szTemp.indexOf(",")));
                                    szTemp = szEvtString.split("YearEnergy=")[1];
                                    SystemYearEnergy += Long.parseLong(szTemp.substring(0, szTemp.indexOf(",")));
                                    szTemp = szEvtString.split("TotalEnergy=")[1];
                                    SystemTotalEnergy += Long.parseLong(szTemp.substring(0, szTemp.indexOf(",")));
                                    szTemp = szEvtString.split("ACPower=")[1];
                                    SystemACPower += Long.parseLong(szTemp.substring(0, szTemp.indexOf(",")));*/
                                    //*********************************************************************************
                                } else {
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

                                    /*if (foundCumulation) {
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
                                            logger.info("frontcontroller event sent successfully");
                                        }
                                        //}
                                        if (!evntSent) {
                                            logger.info("frontcontroller event not sent, logging the event to failed event log");
                                            evtLogger.info(InverterCumulationEvent);
                                        }
                                        InverterCumulationEvent = null;
                                    }*/
                                }
                            } else {
                                logger.info("JSON download failed for some data collection. Ignoring this event.");
                            }
                            evtString = null;
                        } catch (Exception e) {
                            logger.error("Error for device " + deviceID + "==>>" + e.getMessage());
                            logger.error("Send msg and e-mail" + e.toString());
                            if (sendSMS) {
                                EMailSMSUtility.sendSMS("Error for device " + deviceID + "==>>" + e.getMessage());
                            }
                            if (sendEmail) {
                                szBody = "Error for device " + deviceID + "==>>" + e.getMessage();
                                EMailSMSUtility.sendMail(szSubject, szBody);
                                szBody = null;
                            }
                            e.printStackTrace();
                        }
                    }
                    //****************************************************************************************************************************************
                    /*timeL = System.currentTimeMillis() / 1000;
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
                        logger.info("frontcontroller event sent successfully");
                    }
                    //}
                    if (!evntSent) {
                        logger.info("frontcontroller event not sent, logging the event to failed event log");
                        evtLogger.info(InverterCumulationEvent);
                    }
                    InverterCumulationEvent = null;*/
                    //****************************************************************************************************************************************
                    deviceMap = null;
                    szDeviceTemp = null;
                    dataCollMap = null;
                }
                eventsItr = null;

                //sending watchdog event 09-04-2013
                if (AdapterProperties.sendToWatchDog()) {
                    logger.debug("Connecting to WatchDog....");
                    if (!WatchDogEventConnector.isServerReady()) {
                        WatchDogEventConnector.initialize(AdapterProperties.getWatchDogHost(), AdapterProperties.getWatchDogPort(), "\n");
                    }
                    if (!WatchDogEventConnector.isServerReady()) {
                        logger.error("Some error occurred while connecting to WatchDog. Will retry later");
                    } else {
                        WatchDogEventConnector.sendMessage("type=ModBusAdapter,status=Alive");
                        logger.info("dogwatch event sent successfully");
                    }
                }
                try {
                    Thread.sleep(AdapterProperties.getSleepInterval());
                } catch (InterruptedException ex) {
                    logger.error("ModBus adapter sleep between JSON downloads interrupted. Exiting" + ex.toString());
                    logger.error("Send msg, e-mail" + ex.toString());
                    if (sendSMS) {
                        EMailSMSUtility.sendSMS("ModBus adapter sleep between JSON downloads interrupted. Exiting" + ex.toString());
                    }
                    if (sendEmail) {
                        szBody = "ModBus adapter sleep between JSON downloads interrupted. Exiting" + ex.toString();
                        EMailSMSUtility.sendMail(szSubject, szBody);
                        forever = false;
                        szBody = null;
                    }
                    ex.printStackTrace();
                }
            } else {
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
        }
    }

    private long startTimeInMillis() {
        Calendar now = Calendar.getInstance();
        String startTime = AdapterProperties.getStartTime();
        String[] data = startTime.split(":");
        int hours = Integer.parseInt(data[0]);
        int minutes = Integer.parseInt(data[1]);
        int seconds = Integer.parseInt(data[2]);
        //now.set(Calendar.HOUR, hours-12);
        now.set(Calendar.HOUR_OF_DAY, hours);
        now.set(Calendar.MINUTE, minutes);
        now.set(Calendar.SECOND, seconds);
        System.out.println(now.toString());
        return now.getTimeInMillis();
    }

    private long endTimeInMillis() {
        Calendar now = Calendar.getInstance();
        String endTime = AdapterProperties.getEndTime();
        String[] data = endTime.split(":");
        int hours = Integer.parseInt(data[0]);
        int minutes = Integer.parseInt(data[1]);
        int seconds = Integer.parseInt(data[2]);
        //now.set(Calendar.HOUR, hours-12);
        now.set(Calendar.HOUR_OF_DAY, hours);
        now.set(Calendar.MINUTE, minutes);
        now.set(Calendar.SECOND, seconds);
        System.out.println(now.toString());
        return now.getTimeInMillis();
    }

    public static void main(String[] args) {

        //setup the logger properties
        PropertyConfigurator.configure("config/logger.properties");

        //TODO: pass the property file name as a program argument
        ModBusAdapter ModBus = new ModBusAdapter("config/modbusadapter.properties");

        if (ModBus.initialize()) {
            ModBus.mainLoop();
        } else {
            logger.error("Could not start ModBus adapter properly. Exiting.....");
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
                    if (deviceStatus == 0) {
                        availability = 1;
                    } else {
                        availability = 1;
                    }
                    //szEvent += ",availability=" + availability;
                }
            }
            szEvent += ",availability=" + availability;
            return szEvent;
        } catch (Exception ex) {
            logger.error("Error in adding availabiliy" + ex.toString());
            if (sendSMS) {
                EMailSMSUtility.sendSMS("ModBus Error while adding availabiliy==>>" + ex.getMessage());
            }
            if (sendEmail) {
                szBody = "ModBus Error while adding availabiliy"
                        + ex.toString();
                EMailSMSUtility.sendMail(szSubject, szBody);
                szBody = null;
            }
            ex.printStackTrace();
        } finally {
            szStrArray = null;
        }
        return null;
    }
}
