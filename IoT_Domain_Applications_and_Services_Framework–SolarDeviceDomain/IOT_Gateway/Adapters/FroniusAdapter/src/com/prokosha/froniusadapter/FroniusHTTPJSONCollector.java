/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.froniusadapter;

import com.prokosha.emailsmsutility.EMailSMSUtility;
import com.prokosha.froniusadapter.configuration.AdapterProperties;
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
class FroniusHTTPJSONCollector extends FroniusJSONCollector {

    private static Logger logger = Logger.getLogger(FroniusHTTPJSONCollector.class.getName());
    private static String tempURL = AdapterProperties.getFroniusURL();
    private static final String szSystemURL = "?Scope=System";
    private static final String szDeviceURL = "?Scope=Device&DeviceId=";
    private static final String szDC = "&DataCollection=";
    private static final String szTP = "&TimePeriod=";
    private static final String szSubject = "FroniusAdapterError";

    /*public static boolean download(StringBuffer xmlBuff, String deviceID, String dataCollection, String timePeriod) {
        if (!froniusConnect(xmlBuff, deviceID, dataCollection)) {
            logger.error("*** ERROR *** Connecting to fronius failed. Terminating download!!");
            return false;
        }
        return true;
    }*/
    static boolean download(StringBuffer xmlBuff, String deviceID, String dataCollectionReq) {

        logger.debug("Connecting to fronius device id: " + deviceID);
        String froniusURL;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        URL url = null;
        HttpURLConnection conn = null;

        try {
            //tempURL = AdapterProperties.getFroniusURL();
            //GetInverterRealTimeData.cgi?Scope=Device&DeviceId=0&DataCollection=CommonInverterData
            //GetInverterRealTimeData.cgi?Scope=System
            String dataCollection = dataCollectionReq.split(":")[0];
            String httpRequest = dataCollectionReq.split(":")[1];
            String timePeriod = dataCollectionReq.split(":")[2];
            if (deviceID.equalsIgnoreCase("System")) {
                //froniusURL = tempURL + dataCollectionReq + ".cgi?Scope=System";
                froniusURL = tempURL + httpRequest + szSystemURL;
            } else {
                //froniusURL = tempURL + httpRequest + ".cgi?Scope=Device&DeviceId=" + deviceID + "&DataCollection=" + dataCollection;
                froniusURL = tempURL + httpRequest + szDeviceURL + deviceID + szDC + dataCollection;
            }
            froniusURL += szTP + timePeriod;
            logger.info("Connecting to ==>>" + froniusURL);
            url = new URL(froniusURL);
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
            logger.error("*** ERROR *** Cannot connect to fronius device id: " + deviceID + ")!!\n" + ex.toString());
            logger.error("Send error event to CEP, msg and e-mail" + ex.toString());
            FroniusError.sendErrorEvent("Cannot connect to fronius device id:" + deviceID, ex.toString());
            if (AdapterProperties.getSendErrorSMS()) {
                EMailSMSUtility.sendSMS("Cannot connect to fronius device id:" + deviceID + "==>>" + ex.toString());
            }
            if (AdapterProperties.getSendErrorMail()) {
                String szBody = "Cannot connect to fronius device id:" + deviceID + "==>>" + ex.toString();
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
