/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.froniusadapter;

import com.prokosha.emailsmsutility.EMailSMSUtility;
import com.prokosha.froniusadapter.configuration.AdapterProperties;
import com.prokosha.froniusadapter.configuration.SensorMapping;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author rekhas
 */
public class SensorDataParser extends FroniusJSONDocParser {

    private static Logger logger = Logger.getLogger(SensorDataParser.class.getName());
    private static String jsonData;
    private static JSONParser jsonParser = new JSONParser();
    //creating hashmap and getting hashmap object from sensor class
    private static HashMap channelMap = SensorMapping.getChannelMapping();

    public static String parseDoc(String json, ArrayList paramList, String timePeriod) {
        String evtString = "";
        String channelID = "";
        String channelValue = "";
        String channelUnit = "";
        JSONObject jsonObject = null, dataObj = null, channelObj = null, bodyObject = null;
        try {
            //logger.info("JSON==>>" + json);
            jsonData = json;
            jsonObject = (JSONObject) jsonParser.parse(jsonData);
            bodyObject = (JSONObject) jsonObject.get("Body");
            logger.info("json Body object==>>" + bodyObject);
            dataObj = (JSONObject) bodyObject.get("Data");
            //dataObj = (JSONObject) jsonObject.get("Data");
            Iterator channelsItr = dataObj.keySet().iterator();
            while (channelsItr.hasNext()) {
                channelID = channelsItr.next().toString();
                channelObj = (JSONObject) dataObj.get(channelID);
                channelValue = channelObj.get("Value").toString();
                channelUnit = channelObj.get("Unit").toString();
                //evtString += ",Channel" + channelID + "=" + channelValue + " " + channelUnit;
                evtString += "," + channelMap.get(channelID) + "=" + channelValue;
            }
            jsonObject = null;
            dataObj = null;
            bodyObject = null;
            channelID = null;
            channelObj = null;
            channelValue = null;
            channelUnit = null;
            return evtString;
        } catch (Exception e) {
            logger.error("Error in parsing json==>>" + e.toString());
            logger.error("Send error event to CEP, msg and e-mail" + e.toString());
            FroniusError.sendErrorEvent("Error in parsing Fronius JSON", e.toString());
            if (AdapterProperties.getSendErrorSMS()) {
                EMailSMSUtility.sendSMS("Error in parsing json==>>" + e.toString());
            }
            String szSubject = "FroniusAdapterError";
            if (AdapterProperties.getSendErrorMail()) {
                String szBody = "Error in parsing json==>>" + e.toString();
                EMailSMSUtility.sendMail(szSubject, szBody);
            }
            e.printStackTrace();
        } finally {
            jsonObject = null;
            dataObj = null;
            bodyObject = null;
            channelID = null;
            channelObj = null;
            channelValue = null;
            channelUnit = null;
            jsonData = null;
            evtString = null;

        }
        return null;
    }

    public static String parseDocForMinMax(String json, ArrayList paramList, String timePeriod) {
        String evtString = "";
        String channelID = "";
        String channelValue = "";
        String channelUnit = "";
        String sensorActive = "";
        String[] labelArry = {"Day", "Month", "Year", "Total"};
        String[] subLabelArry = {"Min", "Max"};
        JSONObject jsonObject, dataObj, channelObj, durationObj, aggObj, bodyObject;
        try {

            //logger.info("JSON==>>" + json);
            jsonData = json;
            jsonObject = (JSONObject) jsonParser.parse(jsonData);
            bodyObject = (JSONObject) jsonObject.get("Body");
            logger.info("json Body object==>>" + bodyObject);
            dataObj = (JSONObject) bodyObject.get("Data");
            //dataObj = (JSONObject) jsonObject.get("Data");
            Iterator channelsItr = dataObj.keySet().iterator();
            while (channelsItr.hasNext()) {
                channelID = channelsItr.next().toString();
                channelObj = (JSONObject) dataObj.get(channelID);
                sensorActive = channelObj.get("SensorActive").toString();
                evtString += "," + channelMap.get(channelID) + "Active=" + sensorActive;
                for (String labelN : labelArry) {
                    durationObj = (JSONObject) channelObj.get(labelN);
                    for (String subLableN : subLabelArry) {

                        aggObj = (JSONObject) durationObj.get(subLableN);
                        channelValue = aggObj.get("Value").toString();
                        channelUnit = aggObj.get("Unit").toString();
                        //evtString += ",Channel" + channelID + labelN + subLableN + "=" + channelValue + " " + channelUnit;
                        evtString += "," + channelMap.get(channelID) + labelN + subLableN + "=" + channelValue;
                    }
                    aggObj = null;
                    channelValue = null;
                    channelUnit = null;
                }
                durationObj = null;
            }
            channelID = null;
            channelObj = null;
            sensorActive = null;
            jsonObject = null;
            dataObj = null;
            bodyObject = null;
            jsonData = null;
            return evtString;
        } catch (Exception e) {
            logger.error("Error in parsing json==>>" + e.toString());
            logger.error("Send error event to CEP, msg and e-mail");
            FroniusError.sendErrorEvent("Error in parsing Fronius JSON", e.toString());
            if (AdapterProperties.getSendErrorSMS()) {

                EMailSMSUtility.sendSMS("Error in parsing Fronius JSON" + e.toString());
            }
            String szSubject = "FroniusAdapterError";
            if (AdapterProperties.getSendErrorMail()) {
                String szBody = "Error in parsing Fronius JSON" + e.toString();
                EMailSMSUtility.sendMail(szSubject, szBody);
            }
            e.printStackTrace();
        } finally {
            jsonObject = null;
            dataObj = null;
            bodyObject = null;
            jsonData = null;
        }
        return null;
    }
}
