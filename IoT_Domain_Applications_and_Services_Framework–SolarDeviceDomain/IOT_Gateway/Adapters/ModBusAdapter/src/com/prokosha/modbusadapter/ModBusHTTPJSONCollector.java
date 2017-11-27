/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.modbusadapter;

import com.prokosha.emailsmsutility.EMailSMSUtility;
import com.prokosha.modbusadapter.configuration.AdapterProperties;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.log4j.Logger;

/**
 *
 * @author rekhas
 */
class ModBusHTTPJSONCollector {

    private static Logger logger = Logger.getLogger(ModBusHTTPJSONCollector.class.getName());
    private static String tempURL = AdapterProperties.getModBusURL();
    private static final String szSystemURL = "?Scope=System";
    private static final String szDeviceURL = "?Scope=Device&DeviceId=";
    private static final String szDC = "&DataCollection=";
    private static final String szTP = "&TimePeriod=";
    private static final String szSubject = "ModBusAdapterError";

    public static boolean download(StringBuffer xmlBuff, String deviceID, String dataCollection, String timePeriod) {
        if (!modbusConnect(xmlBuff, deviceID, dataCollection)) {
            logger.error("*** ERROR *** Connecting to modbus failed. Terminating download!!");
            return false;
        }
        return true;
    }

    private static boolean modbusConnect(StringBuffer xmlBuff, String deviceID, String dataCollectionReq) {

        logger.debug("Connecting to modbus device id: " + deviceID);
        String modbusURL;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        URL url = null;
        HttpURLConnection conn = null;

        try {
            //tempURL = AdapterProperties.getModBusURL();
            //GetInverterRealTimeData.cgi?Scope=Device&DeviceId=0&DataCollection=CommonInverterData
            //GetInverterRealTimeData.cgi?Scope=System
            String dataCollection = dataCollectionReq.split(":")[0];
            String httpRequest = dataCollectionReq.split(":")[1];
            String timePeriod = dataCollectionReq.split(":")[2];
            if (deviceID.equalsIgnoreCase("System")) {
                //modbusURL = tempURL + dataCollectionReq + ".cgi?Scope=System";
                modbusURL = tempURL + httpRequest + szSystemURL;
            } else {
                //modbusURL = tempURL + httpRequest + ".cgi?Scope=Device&DeviceId=" + deviceID + "&DataCollection=" + dataCollection;
                modbusURL = tempURL + httpRequest + szDeviceURL + deviceID + szDC + dataCollection;
            }
            modbusURL += szTP + timePeriod;
            logger.info("Connecting to ==>>" + modbusURL);
            url = new URL(modbusURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            is = conn.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String temp = "";
            while ((temp = br.readLine()) != null) {
                if (xmlBuff == null) {
                    xmlBuff = new StringBuffer();
                }
                xmlBuff.append(temp);
            }
            conn.disconnect();
            logger.info("HttpResponse==>>" + xmlBuff);
            url = null;
            conn = null;
            //xmlBuff = null;
            br.close();
            isr.close();
            is.close();
            return true;
        } catch (Exception ex) {
            logger.error("*** ERROR *** Cannot connect to modbus device id: " + deviceID + ")!!\n" + ex.toString());
            logger.error("Send error event to CEP, msg and e-mail" + ex.toString());
            ModBusError.sendErrorEvent("Cannot connect to modbus device id:" + deviceID, ex.toString());
            if (AdapterProperties.getSendErrorSMS()) {
                EMailSMSUtility.sendSMS("Cannot connect to modbus device id:" + deviceID + "==>>" + ex.toString());
            }
            if (AdapterProperties.getSendErrorMail()) {

                String szBody = "Cannot connect to modbus device id:" + deviceID + "==>>" + ex.toString();
                EMailSMSUtility.sendMail(szSubject, szBody);
                ex.printStackTrace();
                szBody = null;
            }
            return false;
        } finally {
            try {
                if (br != null) {
                    br.close();
                    br = null;
                }
                if (isr != null) {
                    isr.close();
                    isr = null;
                }
                if (is != null) {
                    is.close();
                    is = null;
                }
                url = null;
                conn = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
