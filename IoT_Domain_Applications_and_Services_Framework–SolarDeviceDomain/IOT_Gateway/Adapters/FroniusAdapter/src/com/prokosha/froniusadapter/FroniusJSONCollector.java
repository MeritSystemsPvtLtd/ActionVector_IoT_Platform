/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.froniusadapter;

import org.apache.log4j.Logger;

/**
 *
 * @author rekhas
 */
public class FroniusJSONCollector {

    private static Logger logger = Logger.getLogger(FroniusJSONCollector.class.getName());

    public static boolean download(StringBuffer xmlBuff, String deviceID, String dataCollection, String timePeriod) {
        if (!froniusConnect(xmlBuff, deviceID, dataCollection)) {
            logger.error("*** ERROR *** Connecting to fronius failed. Terminating download!!");
            return false;
        }
        return true;
    }
    
    private static boolean froniusConnect(StringBuffer xmlBuff, String deviceID, String dataCollectionReq) {
        logger.error("froniusConnect() to be overridden in sub class.");
        return false;
    }
}
