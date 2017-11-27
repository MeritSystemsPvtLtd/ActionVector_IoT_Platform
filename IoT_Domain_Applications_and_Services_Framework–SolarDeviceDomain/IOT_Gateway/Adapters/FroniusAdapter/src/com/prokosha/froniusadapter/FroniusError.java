/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.froniusadapter;

import com.prokosha.froniusadapter.configuration.AdapterProperties;
import com.prokosha.http.HTTPEventDispatcher;
import org.apache.log4j.Logger;

/**
 *
 * @author rekhas
 */
public class FroniusError {

    private static Logger logger = Logger.getLogger(FroniusError.class.getName());
    private static final String installation = AdapterProperties.getInstallationID();
    private static final String customerID = AdapterProperties.getCustomerID();
    private static String eventSendMode = AdapterProperties.getEventSendMode();
    //private static final String cCustomerID = AdapterProperties.getcCustomerID();

    public static boolean sendErrorEvent(String error, String cause) {
        String szErrorEvt = null;
        try {
            szErrorEvt = "stream=FroniusErrorEvent";
            szErrorEvt += ",CustomerID=" + customerID;
            szErrorEvt += ",HostName=Adapter-" + installation;
            szErrorEvt += ",HostAddress=Adapter-" + installation;
            szErrorEvt += ",resourceId=Adapter-" + installation;
            szErrorEvt += ",Error=" + error;
            szErrorEvt += ",Cause=" + cause;
            szErrorEvt += ",resourceType=Adapter";
            szErrorEvt += ",resourceSubType=Adapter";
            szErrorEvt += ",cCustomer=" + AdapterProperties.getcCustomerID();
            szErrorEvt += ",HostGroup=" + installation;
            szErrorEvt += ",service=" + installation;
            if (eventSendMode.equalsIgnoreCase("TCP")) {
                FroniusAdapter.FrontControllerSSLClient.sendMessage(szErrorEvt);
            } else if (eventSendMode.equalsIgnoreCase("HTTP")) {
                HTTPEventDispatcher.sendMessage(szErrorEvt);
            }
            szErrorEvt = null;
            return true;
        } catch (Exception e) {
            logger.error("Error while sending error event to CEP==>>" + e.toString());
            e.printStackTrace();
        } finally {
            szErrorEvt = null;
        }
        return false;
    }
}
