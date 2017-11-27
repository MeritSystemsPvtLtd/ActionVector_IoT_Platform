/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.modbusadapter;

import com.prokosha.emailsmsutility.EMailSMSUtility;
import com.prokosha.modbusadapter.configuration.AdapterProperties;
import org.apache.log4j.Logger;
import mbserial.SerialPortApp;
import mbserial.CommandProcessor;

/**
 *
 * @author rekhas
 */
class ModBusJSONCollector {

    private static Logger logger = Logger.getLogger(ModBusJSONCollector.class.getName());

    public static boolean download(StringBuffer xmlBuff, String deviceID, String dataCollection, String timePeriod) {
        if (!modbusConnect(xmlBuff, deviceID, dataCollection)) {
            logger.error("*** ERROR *** Connecting to modbus failed. Terminating download!!");
            return false;
        }
        return true;
    }

    private static boolean modbusConnect(StringBuffer xmlBuff, String deviceID, String dataCollectionReq) {
        if (CommandProcessor.serialPortFailureCount >= 5) {
            logger.error("**** SerialPort Communication failed continously for " + CommandProcessor.serialPortFailureCount + ", hence terminating.... ****");
            System.exit(-1);
        }
        logger.debug("Connecting to modbus device id: " + deviceID);
        SerialPortApp szSerialPort = null;
        try {
            String dataCollection = dataCollectionReq.split(":")[0];
            //String httpRequest = dataCollectionReq.split(":")[1];
            //String timePeriod = dataCollectionReq.split(":")[2];
            String scope, tempDataCollection;
            int tempDevID;
            if (deviceID.equalsIgnoreCase("System")) {
                scope = "System";
                tempDevID = 1;
                tempDataCollection = "CumulativeInverterData";
            } else {
                scope = "Device";
                tempDevID = Integer.parseInt(deviceID);
                tempDataCollection = dataCollection;
            }
            String jsonRequest = "{\"Scope\":\"" + scope + "\",\"DeviceId\":" + tempDevID + ",\"DataCollection\":\"" + tempDataCollection
                    + "\"}";
            logger.debug("jsonRequest==>>" + jsonRequest);
            szSerialPort = new SerialPortApp();
            //String resp = szSerialPort.getInverterRealTimeData(jsonRequest);
            //String resp = szSerialPort.getDeviceRealTimeData(jsonRequest);
            String resp = szSerialPort.getDeviceData(jsonRequest);
            if (xmlBuff == null) {
                xmlBuff = new StringBuffer();
            }
            xmlBuff.append(resp);
            //logger.info("ModBusResponse==>>" + xmlBuff);
            xmlBuff = null;
            szSerialPort = null;
            resp = null;
            jsonRequest = null;
            return true;
        } catch (Exception ex) {
            logger.error("*** ERROR *** Cannot connect to modbus device id: " + deviceID + ")!!\n" + ex.toString());
            logger.error("Send error event to CEP, msg and e-mail" + ex.toString());
            ModBusError.sendErrorEvent("Cannot connect to modbus device id:" + deviceID, ex.toString());
            if (AdapterProperties.getSendErrorSMS()) {

                EMailSMSUtility.sendSMS("Cannot connect to modbus device id:" + deviceID + "==>>" + ex.toString());
            }
            String szSubject = "ModBusAdapterError";
            if (AdapterProperties.getSendErrorMail()) {

                String szBody = "Cannot connect to modbus device id:" + deviceID + "==>>" + ex.toString();
                EMailSMSUtility.sendMail(szSubject, szBody);
            }
            ex.printStackTrace();
            return false;
        } finally {
            szSerialPort = null;
        }
    }
}
