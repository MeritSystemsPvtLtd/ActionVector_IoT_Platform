/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.modbusadapter;

import com.prokosha.emailsmsutility.EMailSMSUtility;
import com.prokosha.modbusadapter.configuration.AdapterProperties;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author rekhas
 */
public class StringControlDataParser extends ModBusJSONDocParser {

    private static Logger logger = Logger.getLogger(StringControlDataParser.class.getName());
    private static String jsonData = null;
    private static JSONParser jsonParser = new JSONParser();

    public static String parseDoc(String json, ArrayList paramList, String timePeriod) {
        String evtString = "";
        String channelID = "";
        String channelValue = "";
        String channelUnit = "";
        JSONObject jsonObject = null, channelObj = null;
        try {
            //logger.info("JSON==>>" + json);
            jsonData = json;
            jsonObject = (JSONObject) jsonParser.parse(jsonData);
            Iterator channelsItr = jsonObject.keySet().iterator();
            while (channelsItr.hasNext()) {
                channelID = channelsItr.next().toString();
                channelObj = (JSONObject) jsonObject.get(channelID);
                channelValue = channelObj.get("Value").toString();
                channelUnit = channelObj.get("Unit").toString();
                evtString += ",Channel" + channelID + "=" + channelValue + " " + channelUnit;
            }
            logger.info("timePeriod==>>" + timePeriod);
            if (timePeriod != null) {
                evtString += ",TimePeriod=" + timePeriod;
            }
            jsonObject = null;
            channelObj = null;
            channelID = null;
            channelValue = null;
            channelUnit = null;
            jsonData = null;
            return evtString;
        } catch (Exception e) {
            logger.error("Error in parsing json==>>" + e.toString());
            logger.error("Send error event to CEP, msg and e-mail" + e.toString());
            ModBusError.sendErrorEvent("Error in parsing ModBus JSON", e.toString());
            if (AdapterProperties.getSendErrorSMS()) {

                EMailSMSUtility.sendSMS("Error in parsing ModBus JSON" + e.toString());
            }
            String szSubject = "ModBusAdapterError";
            if (AdapterProperties.getSendErrorMail()) {

                String szBody = "Error in parsing ModBus JSON" + e.toString();
                EMailSMSUtility.sendMail(szSubject, szBody);
            }
            e.printStackTrace();
        } finally {
            jsonObject = null;
            channelID = null;
            channelObj = null;
            channelValue = null;
            channelUnit = null;
            jsonData = null;
            evtString = null;
        }
        return null;
    }

    public static String parseDocForLastError(String json, ArrayList paramList, String timePeriod) {
        String evtString = "";
        String channelID = "";
        String channelValue = "";
        String channelUnit = "";
        String TimeOfError = "";
        String StringAverage = "";
        JSONObject jsonObject, tmpObj, dataObj, channelObj, subObj;
        try {
            //logger.info("JSON==>>" + json);
            jsonData = json;
            jsonObject = (JSONObject) jsonParser.parse(jsonData);
            TimeOfError = jsonObject.get("TimeOfError").toString();
            evtString += ",TimeOfError=" + TimeOfError;

            tmpObj = (JSONObject) jsonObject.get("StringAverage");
            StringAverage = tmpObj.get("Value").toString();
            channelUnit = tmpObj.get("Unit").toString();
            evtString += ",StringAverage=" + StringAverage + " " + channelUnit;

            dataObj = (JSONObject) jsonObject.get("Channels");
            Iterator channelsItr = dataObj.keySet().iterator();
            while (channelsItr.hasNext()) {
                channelID = channelsItr.next().toString();
                channelObj = (JSONObject) dataObj.get(channelID);
                Object[] labelArry = channelObj.keySet().toArray();
                for (Object labelN : labelArry) {
                    subObj = (JSONObject) channelObj.get(labelN);
                    channelValue = subObj.get("Value").toString();
                    channelUnit = subObj.get("Unit").toString();
                    evtString += ",Channel" + channelID + labelN + "=" + channelValue + " " + channelUnit;
                }
                subObj = null;
                channelValue = null;
                channelUnit = null;
            }
            channelObj = null;
            channelID = null;
            tmpObj = null;
            dataObj = null;
            jsonObject = null;
            jsonData = null;
            return evtString;
        } catch (Exception e) {
            logger.error("Error in parsing json==>>" + e.toString());
            logger.error("Send error event to CEP, msg and e-mail");
            ModBusError.sendErrorEvent("Error in parsing ModBus JSON", e.toString());
            if (AdapterProperties.getSendErrorSMS()) {

                EMailSMSUtility.sendSMS("Error in parsing ModBus JSON" + e.toString());
            }
            String szSubject = "ModBusAdapterError";
            if (AdapterProperties.getSendErrorMail()) {

                String szBody = "Error in parsing ModBus JSON" + e.toString();
                EMailSMSUtility.sendMail(szSubject, szBody);
            }
            e.printStackTrace();
        } finally {
            tmpObj = null;
            dataObj = null;
            jsonObject = null;
            jsonData = null;
        }
        return null;
    }
}
